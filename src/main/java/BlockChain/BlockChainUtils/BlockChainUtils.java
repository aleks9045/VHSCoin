package BlockChain.BlockChainUtils;

import BlockChain.Block.Block;
import BlockChain.Transaction.Transaction;
import BlockChain.Transaction.TransactionPull.TransactionPull;
import BlockChain.BlockChain;

import java.util.List;


public class BlockChainUtils {
    private TransactionPull transactionPull;
    public BlockChain blockChain;

    public BlockChainUtils() {
        this.transactionPull = new TransactionPull();
        this.blockChain = new BlockChain();
    }

    // Метод обработки нового блокчейна от другого пира
    public void processIncomingBlockChain(BlockChain incomingBlockChain) {
        // Проверяем целостность нового блокчейна
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

}
