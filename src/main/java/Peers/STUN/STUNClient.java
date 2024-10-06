package Peers.STUN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class STUNClient {

    // Адрес STUN-сервера
    private static final String STUN_SERVER = "stun.l.google.com"; // Публичный STUN-сервер Google
    private static final int STUN_PORT = 19302;
    public static void main(String[] args) {
        get();
        get();
        get();
    }

    public static void get() {
        // Используем try-with-resources для автоматического закрытия сокета
        try (DatagramSocket socket = new DatagramSocket()) {

            // STUN запрос: создаем запрос Binding Request
            byte[] stunRequest = createStunRequest();

            // Отправляем запрос STUN-серверу
            InetAddress address = InetAddress.getByName(STUN_SERVER);
            DatagramPacket requestPacket = new DatagramPacket(stunRequest, stunRequest.length, address, STUN_PORT);
            socket.send(requestPacket);
            System.out.println("STUN sended query to " + STUN_SERVER + ":" + STUN_PORT);

            // Получаем ответ от STUN-сервера
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            System.out.println("Ответ получен, длина ответа: " + responsePacket.getLength());

            // Выводим сырые байты ответа
            for (int i = 0; i < responsePacket.getLength(); i++) {
                System.out.print(String.format("%02X ", responseBuffer[i]));
            }
            System.out.println();

            // Парсим ответ от STUN-сервера
            parseStunResponse(responsePacket.getData());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод создания простого STUN Binding Request
    private static byte[] createStunRequest() {
        ByteBuffer buffer = ByteBuffer.allocate(20); // STUN Binding Request — 20 байт
        buffer.putShort((short) 0x0001); // Тип сообщения: Binding Request
        buffer.putShort((short) 0x0000); // Длина сообщения: 0 (для простого запроса)
        buffer.putInt(0x2112A442); // Magic Cookie (фиксированное значение)
        buffer.putInt(0x63c7117e); // Транзакционный ID (случайное значение)
        buffer.putInt(0x0714278f); // Транзакционный ID (случайное значение)
        buffer.putInt(0x5ded3221); // Транзакционный ID (случайное значение)
        return buffer.array();
    }

    // Метод для парсинга ответа STUN-сервера
    private static void parseStunResponse(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.getShort(); // Пропускаем тип сообщения
        buffer.getShort(); // Пропускаем длину сообщения
        buffer.getInt(); // Пропускаем Magic Cookie

        // Пропускаем Транзакционный ID (12 байт)
        buffer.getInt();
        buffer.getInt();
        buffer.getInt();

        // Читаем атрибуты STUN-сообщения
        while (buffer.remaining() > 0) {
            short type = buffer.getShort();  // Читаем тип атрибута
            short length = buffer.getShort(); // Длина атрибута

            if (type == 0x0020) { // XOR-MAPPED-ADDRESS атрибут
                buffer.get(); // Пропускаем первый байт (не используется)
                byte family = buffer.get(); // IPv4 или IPv6
                short port = buffer.getShort(); // Порт

                // XOR операция для порта
                port ^= 0x2112;

                byte[] ip = new byte[4];
                buffer.get(ip);

                // XOR операция для IP-адреса
                ip[0] ^= 0x21;
                ip[1] ^= 0x12;
                ip[2] ^= 0xA4;
                ip[3] ^= 0x42;

                // Выводим публичный IP-адрес и порт
                System.out.println("Public IP: " + (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF) + "." + (ip[3] & 0xFF));
                System.out.println("Public Port: " + (port & 0xFFFF));
            } else {
                buffer.position(buffer.position() + length); // Пропускаем неизвестные атрибуты
            }
        }
    }
}
