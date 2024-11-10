package Peers.TURN;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

public class TURNClient {

    private final String TURN_SERVER = "89.46.131.17";
    private final int TURN_PORT = 5349;
    private final TurnRequest turnRequest = new TurnRequest();

    public void sendAllocateRequest() {
            // Создаем SSL-сокет
            try (SSLSocket sslSocket = this.createSSLSocket()) {
                // Начинаем TLS рукопожатие
                sslSocket.startHandshake();

                System.out.println("Connected to TURN server at " + TURN_SERVER + ":" + TURN_PORT);

                // Открываем потоки ввода/вывода
                InputStream input = sslSocket.getInputStream();
                OutputStream output = sslSocket.getOutputStream();

                // Создаем TURN Binding Request (Allocate Request)
                byte[] request = turnRequest.createAllocateRequest();
                System.out.println("Sending Allocate Request...");
                System.out.println(Arrays.toString(request));

                output.write(request);
                output.flush();

                byte[] responseBuffer = new byte[512];
                int bytesRead = input.read(responseBuffer);

                if (bytesRead > 0) {
                    System.out.println("Received response: " + new String(responseBuffer, 0, bytesRead));
                }

            } catch (Exception e) {
                System.out.println("Error during socket communication: " + e.getMessage());
            }

    }

    private SSLSocket createSSLSocket() throws Exception {
        try {
            // Загружаем самоподписанный сертификат
            InputStream certFile = getClass().getResourceAsStream("/publicKey.crt");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(certFile);

            // Создаем пустое хранилище сертификатов
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);  // Инициализируем пустое хранилище
            trustStore.setCertificateEntry("server", cert);

            // Создаем TrustManager, который будет использовать наш кастомный TrustStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            // Настройка SSLContext для использования этого TrustManager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustManagers, new SecureRandom());

            // Получаем SSLSocketFactory из кастомного SSLContext
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
            return (SSLSocket) sslSocketFactory.createSocket(TURN_SERVER, TURN_PORT);
        } catch (Exception e) {
            throw new Exception("Error during create SSLSocket: " + e.getMessage());
        }
    }

    // Метод для парсинга ответа STUN-сервера
    private String[] parseResponse(byte[] data) {
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
