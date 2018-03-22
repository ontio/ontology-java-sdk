package ontology.core;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import ontology.common.*;
import ontology.core.asset.Fee;
import ontology.core.asset.Sig;
import ontology.core.scripts.Program;
import ontology.io.*;
import ontology.io.json.*;

/**
 *  交易
 */
public abstract class Transaction extends Inventory implements JsonSerializable {

	/**
	 * 版本
	 */
	public byte version = 0;
	/**
	 * 交易类型
	 */
	public final TransactionType type;
	public int nonce = new Random().nextInt();
	/**
	 * 交易属性
	 */
	public TransactionAttribute[] attributes;
	public Fee[] fee = new Fee[0];
	public long networkFee;
	/**
	 * 验证脚本
	 */
	public Program[] scripts;
	public Sig[] sigs = new Sig[0];
	
	protected Transaction(TransactionType type) {
		this.type = type;
	}
	public void setSigs(Sig[] sigs){
		this.sigs = sigs;
		if(this.sigs == null){
			this.sigs = new Sig[0];
		}
	}
	public void setFees(Fee[] fee){
		this.fee = fee;
		if(this.fee == null){
			this.fee = new Fee[0];
		}
		System.out.println(this.fee.length);
	}
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		deserializeUnsigned(reader);
		try {
			sigs = reader.readSerializableArray(Sig.class);
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		onDeserialized();
	}
	@Override
	public void deserializeUnsigned(BinaryReader reader) throws IOException {
		version = reader.readByte();
        if (type.value() != reader.readByte()) { // type
            throw new IOException();
        }
        deserializeUnsignedWithoutType(reader);
	}

	private void deserializeUnsignedWithoutType(BinaryReader reader) throws IOException {
        try {
            deserializeExclusiveData(reader);
			attributes = reader.readSerializableArray(TransactionAttribute.class);
	        fee = reader.readSerializableArray(Fee.class);
	        networkFee = reader.readLong();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}
//	public byte[][] GetSignatureAddresses() {
		//for(int i=0;i < Sig.)
//	}
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
	}
	
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
        serializeUnsigned(writer);
        writer.writeSerializableArray(sigs);
	}
	
	@Override
	public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.writeByte(version);
		writer.writeByte(type.value());
		writer.writeInt(nonce);
        serializeExclusiveData(writer);
        writer.writeSerializableArray(attributes);
		writer.writeSerializableArray(fee);
		writer.writeLong(networkFee);
	}
	
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Transaction)) {
			return false;
		}
		Transaction tx = (Transaction)obj;
		return hash().equals(tx.hash());
	}
	
	@Override
	public int hashCode() {
		return hash().hashCode();
	}
	
	/**
     * 反序列化Transaction(static)
     */
	public static Transaction deserializeFrom(byte[] value) throws IOException {
		return deserializeFrom(value, 0);
	}
	
	public static Transaction deserializeFrom(byte[] value, int offset) throws IOException {
		try (ByteArrayInputStream ms = new ByteArrayInputStream(value, offset, value.length - offset)) {
			try (BinaryReader reader = new BinaryReader(ms)) {
				return deserializeFrom(reader);
			}
		}
	}

	public static Transaction deserializeFrom(BinaryReader reader) throws IOException {
        try {
            TransactionType type = TransactionType.valueOf(reader.readByte());
            String typeName = "ontology.core." + type.toString();
            Transaction transaction = (Transaction)Class.forName(typeName).newInstance();
            transaction.deserializeUnsignedWithoutType(reader);
			//transaction.scripts = reader.readSerializableArray(Program.class);
			transaction.fee = reader.readSerializableArray(Fee.class);
			return transaction;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * 获取验证脚本
	 */
	@Override
	public UInt160[] getAddressU160ForVerifying() {
		HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.stream(this.fee).map(p->p.payer).collect(Collectors.toList()));
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}

	@Override
	public final InventoryType inventoryType() {
		return InventoryType.TX;
	}
	
	public JObject json() {
        JObject json = new JObject();
        json.set("txid", new JString(hash().toString()));
		json.set("TxType", new JString(type.toString()));
		json.set("PayloadVersion", new JString(String.valueOf(version)));
		json.set("Attributes", new JArray(Arrays.stream(attributes).map(p -> p.json()).toArray(JObject[]::new)));
//		json.set("UTXOInputs", new JArray(Arrays.stream(inputs).map(p -> p.json()).toArray(JObject[]::new)));
//		json.set("Outputs", new JArray(IntStream.range(0, outputs.length).boxed().map(i -> outputs[i].json(i)).toArray(JObject[]::new)));
//		json.set("Programs", new JArray(Arrays.stream(scripts).map(p -> p.json()).toArray(JObject[]::new)));
		return json;
	}
	
	
	protected void onDeserialized() throws IOException {
	}
	

	
	/**
	 * 系统费用
	 */
	public Fixed8 systemFee() {
		return Fixed8.ZERO;
	}
	
	/**
	 * 校验
	 */
	@Override
	public boolean verify() {
		return true;
	}
	
	@Override
 	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		if(type.value() != (byte)json.get("TxType").asNumber()) {
			throw new RuntimeException();
		}
		version = (byte)json.get("PayloadVersion").asNumber();
//		nonce = (long)json.get("Nonce").asNumber();
		
		JArray array = (JArray) json.get("Attributes");
		int count = -1;
		if(array == null || array.size() == 0) {
			attributes = new TransactionAttribute[0];
		} else {
			count = array.size();
			try {
				attributes = reader.readSerializableArray(TransactionAttribute.class, count, "Attributes");
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				throw new RuntimeException("Failed to fromJson at attributes");
			}
		}
		array = (JArray) json.get("UTXOInputs");
		if(array == null || array.size() == 0) {
//			inputs = new TransactionInput[0];
		} else {
			count = array.size();
//			try {
//				inputs = reader.readSerializableArray(TransactionInput.class, count, "UTXOInputs");
//			} catch (InstantiationException | IllegalAccessException | IOException e) {
//				throw new RuntimeException("Failed to fromJson at inputs");
//			}
		}
		array = (JArray) json.get("Outputs");
		if(array == null || array.size() == 0) {
//			outputs = new TransactionOutput[0];
		} else {
			count = array.size();
//			try {
//				outputs = reader.readSerializableArray(TransactionOutput.class, count, "Outputs");
//			} catch (InstantiationException | IllegalAccessException | IOException e) {
//				throw new RuntimeException("Failed to fromJson at outputs");
//			}
		}
		array = (JArray) json.get("Programs");
//		if(array == null || array.size() == 0) {
//			scripts = new Program[0];
//		} else {
//			count = array.size();
//			try {
//				scripts = reader.readSerializableArray(Program.class, count, "Programs");
//			} catch (InstantiationException | IllegalAccessException | IOException e) {
//				throw new RuntimeException("Failed to fromJson at scripts");
//			}
//		}
		fromJsonExclusiveData(new JsonReader(json.get("Payload")));
		
	}
	protected void fromJsonExclusiveData(JsonReader reader) {
	}
	
	public static Transaction fromJsonD(JsonReader reader) throws IOException {
        try {
            TransactionType type = TransactionType.valueOf((byte)reader.json().get("TxType").asNumber());
            String typeName = "DNA.Core." + type.toString();
            Transaction transaction = (Transaction)Class.forName(typeName).newInstance();
            transaction.fromJson(reader);;
			return transaction;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

}
