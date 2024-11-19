package Net.Serializers;

import BlockChain.BlockChain;
import BlockChain.Protos.ProtoBlock;
import BlockChain.Protos.ProtoTransaction;
import BlockChain.Protos.ProtoTransactions;
import BlockChain.Block.Block;
import BlockChain.Transaction.Transaction;
import BlockChain.Transaction.TransactionPull.TransactionPull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class BlockchainSerializer {

//    public static BlockChain receiveBlockchainBytes(InputStream in) throws IOException {
//        BlockChain blockChain = new BlockChain();
//        while (true) {
//            // Читаем длину блока
//            byte[] lengthBytes = new byte[4];
//            if (in.read(lengthBytes) != 4) break;
//
//            int blockLength = byteArrayToInt(lengthBytes);
//
//            // Читаем данные блока
//            byte[] blockData = new byte[blockLength];
//            int bytesRead = 0;
//            while (bytesRead < blockLength) {
//                int result = in.read(blockData, bytesRead, blockLength - bytesRead);
//                if (result == -1)
//                    throw new IOException("Connection closed during block transmission.");
//                bytesRead += result;
//            }
//            ProtoBlock protoBlock = ProtoBlock.parseFrom(blockData);
//            TransactionPull transactionPull = getTransactionPull(protoBlock);
//            blockChain.addBlock(new Block(transactionPull, protoBlock.getPreviousHash(), protoBlock.getTimestamp(), protoBlock.getDifficulty()));
//        }
//
//        return blockChain;
//    }
//
////    public static BlockChain createBlockchainBytes(BlockChain blockChain) {
////
////    }
//
//    private static TransactionPull getTransactionPull(ProtoBlock protoBlock) {
//        ProtoTransactions protoBlockData = protoBlock.getData();
//        TransactionPull transactionPull = new TransactionPull();
//        for (ProtoTransaction protoTransaction : protoBlockData.getTransactionsList()) {
//            Transaction transaction = new Transaction(
//                    protoTransaction.getSender(),
//                    protoTransaction.getRecipient(),
//                    protoTransaction.getTimestamp(),
//                    protoTransaction.getAmount(),
//                    protoTransaction.getAccess());
//            transactionPull.addTransaction(transaction);
//        }
//        return transactionPull;
//    }

    private static int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    public static void createBytesGenesisBlock() {
        ProtoTransactions emptyTransactions = ProtoTransactions.newBuilder().build();
        System.out.println(emptyTransactions);
        ProtoBlock block1 = ProtoBlock.newBuilder()
                .setHash("2ac9a6746aca543af8dff39894cfe8173afba21eb01c6fae33d52947222855ef")
                .setPreviousHash("0")
                .setData(emptyTransactions)
                .setTimestamp("")
                .setDifficulty(0)
                .setNonce(0)
                .build();

        byte[] serializedblock1 = block1.toByteArray();
        System.out.println(Arrays.toString(serializedblock1));
    }
}
