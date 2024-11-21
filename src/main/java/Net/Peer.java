package Net;

import Net.Repository.BlockchainRepository;
import Net.Serializers.DataSerializer;

import java.util.Arrays;
import java.util.concurrent.Executors;


public class Peer {
    private final PeerClient peerClient;


    public Peer(String peerName) {
        this.peerClient = new PeerClient(
                peerName,
                Executors.newSingleThreadExecutor());
    }

    public void listen() {
        if (!peerClient.isAlive()) {  // Проверяем, не был ли поток уже запущен
            peerClient.start();
        } else {
            System.out.println("Threads already running.");
        }
    }

    public void sendBlockchain() {
        byte[][] bytesBlockchain = DataSerializer.serializeBlockchain(BlockchainRepository.getBlockChain());
        peerClient.sendData(bytesBlockchain, 1);
    }
    public void sendTransactionPull(){
//        peerClient.sendData(data, 2);
    }

    public void stop() {
        if (peerClient.isAlive()) {
            peerClient.shutdownThreadPool(); // Прерывание пула потоков
            peerClient.disconnectFromSS();
            peerClient.interrupt();
        }
    }
}
