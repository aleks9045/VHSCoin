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
        STUNClient stunClient = new STUNClient();
        String[] myIpNPort = stunClient.getMyIp();
        Peer stable_peer = new Peer("StablePeer", myIpNPort[0], Integer.parseInt(myIpNPort[1]));
        stable_peer.start();

        Peer peer1 = new Peer("Peer1", "localhost",8001);
        peer1.start();
        peer1.sendMessage("localhost", Integer.parseInt(myIpNPort[1]), "getAddresses");
        peer1.sendMessage("localhost", Integer.parseInt(myIpNPort[1]), "getAddresses");
    }
}
