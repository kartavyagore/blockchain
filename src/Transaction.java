import java.security.*;
import java.util.*;
public class Transaction {
    public String transactionId; // this is also the hash of the transaction
    public PublicKey sender; // senders address
    public PublicKey receiver; // receiver address
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // rough count of transactions

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculatehash(){
        sequence++;
        return StringUtil.applySha256(
                  StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(receiver) +
                        Float.toString(value) + sequence
                );
    }

    //Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    //Verifies the data we signed hasnt been tampered with
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction(){
        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for(TransactionInput i : inputs){
            i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }

        if(getInputsValue() < Main.minimumTransaction){
            System.out.println("#Transaction input too small : "+ getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculatehash();
        outputs.add(new TransactionOutput(this.receiver,value,transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));

        for(TransactionOutput o : outputs){
            Main.UTXOs.put(o.id,o);
        }

        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue;//If transaction cant be found skip it.
            Main.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i :inputs){
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput o: outputs){
            total += o.value;
        }
        return total;
    }
}

