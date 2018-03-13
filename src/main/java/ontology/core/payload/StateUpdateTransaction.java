package ontology.core.payload;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import ontology.core.Transaction;
import ontology.core.TransactionType;
import org.bouncycastle.math.ec.ECPoint;

import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.core.scripts.Program;
import ontology.crypto.ECC;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.core.contract.Contract;

public class StateUpdateTransaction extends Transaction {
	public byte[] namespace;
	public byte[] key;
	public byte[] value;
	public ECPoint updater;
	
	public StateUpdateTransaction() {
		super(TransactionType.StateUpdateTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		namespace = reader.readVarBytes();
		key = reader.readVarBytes();
		value = reader.readVarBytes();
		updater = ECC.secp256r1.getCurve().createPoint(
        		new BigInteger(1,reader.readVarBytes()), new BigInteger(1,reader.readVarBytes()));
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeVarBytes(namespace);
		writer.writeVarBytes(key);
		writer.writeVarBytes(value);
		writer.writeVarBytes(Helper.removePrevZero(updater.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(updater.getYCoord().toBigInteger().toByteArray()));
	}
	
	@Override
	public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(updater)));
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
}
