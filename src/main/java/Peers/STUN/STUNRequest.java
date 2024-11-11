package Peers.STUN;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

class STUNRequest {
    private static final SecureRandom secureRandom = new SecureRandom();

    byte[] createSTUNRequest(){

        byte[] request = new byte[20];

        // Тип запроса Binding Request (0x0001)
        request[0] = 0;
        request[1] = (byte) 1;

        // Длина сообщения (минус 20 байт заголовка)
        request[2] = 0;
        request[3] = (byte) 0;

        // Magic Cookie (4 байта, фиксированное значение)
        byte[] magicCookie = new byte[]{(byte) 0x21, (byte) 0x12, (byte) 0xA4, (byte) 0x42};
        System.arraycopy(magicCookie, 0, request, 4, 4);

        // Transaction ID (12 байт, случайный)
        byte[] transactionId = new byte[12];
        secureRandom.nextBytes(transactionId);

        System.arraycopy(transactionId, 0, request, 8, 12);

        return request;
    }
    String[] parseResponse(byte[] data) {
        String[] result = new String[2];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(20);
        while (buffer.remaining() > 0) {
            short type = buffer.getShort();  // Читаем тип атрибута
            buffer.getShort(); // Длина атрибута

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
            }
        }
        return result;
    }

    private String handleIpV4Address(ByteBuffer buffer) {

        byte[] ipBytes = new byte[4];
        buffer.get(ipBytes);
        // XOR операция для IP-адреса(магические числа)
        ipBytes[0] ^= (byte) 0x21;
        ipBytes[1] ^= (byte) 0x12;
        ipBytes[2] ^= (byte) 0xA4;
        ipBytes[3] ^= (byte) 0x42;

        return (ipBytes[0] & 0xFF) + "." + (ipBytes[1] & 0xFF) + "." + (ipBytes[2] & 0xFF) + "." + (ipBytes[3] & 0xFF);
    }

    private String handleIpV6Address(ByteBuffer buffer) {
        byte[] ipBytes = new byte[16];
        buffer.get(ipBytes);
        // XOR операция для IPv6 (размер больше, магические числа не изменяются)
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) {
                ipBytes[i] ^= (byte)0x21;
            } else {
                ipBytes[i] ^= (byte)0x12;
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
