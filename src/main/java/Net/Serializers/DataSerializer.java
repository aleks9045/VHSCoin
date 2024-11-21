package Net.Serializers;

import BlockChain.BlockChain;
import BlockChain.Protos.ProtoBlock;
import BlockChain.Protos.ProtoTransaction;
import BlockChain.Protos.ProtoTransactions;
import BlockChain.Block.Block;
import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;
import Net.Repository.BlockchainRepository;
import Net.Repository.TransactionPullRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSerializer {
    public static void receiveData(InputStream in) {
        try {
            int dataType = twoByteArrayToInt(in.readNBytes(2));
            switch (dataType) {
                case 0:
                    break;
                case 1:
                    BlockchainRepository.setBlockChain(receiveBlockchain(in));
                    break;
                case 2:
                    TransactionPullRepository.setTransactionPull(receiveTransactionPull(in));
                    break;
                default:
                    System.out.println("Invalid data type");
                    break;
            }

        } catch (IOException e) {
            System.out.println("Error reading data");
            Thread.currentThread().interrupt();
        }
    }
    public static byte[][] serializeBlockchain(BlockChain blockChain) {
        List<Block> chain = blockChain.getChain();
        byte[][] serializedBlockchain = new byte[chain.size()][];
        for (int i = 0; i < chain.size(); ++i){
            List<ProtoTransaction> transactionList = new ArrayList<>();
            for (Transaction transaction : chain.get(i).getData().getAllTransactions()){
                ProtoTransaction protoTransaction = ProtoTransaction.newBuilder()
                        .setSender(transaction.getSender())
                        .setRecipient(transaction.getRecipient())
                        .setTimestamp(transaction.getTimeStamp())
                        .setAmount(transaction.getAmount())
                        .setAccess(transaction.getAccess())
                        .build();
                transactionList.add(protoTransaction);
            }
            ProtoTransactions protoTransactions = ProtoTransactions.newBuilder()
                    .addAllTransactions(transactionList)
                    .build();
            ProtoBlock protoBlock = ProtoBlock.newBuilder()
                    .setHash(chain.get(i).getHash())
                    .setPreviousHash(chain.get(i).getPreviousHash())
                    .setData(protoTransactions)
                    .setTimestamp(chain.get(i).getTimeStamp())
                    .setDifficulty(chain.get(i).getDifficulty())
                    .setNonce(chain.get(i).getNonce())
                    .build();
            serializedBlockchain[i] = protoBlock.toByteArray();
        }
        return serializedBlockchain;
    }
    public static BlockChain receiveBlockchain(InputStream in) {
        BlockChain blockChain = new BlockChain();
        try {
            int numOfBlocks = fourByteArrayToInt(in.readNBytes(4));
            for (int i = 0; i < numOfBlocks; i++) {
                ProtoBlock protoBlock = ProtoBlock.parseFrom(readBlock(in));
                TransactionPull transactionPull = TransactionPullFromProtoBlock(protoBlock);
                Block block = new Block(transactionPull, protoBlock.getPreviousHash(), protoBlock.getTimestamp(), protoBlock.getDifficulty());
                blockChain.addBlock(block);
            }
        } catch (IOException e) {
            System.out.println("Error while receiving blockchain bytes: " + e.getMessage());
        }
        return blockChain;
    }

    private static TransactionPull TransactionPullFromProtoBlock(ProtoBlock protoBlock) {
        ProtoTransactions protoBlockData = protoBlock.getData();
        TransactionPull transactionPull = new TransactionPull();
        for (ProtoTransaction protoTransaction : protoBlockData.getTransactionsList()) {
            Transaction transaction = new Transaction(
                    protoTransaction.getSender(),
                    protoTransaction.getRecipient(),
                    protoTransaction.getTimestamp(),
                    protoTransaction.getAmount(),
                    protoTransaction.getAccess()
            );
            transactionPull.addTransaction(transaction);
        }
        return transactionPull;
    }

    private static TransactionPull receiveTransactionPull(InputStream in) {
        TransactionPull transactionPull = new TransactionPull();
        try {
            int numOfBlocks = fourByteArrayToInt(in.readNBytes(4));
            for (int i = 0; i < numOfBlocks; i++) {
                ProtoTransaction protoTransaction = ProtoTransaction.parseFrom(readBlock(in));
                if (!protoTransaction.getAccess().isEmpty()) {
                Transaction transaction = new Transaction(
                        protoTransaction.getSender(),
                        protoTransaction.getRecipient(),
                        protoTransaction.getTimestamp(),
                        protoTransaction.getAmount(),
                        protoTransaction.getAccess()
                );
                transactionPull.addTransaction(transaction);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while receiving pull bytes: " + e.getMessage());
        }
        return transactionPull;
    }

    private static byte[] readBlock(InputStream in) throws IOException {
        byte[] lengthBytes = in.readNBytes(4);
        int blockLength = fourByteArrayToInt(lengthBytes);

        byte[] blockData = new byte[blockLength];
        int bytesRead = 0;
        while (bytesRead < blockLength) {
            int result = in.read(blockData, bytesRead, blockLength - bytesRead);
            if (result == -1) {
                throw new IOException("Connection closed during block transmission.");
            }
            bytesRead += result;
        }
        return blockData;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static int fourByteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    public static int twoByteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }

    public static void createBytesGenesisBlock() {
        ProtoTransactions emptyTransactions = ProtoTransactions.newBuilder().build();
        System.out.println(emptyTransactions);
        ProtoBlock block1 = ProtoBlock.newBuilder()
                .setHash("2ac9a6746aca543af8dff39894cfe8173afba21eb01c6fae33d52947222855ef")
                .setPreviousHash("0")
                .setData(emptyTransactions)
                .setTimestamp(0)
                .setDifficulty(0)
                .setNonce(0)
                .build();

        byte[] serializedblock1 = block1.toByteArray();
        System.out.println(Arrays.toString(serializedblock1));
    }
}
