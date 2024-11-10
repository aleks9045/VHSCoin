package Peers.TURN;

import org.w3c.dom.ls.LSOutput;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

class TurnRequest {
    private static final Random rand = new Random();

    public byte[] createRequest() {
        ByteBuffer buffer = ByteBuffer.allocate(20); // STUN Binding Request — 20 байт
        buffer.putShort((short) 0x0003); // Тип сообщения: Allocate Request
        buffer.putShort((short) 0x0000); // Длина сообщения: 0 (для простого запроса)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        buffer.putInt(rand.nextInt(65536)); // Транзакционный ID (случайное значение)
        buffer.putInt(0x2112A442); // Magic Cookie (фиксированное значение)
        return buffer.array();
    }

    public byte[] createAllocateRequest() throws Exception {
        byte[] passwordBytes = "secret".getBytes();

        int headerLength = 20;

        int messageLength = 4 + 20;

        byte[] allocateRequest = new byte[headerLength + messageLength];

        // Тип запроса Allocate Request (0x0003)
        allocateRequest[0] = 0;
        allocateRequest[1] = (byte) 3;

        // Длина сообщения (минус 20 байт заголовка)

        allocateRequest[2] = 0;
        allocateRequest[3] = (byte) messageLength;

        // Magic Cookie (4 байта, фиксированное значение)
        byte[] magicCookie = new byte[]{(byte) 0x21, (byte) 0x12, (byte) 0xA4, (byte) 0x42};
        System.arraycopy(magicCookie, 0, allocateRequest, 4, 4);

        // Transaction ID (12 байт, случайный)
        byte[] transactionId = new byte[12];
        new Random().nextBytes(transactionId);

        System.arraycopy(transactionId, 0, allocateRequest, 8, 12);

        byte[] messageIntegrity = calculateHMAC(allocateRequest, passwordBytes);
        addAttribute(allocateRequest, 20, (byte) 12, messageIntegrity);


        return allocateRequest;
    }

    private byte[] calculateHMAC(byte[] data, byte[] key) throws Exception {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA1");
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new Exception("Error during calculateHMAC: " + e.getMessage());
        }
    }

    // Метод для добавления атрибута в запрос
    private void addAttribute(byte[] request, int offset, byte attribureType, byte[] value) {

        // Записываем тип атрибута (2 байта)
        request[offset] = (byte) (attribureType >> 8);
        request[offset + 1] = attribureType;

        // Длина атрибута (2 байта)
        System.out.println((byte) (value.length >> 8));
        request[offset + 2] = (byte) (value.length >> 8); // Старший байт длины
        request[offset + 3] = (byte) (value.length); // Младший байт длины

        // Записываем значение атрибута
        System.arraycopy(value, 0, request, offset + 4, value.length);
    }
}
