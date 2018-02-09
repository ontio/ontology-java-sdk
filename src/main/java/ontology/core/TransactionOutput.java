package ontology.core;

import java.io.IOException;

import ontology.io.*;
import ontology.io.json.*;
import ontology.common.Fixed8;
import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.common.UInt256;

/**
 *  交易输出
 */
public class TransactionOutput implements Serializable, JsonSerializable {
    /**
     *  资产编号
     */
    public UInt256 assetId;
    /**
     *  金额
     */
    public Fixed8 value;
    /**
     *  收款地址
     */
    public UInt160 scriptHash;
    
    /**
	 * byte格式数据反序列化
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeSerializable(assetId);
		writer.writeSerializable(value);
		writer.writeSerializable(scriptHash);
	}
	
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		try {
			assetId = reader.readSerializable(UInt256.class);
			value = reader.readSerializable(Fixed8.class);
			scriptHash = reader.readSerializable(UInt160.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IOException();
		}
	}

    public JObject json(int index) {
        JObject json = new JObject();
        json.set("n", new JNumber(index));
        json.set("asset", new JString(assetId.toString()));
        json.set("value", new JString(value.toString()));
        json.set("high", new JNumber(value.getData() >> 32));
        json.set("low", new JNumber(value.getData() & 0xffffffff));
//        json.set("address", new JString(Wallet.toAddress(scriptHash)));
        return json;
    }
    
	@Override
	public String toString() {
		return "TransactionOutput [assetId=" + assetId + ", value=" + value
				+ ", scriptHash=" + scriptHash + "]";
	}

	@Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		assetId = new UInt256(Helper.hexToBytes(json.get("AssetID").asString()));
		value = new Fixed8((long)json.get("Value").asNumber());
		scriptHash = new UInt160(Helper.hexToBytes(json.get("ProgramHash").asString()));
//		assetId = UInt256.parse(json.get("AssetID").asString());
//		value = new Fixed8((long)json.get("Value").asNumber());
//		scriptHash = UInt160.parse(json.get("ProgramHash").asString());
	}
	
}
