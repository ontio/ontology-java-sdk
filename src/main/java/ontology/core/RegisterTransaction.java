package ontology.core;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import org.bouncycastle.math.ec.ECPoint;

import ontology.common.Fixed8;
import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.core.scripts.Program;
import ontology.crypto.ECC;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.json.JNumber;
import ontology.io.json.JObject;
import ontology.io.json.JString;
import ontology.core.contract.Contract;


//注册资产交易
public class RegisterTransaction extends Transaction {

	public String name;
	public String description;
	public byte precision;
	public AssetType assetType;
	public RecordType recordType;
	public Fixed8 amount;
	public ECPoint issuer;
	public UInt160 admin;
	
	public RegisterTransaction() {
		super(TransactionType.RegisterTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			name = reader.readVarString();
			description = reader.readVarString();
			precision = reader.readByte();
			assetType = AssetType.valueOf(reader.readByte());
			recordType = RecordType.valueOf(reader.readByte());
	        amount = reader.readSerializable(Fixed8.class);
	        byte[] xx = reader.readVarBytes();
	        byte[] yy = reader.readVarBytes();
	        admin = reader.readSerializable(UInt160.class);
            issuer = ECC.secp256r1.getCurve().createPoint(
	        		new BigInteger(1,xx), new BigInteger(1,yy));
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarString(name);
        writer.writeVarString(description);
        writer.writeByte(precision);
        writer.writeByte(assetType.value());
        writer.writeByte(recordType.value());
        writer.writeSerializable(amount);
        writer.writeVarBytes(Helper.removePrevZero(issuer.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(issuer.getYCoord().toBigInteger().toByteArray()));
//        writer.writeVarBytes(issuer.getXCoord().toBigInteger().toByteArray());
//        writer.writeVarBytes(issuer.getYCoord().toBigInteger().toByteArray());
        writer.writeSerializable(admin);
	}
	
	//获取验证脚本Hash

	@Override
	public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(issuer)));
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
	@Override
    public JObject json() {
        JObject json = super.json();
        json.set("Asset", new JObject());
        json.get("Asset").set("Name", new JString(name));
        json.get("Asset").set("Precision", new JNumber(precision));
        json.get("Asset").set("AssetType", new JString(String.valueOf(assetType.value())));
        json.get("Asset").set("RecordType", new JString(String.valueOf(recordType)));
        json.set("Amount", new JNumber(amount.toLong()));
        json.set("Issuer", new JObject());
        json.get("Issuer").set("X", new JString(issuer.getXCoord().toBigInteger().toString()));
        json.get("Issuer").set("Y", new JString(issuer.getYCoord().toBigInteger().toString()));
        json.set("Controller", new JString(admin.toString()));
        return json;
    }

	@Override
	public String toString() {
		return "RegisterTransaction [name=" + name + "]";
	}
	
}
