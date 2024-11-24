package BlockChain.BlockChainUtils;

import BlockChain.Block.Block;
import BlockChain.Transactions.Transaction;
import BlockChain.Transactions.TransactionPull.TransactionPull;
import BlockChain.BlockChain;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;


public class BlockChainUtils {
    public TransactionPull transactionPull;
    public BlockChain blockChain;

    public BlockChainUtils() {
        this.transactionPull = new TransactionPull();
        this.blockChain = new BlockChain();

    }

    // Метод обработки нового блокчейна от другого пира
    public void processIncomingBlockChain(BlockChain incomingBlockChain) {

        if (!incomingBlockChain.isChainValid()) {
            System.out.println("Incoming blockchain is invalid.");
            return;
        }

        // Сравниваем текущий блокчейн с новым
        List<Block> currentBlocks = blockChain.getChain();
        List<Block> incomingBlocks = incomingBlockChain.getChain();

        int commonPrefixIndex = findCommonPrefixIndex(currentBlocks, incomingBlocks);

        if (commonPrefixIndex == currentBlocks.size() - 1) {
            // Если новый блокчейн - продолжение текущего
            addNewBlocks(currentBlocks.size(), incomingBlocks);
        } else if (commonPrefixIndex >= 0) {
            // Если блокчейны расходятся после общего префикса
            resolveDivergentChains(commonPrefixIndex, incomingBlocks, currentBlocks);
        } else {
            System.out.println("No common prefix found. Cannot merge blockchains.");
        }

        // Удаляем транзакции из пула, которые уже присутствуют в обновленном блокчейне
        removeProcessedTransactionsFromPool();
    }

    private int findCommonPrefixIndex(List<Block> currentBlocks, List<Block> incomingBlocks) {
        int index = -1;
        for (int i = 0; i < Math.min(currentBlocks.size(), incomingBlocks.size()); i++) {
            if (currentBlocks.get(i).getHash().equals(incomingBlocks.get(i).getHash())) {
                index = i;
            } else {
                break;
            }
        }
        return index;
    }

    private void addNewBlocks(int startIndex, List<Block> incomingBlocks) {
        for (int i = startIndex; i < incomingBlocks.size(); i++) {
            Block newBlock = incomingBlocks.get(i);
            if (newBlock.calculateHash().equals(newBlock.getHash())) {
                blockChain.addBlock(newBlock);
            } else {
                System.out.println("Invalid block detected. Stopping addition.");
                break;
            }
        }
    }

    private void resolveDivergentChains(int commonIndex, List<Block> incomingBlocks, List<Block> currentBlocks) {
        List<Block> currentTail = currentBlocks.subList(commonIndex + 1, currentBlocks.size());
        List<Block> incomingTail = incomingBlocks.subList(commonIndex + 1, incomingBlocks.size());

        // Выбираем цепочку, первый блок разницы которой появился раньше
        Block firstCurrentBlock = currentTail.isEmpty() ? null : currentTail.get(0);
        Block firstIncomingBlock = incomingTail.isEmpty() ? null : incomingTail.get(0);

        if (firstIncomingBlock != null && (firstCurrentBlock == null ||
                firstIncomingBlock.getTimeStamp() < firstCurrentBlock.getTimeStamp())) {
            // Если блоки из incomingChain раньше, добавляем все блоки из incomingBlocks в свой блокчейн
            blockChain = new BlockChain();
            incomingBlocks.forEach(block -> blockChain.addBlock(block));

            // Все транзакции из текущей цепочки, начиная с первого различающегося блока, добавляем в пул транзакций
            currentTail.forEach(block -> {
                TransactionPull blockData = block.getData();
                blockData.getAllTransactions().forEach(transaction ->
                        transactionPull.addTransaction(transaction));
            });
        } else {
            // Если блоки из currentChain раньше, добавляем все транзакции из incomingTail в пул транзакций
            incomingTail.forEach(block -> {
                TransactionPull blockData = block.getData();
                blockData.getAllTransactions().forEach(transaction ->
                        transactionPull.addTransaction(transaction));
            });
        }
    }

    private void removeProcessedTransactionsFromPool() {
        List<Block> chain = blockChain.getChain();
        List<Transaction> allTransactions = transactionPull.getAllTransactions();

        for (Block block : chain) {
            // Получаем все транзакции из блока
            List<Transaction> blockTransactions = block.getData().getAllTransactions();

            // Удаляем все транзакции блока из пула
            blockTransactions.forEach(transaction ->
                    allTransactions.removeIf(existingTransaction ->
                            existingTransaction.equals(transaction))
            );
        }
    }

    public static boolean checkKeys(String publicKey, String privateKey) {
        try{
            String str = "Some text for test.";
            String a = encryptWithPrivateKey(str, privateKey);
            String b = decryptWithPublicKey(a, publicKey);

            return (b.equals(str));
        }
        catch(Exception e){
            return false;
        }
    }

    public static String encryptWithPrivateKey(String message, String privateKeyString) throws Exception {
        PrivateKey privateKey = convertStringToPrivateKey(privateKeyString); // Конвертируем строку в PrivateKey
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Кодируем в Base64 для удобства
    }

    public static String decryptWithPublicKey(String encryptedMessage, String publicKeyString) throws Exception {
        PublicKey publicKey = convertStringToPublicKey(publicKeyString); // Конвертируем строку в PublicKey
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    private static PublicKey convertStringToPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString); // Декодируем строку Base64 в байты
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes); // Создаём спецификацию ключа
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec); // Генерируем PublicKey из спецификации
    }

    private static PrivateKey convertStringToPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString); // Декодируем строку Base64 в байты
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes); // Создаём спецификацию ключа
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec); // Генерируем PrivateKey из спецификации

    }

    public static String calculateHash(String input) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

