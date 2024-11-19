package Net;

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

//    public void sendMessageToSS(String targetId, String message) {
//        peerClient.sendMessage(targetId, message);
//    }
//
//    public String getLastMessageFromSS() {
//        return signalClient.getLastMessage();
//    }

    public void start() {
        if (!peerClient.isAlive()) {  // Проверяем, не был ли поток уже запущен
            peerClient.start();
        } else {
            System.out.println("Threads already running.");
        }
    }

    public void stop() {
        if (peerClient.isAlive()) {
            peerClient.shutdownThreadPool(); // Прерывание пула потоков
            peerClient.disconnectFromSS();
            peerClient.interrupt();
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
