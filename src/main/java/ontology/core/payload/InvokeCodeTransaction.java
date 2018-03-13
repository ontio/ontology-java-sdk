package ontology.core.payload;

import ontology.core.Transaction;
import ontology.core.TransactionType;
import ontology.core.scripts.Program;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.common.UInt160;
import ontology.core.contract.Contract;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class InvokeCodeTransaction extends Transaction {
	public UInt160 codeHash;
	public byte[] code;
	//public UInt160 programHash;
	public ECPoint invoker;

	public InvokeCodeTransaction() {
		super(TransactionType.InvokeCodeTransaction);
	}
	public InvokeCodeTransaction(ECPoint invoker) {
		super(TransactionType.InvokeCodeTransaction);
		this.invoker = invoker;
	}
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			codeHash = reader.readSerializable(UInt160.class);
			code = reader.readVarBytes();
			//programHash = reader.readSerializable(UInt160.class);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeSerializable(codeHash);
		writer.writeVarBytes(code);
		//writer.writeSerializable(programHash);
	}
	@Override
	public UInt160[] getScriptHashesForVerifying() {
		HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
		hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(invoker)));
		return hashes.stream().sorted().toArray(UInt160[]::new);
	}
}
