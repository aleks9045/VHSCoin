package Net;

import Net.Repository.BlockchainRepository;
import Net.Repository.TransactionPullRepository;
import Net.Serializers.DataHandler;

import java.util.concurrent.Executors;


public class Peer {
    private final PeerClient peerClient;


    public Peer() {
        this.peerClient = new PeerClient(
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
        byte[][] bytesBlockchain = DataHandler.serializeBlockchain(BlockchainRepository.getBlockChain());
        peerClient.sendData(bytesBlockchain, 1);
    }
    public void sendTransactionPull(){
        byte[][] bytesTransactionPull = DataHandler.serializeTransactionPull(TransactionPullRepository.getTransactionPull());
        peerClient.sendData(bytesTransactionPull, 2);
    }

    public void stop() {
        if (peerClient.isAlive()) {
            peerClient.shutdownThreadPool();
            peerClient.disconnectFromSS();
            peerClient.interrupt();
        }
    }
}
