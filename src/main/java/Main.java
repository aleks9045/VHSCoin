import BlockChain.Transactions.Transaction;

import BlockChain.WalletGenerator.WalletGenerator;
import BlockChain.BlockChainUtils.BlockChainUtils;

import Net.Peer;
import Net.Repository.BlockchainRepository;
import User.User;

import java.security.KeyPair;


public class Main {
    public static void main(String[] args) throws Exception {
        fork1();
    }

    public static void fork1() {

        try {
            User user = new User();
            user.connect();
            user.console();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

//Genesis
//        BlockChainUtils utils = new BlockChainUtils();
//        utils.blockChain.addBlock(utils.blockChain.createGenesisBlock());
//        System.out.println(utils.blockChain.getLatestBlock().getHash());
    }

    public static void fork2() throws Exception {

        Peer peer1 = new Peer();

        peer1.listen();
//        Thread.sleep(2000);
//        peer1.sendBlockchain();
//        Thread.sleep(2000);

//        peer1.stop();
    }
}
