package Net.Repository;

import BlockChain.BlockChain;

public class BlockchainRepository {
    private static volatile BlockChain blockChain;
    private static volatile boolean actual;

    public static synchronized BlockChain getBlockChain() {
        return blockChain;
    }

    public static synchronized void setBlockChain(BlockChain newBlockChain) {
        blockChain = newBlockChain;
    }

    public synchronized static boolean isActual() {
        return actual;
    }

    public synchronized static void setActual(boolean actual) {
        BlockchainRepository.actual = actual;
    }
}
