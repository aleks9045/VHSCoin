package BlockChain.Block;

import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;

import BlockChain.BlockChainUtils.BlockChainUtils;

public class Block {
    private String hash;
    private String previousHash;
    private TransactionPull data;
    private long timeStamp;
    private int difficulty;
    private int nonce;

    public Block(TransactionPull data, String previousHash, long timeStamp, int difficulty) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.difficulty = difficulty;
        this.hash = calculateHash();
    }

    public void mineBlock() throws InterruptedException {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            if (Thread.currentThread().isInterrupted()){
                throw new InterruptedException("Operation interrupted");
            }
            nonce++;
            hash = this.calculateHash();
        }
    }

    public String calculateHash() {
        StringBuilder inputBuilder = new StringBuilder();

        // Добавляем базовую информацию блока
        inputBuilder.append(previousHash)
                .append(timeStamp)
                .append(nonce);

        // Добавляем информацию из каждой транзакции
        for (Transaction transaction : data.getAllTransactions()) {
            inputBuilder.append(transaction.getSender())
                    .append(transaction.getRecipient())
                    .append(transaction.getTimeStamp())
                    .append(transaction.getAmount())
                    .append(transaction.getAccess());
        }

        return BlockChainUtils.calculateHash(inputBuilder.toString());
    }


    public void printData() {
        System.out.println("Time: " + timeStamp);
        System.out.println("Hash: " + hash);
        System.out.println("PrHash: " + previousHash);
        System.out.println("Data: " + data);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public TransactionPull getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getNonce() {
        return nonce;
    }
}
