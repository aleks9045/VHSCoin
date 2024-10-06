package Peers;

import Peers.DHT.KademliaNode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class PeerThread extends Thread {
    private final String peerName;
    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
//    private final KademliaNode kademliaNode = new KademliaNode();
    private HashMap<String, Function<Object[], Object>> commands = new HashMap<>();
    CommandProcessor commandProcessor = new CommandProcessor();

    public PeerThread(String threadName, String peerName, int port, ServerSocket serverSocket, ExecutorService threadPool) {
        super(threadName);
        this.peerName = peerName;
        this.port = port;
        this.serverSocket = serverSocket;
        // используем пул потоков для управления соединениями
        this.threadPool = threadPool;
        prepareData();
    }

    @Override
    public void run() {
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        System.out.println(peerName + " is listening on port " + port);

        while (!isInterrupted()) {
            try {

                Socket newConnection = serverSocket.accept();
                // используем пул потоков для обработки клиентов
                threadPool.submit(() -> handleClient(newConnection));

            } catch (SocketException e) {
                // Исключение возникает при закрытии ServerSocket
                System.out.println(peerName + ": Server socket closed, because stopping thread.");
                break;
            } catch (IOException e) {
                System.err.println(peerName + ": Error handling client: " + e.getMessage());
            }
        }
        System.out.printf("%s finished... \n", Thread.currentThread().getName());
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true)) {


            String firstLine = in.readLine();
            System.out.println(peerName + " received: " + firstLine + " from " + socket.getRemoteSocketAddress());

            Object response = executeCommand(firstLine);

            out.println(response.toString());
            out.flush(); // На всякий случай
            System.out.println(peerName + " sent answer: " + response + " to " + socket.getRemoteSocketAddress());

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close(); // Явно закрываем сокет
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    // Добавление стабильных нод
    private void prepareData() {
        System.out.println("Initial data prepared.");
    }

    // Закрытие пула потоков при завершении работы
    public void shutdownThreadPool() {
        try {
            // Закрытие сокета: срабатывает исключение SocketException
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(peerName + ": Error closing server socket: " + e.getMessage());
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        } finally {
            System.out.println("Shutting down thread pool");
        }
    }

    private Object executeCommand(String firstLine) {
        try {
            String[] slicesFirstLine = firstLine.split(" ");
            if (slicesFirstLine.length > 0) {
                String command = slicesFirstLine[0];
                String[] args = new String[slicesFirstLine.length - 1];
                if (slicesFirstLine.length > 1) {
                    System.arraycopy(slicesFirstLine, 1, args, 0, args.length);
                }
                return commandProcessor.processCommand(command, args);
            } else {
                return "Empty input.";
            }
        } catch (IllegalArgumentException e) {
            return "Unknown command, please try send 'help'.";
        }
    }

    private class CommandProcessor {
        public CommandProcessor() {
            // Пример команды без параметров
//            commands.put("getAddresses", (args) -> adresses);
            commands.put("help", (args) -> commands);
        }

        public Object processCommand(String command, Object[] args) {
            Function<Object[], Object> cmd = commands.get(command);
            if (cmd != null) {
                return cmd.apply(args);
            } else {
                throw new IllegalArgumentException("Unknown command: " + command);
            }
        }
    }
}