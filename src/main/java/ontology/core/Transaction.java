package ontology.core;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.*;

import ontology.common.*;
import ontology.core.payload.RegisterTransaction;
import ontology.core.scripts.Program;
import ontology.io.*;
import ontology.io.json.*;

/**
 *  交易
 */
public abstract class Transaction extends Inventory implements JsonSerializable {
	/**
	 * 交易类型
	 */
	public final TransactionType type;
	/**
	 * 版本
	 */
	public byte version = 0;

	/**
	 * 交易属性
	 */
	public TransactionAttribute[] attributes;
	/**
	 * 交易资产来源
	 */
	public TransactionInput[] inputs;
	/**
	 * 交易资产去向
	 */
	public TransactionOutput[] outputs;
	public long systemFee;
	public long networkFee;
	/**
	 * 验证脚本
	 */
	public Program[] scripts;
	
	protected Transaction(TransactionType type) {
		this.type = type;
	}
	
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		deserializeUnsigned(reader);
		try {
			scripts = reader.readSerializableArray(Program.class);
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		onDeserialized();
	}
	@Override
	public void deserializeUnsigned(BinaryReader reader) throws IOException {
        if (type.value() != reader.readByte()) { // type
            throw new IOException();
        }
        deserializeUnsignedWithoutType(reader);
	}

	private void deserializeUnsignedWithoutType(BinaryReader reader) throws IOException {
        try {
            version = reader.readByte();
            deserializeExclusiveData(reader);
			attributes = reader.readSerializableArray(TransactionAttribute.class);
	        inputs = reader.readSerializableArray(TransactionInput.class);
	        TransactionInput[] inputs_all = getAllInputs().toArray(TransactionInput[]::new);
	        for (int i = 1; i < inputs_all.length; i++) {
	            for (int j = 0; j < i; j++) {
	                if (inputs_all[i].prevHash == inputs_all[j].prevHash && inputs_all[i].prevIndex == inputs_all[j].prevIndex) {
	                    throw new IOException();
	                }
	            }
	        }
	        outputs = reader.readSerializableArray(TransactionOutput.class);
	        systemFee = reader.readLong();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}
	
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
	}
	
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
        serializeUnsigned(writer);
        writer.writeSerializableArray(scripts);
	}
	
	@Override
	public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.writeByte(type.value());
        writer.writeByte(version);
        serializeExclusiveData(writer);
        writer.writeSerializableArray(attributes);
        writer.writeSerializableArray(inputs);
        writer.writeSerializableArray(outputs);
        writer.writeLong(systemFee);
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
			transaction.scripts = reader.readSerializableArray(Program.class);
			return transaction;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new IOException(ex);
		}
	}
	
	public Stream<TransactionInput> getAllInputs() {
		return Arrays.stream(inputs);
	}
	
	public Stream<TransactionOutput> getAllOutputs() {
		return Arrays.stream(outputs);
	}
	
	/**
	 * 获取验证脚本
	 */
	@Override
	public UInt160[] getScriptHashesForVerifying() {
        if (references() == null){
        	throw new IllegalStateException();
		}
        HashSet<UInt160> hashes = new HashSet<UInt160>(getAllInputs().map(p -> references().get(p).scriptHash).collect(Collectors.toList()));
        for (Entry<UInt256, List<TransactionOutput>> group : getAllOutputs().collect(Collectors.groupingBy(p -> p.assetId)).entrySet()) {
            Transaction tx;
			try {
				tx = Blockchain.current().getTransaction(group.getKey());
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
            if (tx == null || !(tx instanceof RegisterTransaction)) {
            	throw new IllegalStateException();
            }
        }
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
    public TransactionResult[] getTransactionResults() {
        if (references() == null){
        	return null;
		}
        Stream<TransactionResult> in = references().values().stream().map(p -> new TransactionResult(p.assetId, p.value));
        Stream<TransactionResult> out = getAllOutputs().map(p -> new TransactionResult(p.assetId, p.value.negate()));
        Map<UInt256, Fixed8> results = Stream.concat(in, out).collect(Collectors.toMap(p -> p.assetId, p -> p.amount, (a, b) -> a.add(b)));
        return results.entrySet().stream().filter(p -> !p.getValue().equals(Fixed8.ZERO)).map(p -> new TransactionResult(p.getKey(), p.getValue())).toArray(TransactionResult[]::new);
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
		json.set("UTXOInputs", new JArray(Arrays.stream(inputs).map(p -> p.json()).toArray(JObject[]::new)));
		json.set("Outputs", new JArray(IntStream.range(0, outputs.length).boxed().map(i -> outputs[i].json(i)).toArray(JObject[]::new)));
		json.set("Programs", new JArray(Arrays.stream(scripts).map(p -> p.json()).toArray(JObject[]::new)));
		return json;
	}
	
	
	protected void onDeserialized() throws IOException {
	}
	
    //[NonSerialized]
    private Map<TransactionInput, TransactionOutput> _references = null;
    public Map<TransactionInput, TransactionOutput> references() {
        if (_references == null) {
        	Map<TransactionInput, TransactionOutput> map = new HashMap<TransactionInput, TransactionOutput>();
            for (Entry<UInt256, List<TransactionInput>> entry : getAllInputs().collect(Collectors.groupingBy(p -> p.prevHash)).entrySet()) {
                Transaction tx;
				try {
					tx = Blockchain.current().getTransaction(entry.getKey());
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
                if (tx == null) {
                	return null;
                }
                for (TransactionInput input : entry.getValue()) {
                    map.put(input, tx.outputs[input.prevIndex]);
                }
            }
            _references = map;
        }
        return _references;
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
			inputs = new TransactionInput[0];
		} else {
			count = array.size();
			try {
				inputs = reader.readSerializableArray(TransactionInput.class, count, "UTXOInputs");
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				throw new RuntimeException("Failed to fromJson at inputs");
			}
		}
		array = (JArray) json.get("Outputs");
		if(array == null || array.size() == 0) {
			outputs = new TransactionOutput[0];
		} else {
			count = array.size();
			try {
				outputs = reader.readSerializableArray(TransactionOutput.class, count, "Outputs");
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				throw new RuntimeException("Failed to fromJson at outputs");
			}
		}
		array = (JArray) json.get("Programs");
		if(array == null || array.size() == 0) {
			scripts = new Program[0];
		} else {
			count = array.size();
			try {
				scripts = reader.readSerializableArray(Program.class, count, "Programs");
			} catch (InstantiationException | IllegalAccessException | IOException e) {
				throw new RuntimeException("Failed to fromJson at scripts");
			}
		}
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
