import java.util.*;

public class Block{
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    public long timeStamp;
    int nonce;

    public Block(String previousHash){

        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    public String calculateHash(){ //calculating latest hash
        String calculatedhash = StringUtil.applySha256(
                  previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                          merkleRoot

        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty){
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring(0,difficulty).equals(target)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction){
        if (transaction == null) return false;
        if ((previousHash != "0")){
            if (transaction.processTransaction() != true){
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block");
        return true;
    }
}