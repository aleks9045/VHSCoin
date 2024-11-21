package Net;

import BlockChain.BlockChain;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;


public class Peer {
    private final String peerName;
    private final PeerClient peerClient;


    public Peer(String peerName) {
        this.peerName = peerName;
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

    public void sendMessage(){
        System.out.println(peerClient.sendMessage());
    }

    public void stop() {
        if (peerClient.isAlive()) {
            peerClient.shutdownThreadPool(); // Прерывание пула потоков
            peerClient.disconnectFromSS();
            peerClient.interrupt();
        }
    }
}
