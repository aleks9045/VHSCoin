package BlockChain.Transactions;

import BlockChain.BlockChainUtils.BlockChainUtils;

public class Transaction {
    private String sender;
    private String recipient;
    private long timeStamp;
    private long amount;
    private String access;

    public Transaction(String sender, String recipient, long amount, long timeStamp, String access){
        this.sender = sender;
        this.recipient = recipient;
        this.timeStamp = timeStamp;
        this.amount = amount;
        this.access = access;
    }

    public void setAccess(String privateKey) {
        try {
            BlockChainUtils utils = new BlockChainUtils();
            String hash = BlockChainUtils.calculateHash(this.getFeilds());
            access = BlockChainUtils.encryptWithPrivateKey(hash, privateKey);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFeilds() {
        return sender + recipient + timeStamp + amount;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getAmount() {
        return amount;
    }

    public String getAccess() {
        return access;
    }

}