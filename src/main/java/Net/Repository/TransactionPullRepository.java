package Net.Repository;

import BlockChain.Transactions.TransactionPull.TransactionPull;

public class TransactionPullRepository {
    private static volatile TransactionPull transactionPull;

    public static synchronized TransactionPull getTransactionPull() {
        return transactionPull;
    }

    public static synchronized void setTransactionPull(TransactionPull newTransactionPull) {
        System.out.println("Set transaction pull in repository");
        transactionPull = newTransactionPull;
    }
}
