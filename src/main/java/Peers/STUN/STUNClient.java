package Peers.STUN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import java.util.Random;

public class STUNClient {

    // Адрес STUN-сервера
    private static final String STUN_SERVER = "stun.l.google.com"; // Публичный STUN-сервер Google
    private static final int STUN_PORT = 19302;
    private static final Random rand = new Random();

    public String[] getMyIp() {
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
            System.out.println("Recieved STUN response.");
            // Парсим ответ от STUN-сервера
            return parseStunResponse(responsePacket.getData());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Метод создания простого STUN Binding Request
    private byte[] createStunRequest() {
        ByteBuffer buffer = ByteBuffer.allocate(20); // STUN Binding Request — 20 байт
        buffer.putShort((short) 0x0001); // Тип сообщения: Binding Request
        buffer.putShort((short) 0x0000); // Длина сообщения: 0 (для простого запроса)
        buffer.putInt(0x2112A442); // Magic Cookie (фиксированное значение)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        return buffer.array();
    }

    // Метод для парсинга ответа STUN-сервера
    private String[] parseStunResponse(byte[] data) {
        String[] result = new String[2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.getShort(); // Пропускаем тип сообщения
        buffer.getShort(); // Пропускаем длину сообщения
        buffer.getInt(); // Пропускаем Magic Cookie

        // Пропускаем Транзакционный ID (12 байт)
        buffer.get(new byte[12]);

        // Читаем атрибуты STUN-сообщения
        while (buffer.remaining() > 0) {
            short type = buffer.getShort();  // Читаем тип атрибута
            short length = buffer.getShort(); // Длина атрибута

            if (type == 0x0020) { // XOR-MAPPED-ADDRESS атрибут
                buffer.get(); // Пропускаем первый байт (не используется)
                byte family = buffer.get(); // IPv4 или IPv6

                short shortPort = buffer.getShort(); // Порт
                // XOR операция для порта (магическое число)
                shortPort ^= 0x2112;
                // Преобразование из short в int с сохранением первых 16 бит
                int intPort = shortPort & 0xFFFF;
                String ipAddress = null;
                if (family == 0x01) {
                    ipAddress = handleIpV4Address(buffer);
                } else if (family == 0x02) {
                    ipAddress = handleIpV6Address(buffer);
                }
                result[0] = ipAddress;
                result[1] = Integer.toString(intPort);
                break;
            } else {
                buffer.position(buffer.position() + length); // Пропускаем неизвестные атрибуты
            }
        }
        return result;
    }

    private String handleIpV4Address(ByteBuffer buffer) {

        byte[] ipBytes = new byte[4];
        buffer.get(ipBytes);
        // XOR операция для IP-адреса(магические числа)
        ipBytes[0] ^= 0x21;
        ipBytes[1] ^= 0x12;
        ipBytes[2] ^= 0xA4;
        ipBytes[3] ^= 0x42;

        return (ipBytes[0] & 0xFF) + "." + (ipBytes[1] & 0xFF) + "." + (ipBytes[2] & 0xFF) + "." + (ipBytes[3] & 0xFF);
    }

    private String handleIpV6Address(ByteBuffer buffer) {
        byte[] ipBytes = new byte[16];
        buffer.get(ipBytes);
        // XOR операция для IPv6 (размер больше, магические числа не изменяются)
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) {
                ipBytes[i] ^= 0x21;
            } else {
                ipBytes[i] ^= 0x12;
            }
        }
        // Преобразуем IP в строку для IPv6
        StringBuilder ipAddress = new StringBuilder();
        for (int i = 0; i < 16; i += 2) {
            ipAddress.append(String.format("%02x", ipBytes[i] & 0xFF))
                    .append(String.format("%02x", ipBytes[i + 1] & 0xFF));
            if (i < 14) ipAddress.append(":");
        }
        return ipAddress.toString();
    }
}
