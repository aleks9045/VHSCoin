package Blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int difficulty;
    private int nonce;

    public Block(String data, String previousHash, long timeStamp, int difficulty) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.difficulty = difficulty;
        this.hash = calculateHash();
        mineBlock();
    }

    public String calculateHash() {
        String input = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
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

    public void mineBlock() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public String printData() {
        System.out.println("Time: " + timeStamp);
        System.out.println("Hash: " + hash);
        System.out.println("PrHash: " + previousHash);
        System.out.println("Data: " + data);
        return "";
    }

    public String getHash(){
        return hash;
    }
}
