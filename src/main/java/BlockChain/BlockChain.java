package BlockChain;

import BlockChain.Block.Block;
import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;

import java.util.ArrayList;
import java.util.List;

public class BlockChain {
    private List<Block> chain;

    // Конструктор
    public BlockChain() {
        this.chain = new ArrayList<>();
    }

    // Метод для создания генезис-блока
    public Block createGenesisBlock() {
        TransactionPull pull = new TransactionPull();
        return new Block(pull, "0", 0, 0);
    }

    // Метод для получения последнего блока
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public ArrayList<Block> getChain() {
        return (ArrayList<Block>) chain;
    }

    // Метод для добавления нового блока
    public void addBlock(Block block) {
        chain.add(block);
    }

    // Метод для проверки целостности блокчейна
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Проверка хеша блока
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current block hash is invalid at index " + i);
                return false;
            }

            // Проверка предыдущего хеша
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Previous block hash is invalid at index " + i);
                return false;
            }
        }
        return true;
    }

    public TransactionPull getUserTransactions(String publicKey) {
        TransactionPull userTransactions = new TransactionPull();

        for (Block block : chain) {
            TransactionPull pull = block.getData();
            for (Transaction transaction : pull.getAllTransactions()) {
                if (publicKey.equals(transaction.getSender()) || publicKey.equals(transaction.getRecipient())) {
                    userTransactions.getAllTransactions().add(transaction);
                }
            }
        }

        // Упорядочиваем транзакции по времени
        userTransactions.getAllTransactions().sort((t1, t2) -> Long.compare(t1.getTimeStamp(), t2.getTimeStamp()));

        return userTransactions;
    }

    public static long calculateBalance(String publicKey, TransactionPull transactions) {
        long balance = 0;

        for (Transaction transaction : transactions.getAllTransactions()) {
            if (publicKey.equals(transaction.getRecipient())) {
                balance += transaction.getAmount();
            }
            if (publicKey.equals(transaction.getSender())) {
                balance -= transaction.getAmount();
            }
        }

        return balance;
    }

    // Метод для вывода всех блоков
    public void printChain() {
        for (Block block : chain) {
            System.out.println(block);
        }
    }
}
