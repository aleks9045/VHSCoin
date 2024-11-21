package Net;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import Net.Repository.BlockchainRepository;
import Net.Serializers.DataSerializer;


public class PeerClient extends Thread {
    private final String peerId;
    private Socket socket;
    private volatile InputStream in;
    private volatile OutputStream out;
    private final ExecutorService threadPool;

    private final Object lock = new Object();
    private Integer lockServerResponse = null;
    private int serverResponse;

    public PeerClient(String peerId, ExecutorService threadPool) {
        super(peerId + " peerThread");
        this.peerId = peerId;
        // используем пул потоков для управления соединениями
        this.threadPool = threadPool;
    }

    private void connectToSS() {

        String serverHost = "localhost";
        int serverPort = 5000;
        try {
            socket = new Socket(serverHost, serverPort);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            Thread.sleep(100);
            System.out.println("Connected to the signaling server as " + peerId);
        } catch (IOException e) {
            System.out.println("Error in connect to the signaling server as " + peerId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        this.connectToSS();
        DataSerializer.receiveData(in);
        DataSerializer.receiveData(in);

        while (!isInterrupted()) {
            DataSerializer.receiveData(in);
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }


    public void sendData(byte[][] data, int dataType) {
        Thread senderThread = new Thread(() -> {
            try {
                out.write(DataSerializer.shortToByteArray((short) dataType));
                out.write(DataSerializer.intToByteArray(data.length));

                for (byte[] dataBlock : data) {
                    out.write(DataSerializer.intToByteArray(dataBlock.length));
                    out.write(dataBlock);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Error in sending message: " + e.getMessage());
            }
        });
        senderThread.start();
        try {
            senderThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted while waiting for response");
        }
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