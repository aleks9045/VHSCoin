import Peers.Peer;
import java.io.IOException;
import java.sql.Timestamp;
import BlockChain.Block;
import Peers.STUN.STUNClient;
import BlockChain.Transaction;

public class Main {
    public static void main(String[] args) throws IOException {
        fork2();
    }


    public static void fork1(){
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
        Peer stable_peer1 = new Peer("StablePeer1", 5001);
        Peer stable_peer2 = new Peer("StablePeer2", 5002);

        stable_peer1.start();
        stable_peer2.start();
        STUNClient stunClient = new STUNClient();
        Peer peer1 = new Peer("Peer1", Integer.parseInt(stunClient.getMyIp()[1]));
        peer1.start();
        peer1.sendMessage("localhost", 5001, "help");
    }
}
