package BlockChain.Transaction;

public class Transaction {
    private String sender;
    private String recipient;
    private long timeStamp;
    private Integer amount;

    public Transaction(String sender, String recipient, Integer amount, long timeStamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.timeStamp = timeStamp;
        this.amount = amount;
    }
}