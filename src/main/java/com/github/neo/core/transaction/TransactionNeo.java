package com.github.neo.core.transaction;



import com.github.neo.core.Program;
import com.github.neo.core.TransactionInput;
import com.github.ontio.account.Account;
import com.github.ontio.common.Fixed8;
import com.github.ontio.common.Helper;
import com.github.ontio.core.Inventory;
import com.github.ontio.core.InventoryType;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.neo.core.TransactionAttribute;
import com.github.neo.core.TransactionOutput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 *
 */
public abstract class TransactionNeo extends Inventory {
	/**
	 *
	 */
	public final TransactionType type;
	/**
	 *
	 */
	public byte version = 0;
	/**
	 *
	 */
	public long nonce;
	/**
	 *
	 */
	public TransactionAttribute[] attributes;
	/**
	 *
	 */
	public TransactionInput[] inputs;
	/**
	 *
	 */
	public TransactionOutput[] outputs;
	/**
	 *
	 */
	public Program[] scripts = new Program[0];
	
	protected TransactionNeo(TransactionType type) {
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
		if (!(obj instanceof TransactionNeo)) {
			return false;
		}
		TransactionNeo tx = (TransactionNeo)obj;
		return hash().equals(tx.hash());
	}
	
	@Override
	public int hashCode() {
		return hash().hashCode();
	}
	
	/**
     * 反序列化Transaction(static)
     */
	public static TransactionNeo deserializeFrom(byte[] value) throws IOException {
		return deserializeFrom(value, 0);
	}
	
	public static TransactionNeo deserializeFrom(byte[] value, int offset) throws IOException {
		try (ByteArrayInputStream ms = new ByteArrayInputStream(value, offset, value.length - offset)) {
			try (BinaryReader reader = new BinaryReader(ms)) {
				return deserializeFrom(reader);
			}
		}
	}

	public static TransactionNeo deserializeFrom(BinaryReader reader) throws IOException {
        try {
            TransactionType type = TransactionType.valueOf(reader.readByte());
            String typeName = "NEO.Core." + type.toString();
			TransactionNeo transaction = (TransactionNeo)Class.forName(typeName).newInstance();
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
	

	@Override
	public final InventoryType inventoryType() {
		return InventoryType.TX;
	}
	

	
	protected void onDeserialized() throws IOException {
	}
	
    //[NonSerialized]
    private Map<TransactionInput, TransactionOutput> _references = null;


	
	/**
	 *
	 */
	@Override
	public boolean verify() {
		return true;
	}

	@Override
	public byte[] sign(Account account, SignatureScheme scheme) throws Exception {
		byte[] bys = account.generateSignature(((getHashData())), scheme, null);
		byte[] signature = new byte[64];
		System.arraycopy(bys, 1, signature, 0, 64);
		return signature;
	}
}
