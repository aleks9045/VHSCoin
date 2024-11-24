package Net.Repository;

import BlockChain.Transactions.TransactionPull.TransactionPull;

public class TransactionPullRepository {
    private static volatile TransactionPull transactionPull;
    private static volatile boolean actual;

    public static synchronized TransactionPull getTransactionPull() {
        return transactionPull;
    }

    public static synchronized void setTransactionPull(TransactionPull newTransactionPull) {
        transactionPull = newTransactionPull;
    }

    public static boolean isActual() {
        return actual;
    }
    public static void setActual(boolean actual) {
        TransactionPullRepository.actual = actual;
    }
}
