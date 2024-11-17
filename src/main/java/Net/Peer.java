package Net;

import Net.Peers.PeerClient;
import Net.Signaling.SignalingClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;


public class Peer {
    private final String peerName;
    private final PeerClient peerClient;
    private final SignalingClient signalingClient;

    public Peer(String peerName, String ipAddress, int port) throws IOException {
        this.peerName = peerName;

        this.signalingClient = new SignalingClient(peerName);

        this.peerClient = new PeerClient(
                peerName,
                ipAddress,
                port,
                new ServerSocket(port),
                Executors.newSingleThreadExecutor());
    }


    public void connectToSS() {
        if (!signalingClient.isAlive()) {
            peerClient.start();
        } else {
            System.out.println("Thread already running.");
        }
    }

    public void sendMessageToSS(String targetId, String message) {
        signalingClient.sendMessage(targetId, message);
    }

    public String getLastMessageFromSS() {
        return signalingClient.getLastMessage();
    }

    public void start() {
        if (!peerClient.isAlive() && !signalingClient.isAlive()) {  // Проверяем, не был ли поток уже запущен
            signalingClient.start();
            peerClient.start();
        } else {
            System.out.println("Threads already running.");
        }
    }

    public void stop() {
        if (peerClient.isAlive() || signalingClient.isAlive()) {
            peerClient.shutdownThreadPool(); // Прерывание пула потоков
            peerClient.interrupt();
            signalingClient.disconnect();
            signalingClient.interrupt();
        }
    }

    public void sendMessageAsync(String host, int port, String message) {
        new Thread(() -> sendMessage(host, port, message)).start();
    }

    public void sendMessage(String host, int port, String message) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Отправляем сообщение
            out.println(message);
            System.out.println(peerName + " sent: '" + message + "' to " + host + ":" + port);
            String answer = in.readLine();
            System.out.println("ANSWER IS: " + answer);

        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
