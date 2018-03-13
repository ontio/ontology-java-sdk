package ontology.core.payload;

import java.io.IOException;

import ontology.core.Transaction;
import ontology.core.TransactionType;
import ontology.core.code.FunctionCode;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;

public class DeployCodeTransaction extends Transaction {
	public FunctionCode code;
	//public byte[] params;
	public byte vmType;
	public boolean needStorage;
	public String name;
	public String codeVersion;
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
			if(code == null){
				code = new FunctionCode(null,null,null);
			}
			code.deserialize(reader);
			//params = reader.readVarBytes();
			vmType = reader.readByte();
			needStorage = reader.readBoolean();
			name = reader.readVarString();
			codeVersion = reader.readVarString();
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
		code.serialize(writer);
//		writer.writeVarBytes(params);
		writer.writeByte(vmType);
		writer.writeBoolean(needStorage);
		writer.writeVarString(name);
		writer.writeVarString(codeVersion);
		writer.writeVarString(author);
		writer.writeVarString(email);
		writer.writeVarString(description);
//		writer.writeByte(language);
//		writer.writeSerializable(programHash);
	}
}
