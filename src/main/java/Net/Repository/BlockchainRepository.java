package Net.Repository;

import BlockChain.BlockChain;

public class BlockchainRepository {
    private static volatile BlockChain blockChain;

    public static synchronized BlockChain getBlockChain() {
        return blockChain;
    }

    public static synchronized void setBlockChain(BlockChain newBlockChain) {
        System.out.println("Set blockchain in repository");
        blockChain = newBlockChain;
    }
}
