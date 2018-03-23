package com.github.ontio.core;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.*;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.JsonReader;
import com.github.ontio.io.JsonSerializable;
import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;

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
	public final TransactionType txType;
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
	public Sig[] sigs = new Sig[0];
	
	protected Transaction(TransactionType type) {
		this.txType = type;
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
        if (txType.value() != reader.readByte()) {
            throw new IOException();
        }
        deserializeUnsignedWithoutType(reader);
	}

	private void deserializeUnsignedWithoutType(BinaryReader reader) throws IOException {
        try {
            deserializeExclusiveData(reader);
			attributes = reader.readSerializableArray(TransactionAttribute.class);
			int len = (int)reader.readVarInt();
			fee = new Fee[len];
			for(int i=0;i<len;i++){
				fee[i] = new Fee(reader.readLong(),reader.readSerializable(Address.class));
			}
	        networkFee = reader.readLong();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}

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
		writer.writeByte(txType.value());
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
			reader.readByte();//read version
            TransactionType type = TransactionType.valueOf(reader.readByte());
			System.out.println(type.toString());
			String typeName = "com.github.ontio.core.payload." + type.toString();
			Transaction transaction = (Transaction)Class.forName(typeName).newInstance();
			reader.readInt();//nonce
            transaction.deserializeUnsignedWithoutType(reader);

			transaction.sigs = new Sig[(int)reader.readVarInt()];
			for(int i=0;i<transaction.sigs.length;i++){
				transaction.sigs[i] = reader.readSerializable(Sig.class);
			}
			return transaction;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * 获取验证脚本
	 */
	@Override
	public Address[] getAddressU160ForVerifying() {
		HashSet<Address> hashes = new HashSet<Address>(Arrays.stream(this.fee).map(p->p.payer).collect(Collectors.toList()));
        return hashes.stream().sorted().toArray(Address[]::new);
	}

	@Override
	public final InventoryType inventoryType() {
		return InventoryType.TX;
	}
	
	public JObject json() {
        JObject json = new JObject();
        json.set("txid", new JString(hash().toString()));
		json.set("TxType", new JString(txType.toString()));
		json.set("Version", new JString(String.valueOf(version)));
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
	
//	@Override
// 	public void fromJson(JsonReader reader) {
//		JObject json = reader.json();
//		if(txType.value() != (byte)json.get("TxType").asNumber()) {
//			throw new RuntimeException();
//		}
//		version = (byte)json.get("Version").asNumber();
//		nonce = new Double(json.get("Nonce").asNumber()).intValue();
//
//		JArray array = (JArray) json.get("Attributes");
//		int count = -1;
//		if(array == null || array.size() == 0) {
//			attributes = new TransactionAttribute[0];
//		} else {
//			count = array.size();
//			try {
//				attributes = reader.readSerializableArray(TransactionAttribute.class, count, "Attributes");
//			} catch (InstantiationException | IllegalAccessException | IOException e) {
//				throw new RuntimeException("Failed to fromJson at attributes");
//			}
//		}
//		array = (JArray) json.get("Fee");
//		if(array == null || array.size() == 0) {
//			fee = new Fee[0];
//		} else {
//			count = array.size();
//			try {
//				fee = new Fee[count];
//				for(int i=0; i<count; i++) {
//					fee[i] = Fee.fromJsonD(new JsonReader(array.get(i)));
//				}
//				//fee = reader.readSerializableArray(Fee.class, count, "Fee");
//			} catch (Exception e) {
//				throw new RuntimeException("Failed to fromJson at fee");
//			}
//		}
//
//		array = (JArray) json.get("Sigs");
//		if(array == null || array.size() == 0) {
//			sigs = new Sig[0];
//		} else {
//			count = array.size();
//			try {
//				sigs = new Sig[count];
//				for(int i=0; i<count; i++) {
//					sigs[i] = Sig.fromJsonD(new JsonReader(array.get(i)));
//				}
//				//sigs = reader.readSerializableArray(Sig.class, count, "Sigs");
//			} catch (Exception e) {
//				throw new RuntimeException("Failed to fromJson at fee");
//			}
//		}
//
//		fromJsonExclusiveData(new JsonReader(json.get("Payload")));
//
//	}
	protected void fromJsonExclusiveData(JsonReader reader) {
	}
	
//	public static Transaction fromJsonD(JsonReader reader) throws IOException {
//        try {
//			System.out.println(reader.json());
//			TransactionType type = TransactionType.valueOf(new Double(reader.json().get("TxType").asNumber()).byteValue());
//            String typeName = "com.github.ontio.core.payload." + type.toString();
//			Transaction transaction = (Transaction)Class.forName(typeName).newInstance();
//            transaction.fromJson(reader);;
//			return transaction;
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//			ex.printStackTrace();
//			throw new IOException(ex);
//		}
//	}

}
