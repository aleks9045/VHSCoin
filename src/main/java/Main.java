import Peers.Peer;

public class Main {
    public static void main(String[] args) throws Exception {
//        Peer stable_peer1 = new Peer("StablePeer1", 5001);
//        Peer stable_peer2 = new Peer("StablePeer2", 5002);
//
//        stable_peer1.start();
//        stable_peer2.start();
        Peer peer1 = new Peer("Peer1", 8001);
        peer1.start();
        peer1.sendMessage("localhost", 5001, "getAddresses");
    }
}
