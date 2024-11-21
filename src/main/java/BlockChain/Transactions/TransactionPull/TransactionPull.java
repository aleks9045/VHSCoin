package BlockChain.Transactions.TransactionPull;

import BlockChain.Transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionPull {
    // Хранилище транзакций
    private List<Transaction> transactions;

    // Конструктор
    public TransactionPull() {
        this.transactions = new ArrayList<>();
    }

    // Метод для добавления транзакции
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Метод для удаления транзакции
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }

    // Метод для получения всех транзакций
    public List<Transaction> getAllTransactions() {
        return transactions;
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

