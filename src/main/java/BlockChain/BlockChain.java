package BlockChain;

import BlockChain.Block.Block;
import BlockChain.Transaction.TransactionPull.TransactionPull;

import java.util.ArrayList;
import java.util.List;

public class BlockChain {
    private List<Block> chain;

    // Конструктор
    public BlockChain() {
        this.chain = new ArrayList<>();
        // Создаем генезис-блок
        chain.add(createGenesisBlock());
    }

    // Метод для создания генезис-блока
    private Block createGenesisBlock() {
        TransactionPull pull = new TransactionPull();
        return new Block(pull, "0", System.currentTimeMillis(), 2);
    }

    // Метод для получения последнего блока
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public ArrayList<Block> getChain() {
        return (ArrayList<Block>) chain;
    }

    // Метод для добавления нового блока
    public void addBlock(Block block) {
        chain.add(block);
    }

    // Метод для проверки целостности блокчейна
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Проверка хеша блока
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current block hash is invalid at index " + i);
                return false;
            }

            // Проверка предыдущего хеша
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Previous block hash is invalid at index " + i);
                return false;
            }
        }
        return true;
    }

    // Метод для вывода всех блоков
    public void printChain() {
        for (Block block : chain) {
            System.out.println(block);
        }
    }
}
