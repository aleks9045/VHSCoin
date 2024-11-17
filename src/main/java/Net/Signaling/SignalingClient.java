package Net.Signaling;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SignalingClient extends Thread {
    private final String clientId;
    private Socket socket;
    private volatile BufferedReader in;
    private volatile PrintWriter out;
    private volatile String lastMessage;

    public SignalingClient(String clientId) {
        super(clientId + " signalThread");
        this.clientId = clientId;
    }
    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        String serverHost = "localhost";
        int serverPort = 8000;
        try {
            socket = new Socket(serverHost, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(clientId);
            System.out.println("Connected to the signaling server as " + clientId);
        } catch (IOException e) {
            System.out.println("Error in connect to the signaling server as " + clientId);
        }

        while (!isInterrupted()) {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    handleSignal(message);
                    setLastMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }

    public void sendMessage(String targetId, String message) {
        out.println(targetId + " " + message);
        System.out.println("Sent message to SS, to " + targetId + " from " + clientId);
    }

    private void handleSignal(String message) {
        System.out.println(clientId + " handling signal: " + message);
    }

    public synchronized void setLastMessage(String message) {
        this.lastMessage = message;
    }

    public synchronized String getLastMessage() {
        return lastMessage;
    }

    // Закрытие соединения
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}