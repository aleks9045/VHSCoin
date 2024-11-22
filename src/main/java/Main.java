import BlockChain.Transactions.Transaction;

import BlockChain.WalletGenerator.WalletGenerator;
import BlockChain.BlockChainUtils.BlockChainUtils;

import Net.Peer;

import java.security.KeyPair;


public class Main {
    public static void main(String[] args) throws Exception {
        fork2();
    }

    public static void fork1() {

        WalletGenerator walletGen = new WalletGenerator();
        try {
            KeyPair wallet = walletGen.generateWallet();
            String publicKey = walletGen.encodeKeyToBase64(wallet.getPublic().getEncoded());
            String privateKey = walletGen.encodeKeyToBase64(wallet.getPrivate().getEncoded());
            BlockChainUtils utils = new BlockChainUtils();
            Transaction t1 = new Transaction(publicKey, "heth28y8923hjfh3", 20, 2, privateKey);

            String access = utils.decryptWithPublicKey(t1.getAccess(), publicKey);
            String res = utils.calculateHash(t1.getFeilds());
            boolean a = (res.equals(access));
            System.out.println(a);


        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
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
