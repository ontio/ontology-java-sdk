package com.github.ontio.core.payload;

import com.github.ontio.core.Transaction;
import com.github.ontio.core.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.common.Address;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class InvokeCodeTransaction extends Transaction {
	public long gasLimit;
	public byte vmType;
	public byte[] code;
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
			gasLimit = reader.readLong();
			vmType = reader.readByte();
			code = reader.readVarBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeLong(gasLimit);
		writer.writeByte(vmType);
		writer.writeVarBytes(code);
	}
	@Override
	public Address[] getAddressU160ForVerifying() {
		HashSet<Address> hashes = new HashSet<Address>(Arrays.asList(super.getAddressU160ForVerifying()));
		if(invoker != null) {
			hashes.add(Address.addressFromPubKey(invoker));
		}
		return hashes.stream().sorted().toArray(Address[]::new);
	}
}
