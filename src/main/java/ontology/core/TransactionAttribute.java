package ontology.core;

import java.io.IOException;
import java.util.Arrays;

import ontology.common.Helper;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.JsonReader;
import ontology.io.JsonSerializable;
import ontology.io.Serializable;
import ontology.io.json.JObject;
import ontology.io.json.JString;

/**
 *  交易属性
 */
public class TransactionAttribute implements Serializable, JsonSerializable {
	/**
	 * 用途
	 */
	public TransactionAttributeUsage usage;
	/**
	 * 描述
	 */
	public byte[] data;
	
	/**
	 * byte格式数据反序列化
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		// usage
        writer.writeByte(usage.value());
        // data
        if (usage == TransactionAttributeUsage.Script 
        		|| usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
            writer.writeVarBytes(data);
        } else {
            throw new IOException();
        }
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		// usage
		usage = TransactionAttributeUsage.valueOf(reader.readByte());
		// data
        if (usage == TransactionAttributeUsage.Script
        		|| usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
        			data = reader.readVarBytes(255);
        } else {
            throw new IOException();
        }
	}
	
	public JObject json() {
        JObject json = new JObject();
        json.set("usage", new JString(usage.toString()));
        json.set("data", new JString(Helper.toHexString(data)));
        return json;
	}
	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}
	
	@Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		usage = TransactionAttributeUsage.valueOf((byte)json.get("Usage").asNumber());
		data = Helper.hexToBytes(json.get("Data").asString());
	}
}
