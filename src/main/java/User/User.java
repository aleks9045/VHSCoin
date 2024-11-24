package User;

import BlockChain.Block.Block;
import BlockChain.BlockChain;
import BlockChain.BlockChainUtils.BlockChainUtils;
import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;
import BlockChain.WalletGenerator.WalletGenerator;
import Net.Peer;
import Net.Repository.BlockchainRepository;
import Net.Repository.TransactionPullRepository;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private final Peer peer = new Peer();
    private String publicKey;
    private String privateKey;
    private BlockChainUtils utils;
    private Long balance;


    public User() {
        this.privateKey = "";
        this.publicKey = "";
        this.balance = 0L;
    }

    public void connect(){
        peer.listen();
        peer.waitData();
        utils = new BlockChainUtils();
    }

    private void exchangeBlockchains(){
        BlockchainRepository.setBlockChain(utils.blockChain);
        peer.sendBlockchain();
        peer.waitData();
    }

    private void exchangePulls(){
        TransactionPullRepository.setTransactionPull(utils.transactionPull);
        peer.sendTransactionPull();
        peer.waitData();
    }

    public void console() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите команду (help для справки):");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            handleCommand(input);
        }

    }

    private void handleCommand(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;

        switch (command.toLowerCase()) {
            case "balance":
                getBalance();
                break;
            case "generate":
                this.generateWallet();
                break;
            case "transaction":
                this.addTransaction();
                break;
            case "mine":
                this.mine();
                break;
            case "q":
                System.out.println("Майнер не запущен");
                break;
            case "login":
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введите публичный ключ:");
                String prKey = scanner.nextLine().trim();
                System.out.println("Введите приватный ключ:");
                String puKey = scanner.nextLine().trim();

                this.login(prKey, puKey);
            case "help":
                System.out.println("Доступные команды:");
                System.out.println("generate - Генерация нового кошелька(вход происходит автоматически)");
                System.out.println("login - Вход в кошелек");
                System.out.println("balance [publicKey] - Узнать баланс текущего кошелька или указанного публичного ключа");
                System.out.println("mine - Запуск майнера");
                System.out.println("q - Остановка майнера");
                break;

            default:
                break;
        }
    }

    private int addTransaction() {
        if (!publicKey.equals("")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите данные для совершения транзакции: ");
            System.out.println("Публичный ключ получателя: ");
            String recipient = scanner.nextLine().trim();
            System.out.println("Количество монет: ");
            Long amount = formatToStorage(scanner.nextLine().trim());
            while (balance - amount < 0 || amount <= 0) {
                System.out.println("Ваш баланс: " + formatToDisplay(balance) + " VHS");
                System.out.println("Пожалуйста, введите корректную сумму перевода.");
                if (scanner.nextLine().trim().equalsIgnoreCase("cancel")) {
                    return 0;
                }
                amount = formatToStorage(scanner.nextLine().trim());
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Transaction transaction = new Transaction(publicKey, recipient, amount, timestamp.getTime(), "");
            transaction.setAccess(privateKey);
            System.out.println("Транзакция успешно добавлена в очередь");
            utils.transactionPull.addTransaction(transaction);
            exchangePulls();
        } else {
            System.out.println("Требуется аутентификация кошелька. Введите login для входа или generate для генерации нового кошелька.");
        }
        return 0;
    }

    private void getBalance() {
        System.out.println(formatToDisplay(balance) + " VHS");
    }

    private void generateWallet() {
        try {
            KeyPair wallet = WalletGenerator.generateWallet();
            String publicKey = WalletGenerator.encodeKeyToBase64(wallet.getPublic().getEncoded());
            String privateKey = WalletGenerator.encodeKeyToBase64(wallet.getPrivate().getEncoded());

            System.out.println("publicKey: " + publicKey);
            System.out.println("privateKey: " + privateKey);
            login(publicKey, privateKey);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public void mine() {
        Thread miningThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    mineBlock();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        miningThread.start();
        System.out.println("Майнер успешно запущен.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if ("q".equalsIgnoreCase(input)) {
                miningThread.interrupt();
                break;
            }
        }
        System.out.println("Майнер остановлен.");
    }

    private void mineBlock() throws InterruptedException {
        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException("Operation interrupted");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Transaction transaction = new Transaction("miner", publicKey, 100, timestamp.getTime(), "");
        transaction.setAccess(privateKey);
        TransactionPull blockTransPull = new TransactionPull();
        blockTransPull.addTransaction(transaction);

        List<Transaction> copy = new ArrayList<>(utils.transactionPull.getAllTransactions());
        for (Transaction tx : copy) {
            blockTransPull.addTransaction(tx);
            utils.transactionPull.removeTransaction(tx);
        }

        Block block = new Block(blockTransPull, utils.blockChain.getLatestBlock().getHash(), timestamp.getTime(), 5);
        block.mineBlock();

        utils.blockChain.addBlock(block);
        exchangeBlockchains();

        TransactionPull history = utils.blockChain.getUserTransactions(publicKey);
        this.balance = utils.blockChain.calculateBalance(publicKey, history);
        System.out.println("+1,00 VHS");
    }

    private void login(String puKey, String prKey) {

        if (BlockChainUtils.checkKeys(puKey, prKey)) {
            setPrivateKey(prKey);
            setPublicKey(puKey);
            System.out.println("Ключи успешно сохранены.");
        } else {
            System.out.println("Ключи не прошли проверку. Попробуйте снова try again");
        }
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public static String formatToDisplay(long amount) {
        return String.format("%.2f", amount / 100.0);
    }

    public static long formatToStorage(String amount) {
        return (long) (Double.parseDouble(amount) * 100);
    }
}
