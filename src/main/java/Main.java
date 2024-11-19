import BlockChain.Block.Block;
import BlockChain.Transaction.Transaction;
import BlockChain.Transaction.TransactionPull.TransactionPull;
import Net.Peer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {
        fork2();
    }

    public static void fork1() {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        int diff = 5;
//        Block block1 = new Block("a", "0", timestamp.getTime(), diff);
//        block1.printData();
//        String prevHash = block1.getHash();
//        System.out.println("");
//        Block block2 = new Block("b", prevHash, timestamp.getTime(), diff);

    }

    public static void fork2() throws Exception {

//        Peer peer1 = new Peer("Peer1");
//        Peer peer2 = new Peer("Peer2");

//        peer1.start();
//        peer2.start();
//        Thread.sleep(100);

//        peer1.sendMessageToSS("Peer2", "hellooooooooooooooo");
//        peer2.sendMessageToSS("Peer1", "hello");

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
