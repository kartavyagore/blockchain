import java.security.*;
public class TransactionOutput {
    public String id;
    public PublicKey receiver;
    public float value;
    public String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey receiver, float value, String parentTransactionId){
        this.receiver = receiver;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(receiver)+Float.toString(value)+parentTransactionId);
    }

    public boolean ismine(PublicKey publicKey){
        return (publicKey == receiver);
    }
}
