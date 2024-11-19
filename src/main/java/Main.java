import BlockChain.Block.Block;
import BlockChain.BlockChain;
import BlockChain.Transaction.Transaction;
import BlockChain.Transaction.TransactionPull.TransactionPull;

import BlockChain.WalletGenerator.WalletGenerator;
import BlockChain.BlockChainUtils.BlockChainUtils;

import Net.Peer;
import Net.Serializers.BlockchainSerializer;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws Exception {
        fork1();
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

        Peer peer1 = new Peer("Peer1");

        peer1.start();
//        BlockchainSerializer.createGenesisBlock();


//        // Десериализация из массива байтов
//        try {
//            ProtoBlockchain deserializedBlockchain = ProtoBlockchain.parseFrom(serializedBlockchain);
//            System.out.println("skjdfhkjfghksjdfg: " + deserializedBlockchain.getBlocksCount());
//
//            // Вывод данных каждого блока
//            for (ProtoBlock block : deserializedBlockchain.getBlocksList()) {
//                System.out.println("Блок:");
//                System.out.println("  Хеш: " + block.getHash());
//                System.out.println("  Предыдущий хеш: " + block.getPreviousHash());
//                System.out.println("  Данные: " + block.getData());
//                System.out.println("  Время создания: " + block.getTimestamp());
//                System.out.println("  Сложность: " + block.getDifficulty());
//                System.out.println("  Нонс: " + block.getNonce());
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }
}
