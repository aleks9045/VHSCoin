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

    public static boolean isActual() {
        return actual;
    }
    public static void setActual(boolean actual) {
        BlockchainRepository.actual = actual;
    }
}
