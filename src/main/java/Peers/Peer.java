package Peers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;


public class Peer {
    private final String peerName;
    private final PeerThread peerThread;

    public Peer(String peerName, String ipAddress, int port) throws IOException {
        this.peerName = peerName;
        // Кастомный класс потока для запуска пира
        this.peerThread = new PeerThread(
                peerName,
                ipAddress,
                port,
                new ServerSocket(port),
                Executors.newSingleThreadExecutor());
    }

    public void start() {
        if (!peerThread.isAlive()) {  // Проверяем, не был ли поток уже запущен
            peerThread.start();
        } else {
            System.out.println("Thread already running.");
        }
    }

    public void stop() {
        if (peerThread.isAlive()) {
            peerThread.shutdownThreadPool(); // Прерываение пула потоков
            peerThread.interrupt();  // Прерывание потока
        }
    }

    public void sendMessage(String host, int port, String message) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Отправляем сообщение
            out.println(message);
            System.out.println(peerName + " sent: " + message + " to " + host + ":" + port);
            String answer = in.readLine();
//            while ((answer = in.readLine()) != null) {
                System.out.println("ANSWER IS: " + answer);
//            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
