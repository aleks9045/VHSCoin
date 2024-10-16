import BlockChain.Block;
import Peers.Peer;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import BlockChain.Transaction;
import Peers.STUN.STUNClient;


public class Main {
    public static void main(String[] args) throws IOException {
        fork2();

    }


    public static void fork1() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int diff = 5;
        Block block1 = new Block("a", "0", timestamp.getTime(), diff);
        block1.printData();
        String prevHash = block1.getHash();
        System.out.println("");
        Block block2 = new Block("b", prevHash, timestamp.getTime(), diff);
        block2.printData();
    }

    public static void fork2() throws IOException {
//        STUNClient stunClient = new STUNClient();
//        String[] myIpNPort = stunClient.getMyIp();
//        System.out.println(myIpNPort[0] + " " + myIpNPort[1]);
//        Peer stable_peer = new Peer("StablePeer", myIpNPort[0], Integer.parseInt(myIpNPort[1]));
//        stable_peer.start();
//
//        Peer peer1 = new Peer("Peer1", "localhost",8001);
//        peer1.start();
//        peer1.sendMessage("localhost", Integer.parseInt(myIpNPort[1]), "getAddresses");
//        peer1.sendMessage("localhost", Integer.parseInt(myIpNPort[1]), "getAddresses");
        ProtoBlock block1 = ProtoBlock.newBuilder()
                .setHash("0000000000000000")
                .setPreviousHash("0")
                .setData("Первый блок")
                .setTimestamp("2024-10-06T12:00:00Z")
                .setDifficulty(3)
                .setNonce(12345)
                .build();

        // Создаем второй блок
        ProtoBlock block2 = ProtoBlock.newBuilder()
                .setHash("000000000000001")
                .setPreviousHash(block1.getHash())
                .setData("Второй блок")
                .setTimestamp("2024-10-06T12:05:00Z")
                .setDifficulty(3)
                .setNonce(67890)
                .build();

        // Создаем блокчейн и добавляем блоки
        List<ProtoBlock> blockList = new ArrayList<>();
        blockList.add(block1);
        blockList.add(block2);

        ProtoBlockchain blockchain = ProtoBlockchain.newBuilder()
                .addAllBlocks(blockList)
                .build();

        // Сериализация блокчейна в массив байтов
        byte[] serializedBlockchain = blockchain.toByteArray();

        // Десериализация из массива байтов
        try {
            ProtoBlockchain deserializedBlockchain = ProtoBlockchain.parseFrom(serializedBlockchain);
            System.out.println("skjdfhkjfghksjdfg: " + deserializedBlockchain.getBlocksCount());

            // Вывод данных каждого блока
            for (ProtoBlock block : deserializedBlockchain.getBlocksList()) {
                System.out.println("Блок:");
                System.out.println("  Хеш: " + block.getHash());
                System.out.println("  Предыдущий хеш: " + block.getPreviousHash());
                System.out.println("  Данные: " + block.getData());
                System.out.println("  Время создания: " + block.getTimestamp());
                System.out.println("  Сложность: " + block.getDifficulty());
                System.out.println("  Нонс: " + block.getNonce());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
