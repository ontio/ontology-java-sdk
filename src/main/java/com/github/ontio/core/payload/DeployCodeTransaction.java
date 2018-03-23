package com.github.ontio.core.payload;

import java.io.IOException;

import com.github.ontio.core.TransactionType;
import com.github.ontio.core.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

public class DeployCodeTransaction extends Transaction {
	public byte[] code;
	//public byte[] params;
	public byte vmType;
	public boolean needStorage;
	public String name;
	public String version;
	public String author;
	public String email;
	public String description;
	//public byte language;
	//public UInt160 programHash;
//	public ECPoint pubkey;


	public DeployCodeTransaction() {
		super(TransactionType.DeployCodeTransaction);
	}
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			vmType = reader.readByte();
			code = reader.readVarBytes();
			needStorage = reader.readBoolean();
			name = reader.readVarString();
			version = reader.readVarString();
			author = reader.readVarString();
			email = reader.readVarString();
			description = reader.readVarString();
//			language = reader.readByte();
//			programHash = reader.readSerializable(UInt160.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeByte(vmType);
		writer.writeVarBytes(code);
		writer.writeBoolean(needStorage);
		writer.writeVarString(name);
		writer.writeVarString(version);
		writer.writeVarString(author);
		writer.writeVarString(email);
		writer.writeVarString(description);
//		writer.writeByte(language);
//		writer.writeSerializable(programHash);
	}
}
