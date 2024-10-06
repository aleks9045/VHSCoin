package Peers.DHT;

import java.math.BigInteger;
import java.util.*;

import static Hashing.Hash.hash;


public class KademliaNode {
    private final String nodeHash;
    private final String ipAddress;
    private final int port;
    private String blockchain;
    private final TreeMap<BigInteger, KademliaNode> routingTable = new TreeMap<>(); // Таблица маршрутизации
    private static final int K = 16; // Максимальный размер бакета (количество узлов)

    public KademliaNode(String ipAddress, int port) {
        this.nodeHash = hash(ipAddress);
        this.ipAddress = ipAddress;
        this.port = port;
    }

    // Расстояние между двумя идентификаторами узлов (XOR distance)
    public BigInteger xorDistance(String id1, String id2) {
        BigInteger bigId1 = new BigInteger(hash(id1), 16);
        BigInteger bigId2 = new BigInteger(hash(id2), 16);
        return bigId1.xor(bigId2);
    }

    // Добавление узла в таблицу маршрутизации
    public void addNode(KademliaNode node) {
        BigInteger distance = xorDistance(this.nodeHash, node.nodeHash);
        routingTable.put(distance, node);
        if (routingTable.size() > K) {
            routingTable.pollLastEntry(); // Удаление самых дальних узлов
        }
    }

    // Операция GET: поиск данных по ключу
    public String get(String key) {
        return blockchain;
    }

    // Операция PUT: добавление данных
    public void put(String value) {
        blockchain = value;
    }

    // Поиск ближайшего узла по ключу
    public KademliaNode findClosestNode(String key) {
        BigInteger keyHash = new BigInteger(hash(key), 16);
        BigInteger closestDistance = null;
        KademliaNode closestNode = null;

        for (BigInteger nodeDistance : routingTable.keySet()) {
            if (closestDistance == null || nodeDistance.subtract(keyHash).abs().compareTo(closestDistance.subtract(keyHash).abs()) < 0) {
                closestDistance = nodeDistance;
                closestNode = routingTable.get(nodeDistance);
            }
        }
        return closestNode;
    }

    public List<String> getAllNodes() {
        List<String> nodeNames = new ArrayList<>();
        for (Map.Entry<BigInteger, KademliaNode> entry : routingTable.entrySet()) {
            nodeNames.add(entry.getValue().ipAddress);
        }
        return nodeNames;
    }
}

