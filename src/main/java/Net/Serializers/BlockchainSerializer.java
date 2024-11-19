package Net.Serializers;

import BlockChain.Protos.ProtoBlock;
import BlockChain.Protos.ProtoBlockchain;
import BlockChain.Protos.ProtoTransactions;
import BlockChain.BlockChain;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockchainSerializer {

    public BlockChain serializeBlockchain(byte[] arr) {
        try {
            ProtoBlockchain blockchain = ProtoBlockchain.parseFrom(arr);
            System.out.println(blockchain);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void createGenesisBlock() {
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

        List<ProtoBlock> blockList = new ArrayList<>();
        blockList.add(block1);

        ProtoBlockchain blockchain = ProtoBlockchain.newBuilder()
                .addAllBlocks(blockList)
                .build();

        byte[] serializedBlockchain = blockchain.toByteArray();
        System.out.println(Arrays.toString(serializedBlockchain));
    }
}
