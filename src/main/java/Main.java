import BlockChain.Block.Block;
import BlockChain.BlockChain;
import BlockChain.Transaction.Transaction;
import BlockChain.Transaction.TransactionPull.TransactionPull;
import BlockChain.WalletGenerator.WalletGenerator;
import BlockChain.BlockChainUtils.BlockChainUtils;

import Net.Peer;

import java.security.KeyPair;
import java.sql.Timestamp;


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
//        System.out.println(myIpNPort[0] + " " + myIpNPort[1]);
//        Peer stable_peer = new Peer("StablePeer", myIpNPort[0], Integer.parseInt(myIpNPort[1]));
//        stable_peer.start();

        Peer peer1 = new Peer("Peer1", "localhost",5001);
        Peer peer2 = new Peer("Peer2", "localhost",5002);

        peer1.start();
        peer2.start();
        Thread.sleep(50);

        peer2.sendMessageToSS("Peer1", "hello");

//        ProtoBlock block1 = ProtoBlock.newBuilder()
//                .setHash("0000000000000000")
//                .setPreviousHash("0")
//                .setData("Первый блок")
//                .setTimestamp("2024-10-06T12:00:00Z")
//                .setDifficulty(5)
//                .setNonce(12345)
//                .build();
//
//        // Создаем второй блок
//        ProtoBlock block2 = ProtoBlock.newBuilder()
//                .setHash("000000000000001")
//                .setPreviousHash(block1.getHash())
//                .setData("Второй блок")
//                .setTimestamp("2024-10-06T12:05:00Z")
//                .setDifficulty(5)
//                .setNonce(67890)
//                .build();
//
//        // Создаем блокчейн и добавляем блоки
//        List<ProtoBlock> blockList = new ArrayList<>();
//        blockList.add(block1);
//        blockList.add(block2);
//
//        ProtoBlockchain blockchain = ProtoBlockchain.newBuilder()
//                .addAllBlocks(blockList)
//                .build();
//
//        // Сериализация блокчейна в массив байтов
//        byte[] serializedBlockchain = blockchain.toByteArray();
//
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
