package ontology.core;

import ontology.common.Fixed8;
import ontology.common.UInt160;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Claim extends Transaction {
	public TransactionInput[] claims;

	public Claim() {
		super(TransactionType.Claim);
	}
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			claims = reader.readSerializableArray(TransactionInput.class);
			TransactionInput[] inputs_all = getAllInputs().toArray(TransactionInput[]::new);
			for (int i = 1; i < inputs_all.length; i++) {
				for (int j = 0; j < i; j++) {
					if (inputs_all[i].prevHash == inputs_all[j].prevHash && inputs_all[i].prevIndex == inputs_all[j].prevIndex) {
						throw new IOException();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeSerializableArray2(claims);
	}
	@Override
	public UInt160[] getScriptHashesForVerifying() {
		HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
		for (TransactionResult result : Arrays.stream(getTransactionResults()).filter(p -> p.amount.compareTo(Fixed8.ZERO) < 0).toArray(TransactionResult[]::new)) {
			Transaction tx;
			try {
				tx = Blockchain.current().getTransaction(result.assetId);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
			if (tx == null || !(tx instanceof RegisterTransaction)) {
				throw new IllegalStateException();
			}
			RegisterTransaction asset = (RegisterTransaction)tx;
			hashes.add(asset.admin);
		}
		return hashes.stream().sorted().toArray(UInt160[]::new);
	}
}
