package Blockchain;

public class Transaction {
    private String sender;
    private String recipient;
    private Integer amount;

    public Transaction(String sender, String recipient, Integer amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
}