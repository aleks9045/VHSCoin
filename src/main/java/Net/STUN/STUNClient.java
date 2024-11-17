package Net.STUN;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

public class STUNClient {

    private final String SERVER = "89.46.131.17";
    private final int PORT = 5349;
    private final STUNRequest STUNRequest = new STUNRequest();

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
            return (SSLSocket) sslSocketFactory.createSocket(SERVER, PORT);
        } catch (Exception e) {
            throw new Exception("Error during create SSLSocket: " + e.getMessage());
        }
    }

    public void sendBindingRequest() {
        try (SSLSocket sslSocket = this.createSSLSocket()){
            sslSocket.startHandshake();

            System.out.println("Connected to STUN server at " + SERVER + ":" + PORT);

            InputStream input = sslSocket.getInputStream();
            OutputStream output = sslSocket.getOutputStream();

            byte[] request = STUNRequest.createSTUNRequest();
            System.out.println("Sending Request...");

            output.write(request);
            output.flush();
            byte[] bytesRead = input.readAllBytes();
            System.out.println("Received Response...");

            if (bytesRead.length > 0) {
                String[] two = STUNRequest.parseResponse(bytesRead);
                System.out.println(Arrays.toString(two));
            }

        } catch (Exception e) {
            System.out.println("Error during socket communication: " + e.getMessage());
        }

    }
}
