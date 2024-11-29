package Net;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import Net.Serializers.DataHandler;


public class PeerClient extends Thread {
    private Socket socket;
    private volatile InputStream in;
    private volatile OutputStream out;
    private final ExecutorService threadPool;
    private int reconnections = 0;

    public PeerClient(ExecutorService threadPool) {
        super("peerThread");
        // используем пул потоков для управления соединениями
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        this.connectToSS();
        if (!isInterrupted()) {
            this.initData();
        }
        while (!isInterrupted()) {
            DataHandler.receiveData(in);

        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }

    private void initData() {
        DataHandler.receiveData(in); // Receive blockchain
        DataHandler.receiveData(in); // Receive transaction pull
    }

    private void connectToSS() {
        String serverHost = "89.46.131.17";
        int serverPort = 5000;
        try {
            socket = new Socket(serverHost, serverPort);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            reconnections = 0;
            System.out.println("Connected to the signaling server");
        } catch (IOException e) {
            System.out.println("Error in connect to the signaling server");
            this.checkConnection();
        }
    }


    public void sendData(byte[][] data, int dataType) {
        if (!isInterrupted()) {
            Thread senderThread = new Thread(() -> {
                try {
                    out.write((byte) dataType);
                    out.write(DataHandler.intToByteArray(data.length));

                    for (byte[] dataBlock : data) {
                        out.write(DataHandler.intToByteArray(dataBlock.length));
                        out.write(dataBlock);
                        out.flush();
                    }

                } catch (IOException e) {
                    System.out.println("Error in sending data: " + e.getMessage());
                }
            });
            senderThread.start();
            try {
                senderThread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted while waiting for data transmission");
            }
        }
    }

    private void checkConnection() {
        if (socket == null || socket.isClosed() || isInterrupted()) {
            this.reconnections++;
            if (this.reconnections >= 6) {
                System.out.println("Reached the maximum number of reconnections");
                this.shutdownThreadPool();
                this.disconnectFromSS();
                this.interrupt();
                return;
            }
            System.out.println("Trying to reconnect to the signaling server in " + Math.pow(2, reconnections) + " seconds");
            try {
                Thread.sleep((long) Math.pow(2, reconnections) * 1000);
                this.connectToSS();
            } catch (InterruptedException e) {
                System.out.println("Error in reconnecting to the signaling server");
            }
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