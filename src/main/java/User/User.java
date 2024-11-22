package User;

import BlockChain.Block.Block;
import BlockChain.BlockChainUtils.BlockChainUtils;
import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;
import BlockChain.WalletGenerator.WalletGenerator;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.Scanner;

public class User {

    private String publicKey;
    private String privateKey;
    private BlockChainUtils utils;


    public User() {
        this.privateKey = "";
        this.publicKey = "";
        this.utils = new BlockChainUtils();
        utils.blockChain.addBlock(utils.blockChain.createGenesisBlock());
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
                break;
            case "generate":
                this.generateWallet();
                break;
            case "mine":
                this.mine();
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
                System.out.println("balance [publicKey] - Узнать баланс текущего кошелька или указанного публичного ключа");
                System.out.println("exit - Выйти из программы");
                break;

            default:
                break;
        }
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
        System.out.println("Майнер успешно запущен.");

        // Поток для выполнения майнинга
        Thread miningThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                mineBlock();
            }
        });
        miningThread.start();

        // Основной поток для слушания ввода пользователя
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                miningThread.interrupt();
                break; // Выход из цикла
            }
        }

        try {
            miningThread.join(); // Ждем завершения майнинг-потока
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Майнинг остановлен.");
    }

    private void mineBlock(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Transaction transaction = new Transaction("miner", publicKey, timestamp.getTime(), 100, "");
        transaction.setAccess(privateKey);
        TransactionPull transactionPull = new TransactionPull();
        transactionPull.addTransaction(transaction);
        Block block = new Block(transactionPull, utils.blockChain.getLatestBlock().getHash(), timestamp.getTime(), 5);
        block.mineBlock();
        utils.blockChain.addBlock(block);
        System.out.println("+1.00 VHS");
    }

    private void login(String puKey, String prKey) {

        if (BlockChainUtils.checkKeys(puKey, prKey )){
            setPrivateKey(prKey);
            setPublicKey(puKey);
            System.out.println("Ключи успешно сохранены.");
        }
        else{
            System.out.println("Ключи не прошли проверку. Попробуйте снова");
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
}
