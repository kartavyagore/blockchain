
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.*;

public class Wallet {
    public PrivateKey privateKey; // password of our wallet
    public PublicKey publicKey; // address of our wallet

    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    //Signature = createSignature(Pvt_key , From+To+Value)
    //verifySignature = verifySignature(pub_key, Signature, From+To+Value) - returns true or false

    public Wallet(){
        generateKeyPair();
    }

    public float getBalance(){
        float total = 0;
        for(Map.Entry<String, TransactionOutput> item : Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.ismine(publicKey)){
                UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _receiver, float value ){
        if(getBalance() < value){
            System.out.println("#Not enough funds to send transaction. Transaction discarded.");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for(Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _receiver, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input:inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }

    public void generateKeyPair(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            //Intialize key generator
            keyGen.initialize(ecSpec, random); //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
