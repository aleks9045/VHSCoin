package BlockChain.Transactions.TransactionPull;

import BlockChain.Transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionPull {
    // Хранилище транзакций
    private  List<Transaction> transactions;

    // Конструктор
    public TransactionPull() {
        this.transactions = new ArrayList<>();
    }

    // Метод для добавления транзакции
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void processIncomingTransactionPull(TransactionPull transactionPull) {
        TransactionPull transactionPullCopy = transactionPull;
        for (Transaction transaction : transactionPullCopy.getAllTransactions()) {
            for (Transaction transaction2 : transactions) {
                if (transaction.equals(transaction2)) {
                    transactionPull.removeTransaction(transaction);
                }
            }
        }
        for (Transaction transaction : transactionPull.getAllTransactions()) {
            this.addTransaction(transaction);
        }
    }

    // Метод для удаления транзакции
    public void removeTransaction(Transaction transaction) {
        for (Transaction transaction2 : transactions) {
            if (transaction.equals(transaction2)) {
                displayTransactions();
                transactions.remove(transaction);
            }
        }
    }

    // Метод для получения всех транзакций
    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public void displayTransactions() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction.getSender());
            System.out.println(transaction.getRecipient());
            System.out.println(transaction.getAmount());
            System.out.println(transaction.getTimeStamp());
        }
    }

    public void setAllTransactions(List<Transaction> newTransactions) {
        transactions = newTransactions;
    }

    // Метод для вывода всех транзакций в консоль
    public void printAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions in the pull.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }
}

