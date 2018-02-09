package ontology.core;

import ontology.core.scripts.Program;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.common.Inventory;
import ontology.common.InventoryType;
import ontology.common.UInt160;
import ontology.account.Acct;
import ontology.core.contract.Contract;
import ontology.sdk.info.account.AccountInfo;

import java.io.IOException;
import java.util.HashSet;


public class DataSignature extends Inventory {
    private Acct acct;
    private AccountInfo acctInfo;
    private String data;
    private String algrithem;
    public DataSignature(){
    }
    public DataSignature(String data){
        this.data = data;
    }
    public DataSignature(String alg, Acct acct, String data){
        this.algrithem = alg;
        this.acct = acct;
        this.data = data;
    }
    public String getData(){
        return data;
    }
    public byte[] signature() {
        try {
            SignatureContext context = new SignatureContext(this);
            byte[] signData = context.signable.sign(acct,algrithem);
            return signData;
        } catch (Exception e) {
            throw new RuntimeException("Data signature error.");
        }
    }
    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public final InventoryType inventoryType() {
        return InventoryType.TX;
    }

    @Override
    public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>();
        hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(acct.publicKey)));
        return hashes.stream().sorted().toArray(UInt160[]::new);
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    }

    @Override
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
    }

    @Override
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.write(data.getBytes());
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    }
}
