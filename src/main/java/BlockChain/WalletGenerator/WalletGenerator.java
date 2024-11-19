package BlockChain.WalletGenerator;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

public class WalletGenerator {

    public static void main(String[] args) {
        try {
            KeyPair wallet = generateWallet();
            String publicKey = encodeKeyToBase64(wallet.getPublic().getEncoded());
            String privateKey = encodeKeyToBase64(wallet.getPrivate().getEncoded());

            System.out.println("Публичный ключ (ID кошелька):");
            System.out.println(publicKey);

            System.out.println("\nПриватный ключ (сохраните его!):");
            System.out.println(privateKey);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    // Генерация пары ключей
    public static KeyPair generateWallet() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();
        keyGen.initialize(2048, secureRandom); // Используем 2048-битный RSA для безопасности
        return keyGen.generateKeyPair();
    }

    // Кодирование ключа в Base64
    public static String encodeKeyToBase64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }
}
