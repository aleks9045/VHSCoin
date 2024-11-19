package Net;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import BlockChain.BlockChain;
import Net.Serializers.BlockchainSerializer;

public class PeerClient extends Thread {
    private Socket socket;
    private volatile InputStream in;
    private volatile OutputStream out;
    private final String peerId;
    private final ExecutorService threadPool;

    public PeerClient(String peerId, ExecutorService threadPool) {
        super(peerId + " peerThread");
        this.peerId = peerId;
        // используем пул потоков для управления соединениями
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        this.connectToSS();
        while (!isInterrupted()) {
//            try {
//                BlockChain blockChain = BlockchainSerializer.receiveBlockchainBytes(in);
//            } catch (IOException e) {
//                System.err.println("Error receiving message: " + e.getMessage());
//                if (e.getMessage().equals("Connection reset")) {
//                    Thread.currentThread().interrupt();
//                }
//            }
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }

    private void connectToSS() {

        String serverHost = "localhost";
        int serverPort = 5000;
        try {
            socket = new Socket(serverHost, serverPort);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            System.out.println("Connected to the signaling server as " + peerId);
        } catch (IOException e) {
            System.out.println("Error in connect to the signaling server as " + peerId);
        }
    }

    private void handleMessage(String message) {
        System.out.println(message);
    }

    // Закрытие пула потоков при завершении работы
    public void shutdownThreadPool() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        } finally {
            System.out.println("Shutting down thread pool");
        }
    }

    public void disconnectFromSS() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}