package ontology.core.asset;

import ontology.common.UInt160;
import ontology.crypto.Digest;
import ontology.io.*;
import ontology.io.json.JObject;

import java.io.IOException;
import java.math.BigInteger;

/**
 *  脚本
 */
public class State implements Serializable, JsonSerializable {
    public UInt160 from;
    public UInt160 to;
    public BigInteger value;

    public State( UInt160 from, UInt160 to,BigInteger amount){
        this.from = from;
        this.to = to;
        this.value = amount;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
//        amount = reader.readLong();
//        try {
//            payer = reader.readSerializable(UInt160.class);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeSerializable(from);
        writer.writeSerializable(to);
        writer.writeVarBytes(value.toByteArray());

    }

    /**
     *  变成json对象
     *  <returns>返回json对象</returns>
     */
    public JObject json() {
        JObject json = new JObject();
//        json.set("code", new JString(Helper.toHexString(code)));
//        json.set("parameter", new JString(Helper.toHexString(parameter)));
        return json;
    }

    public static UInt160 toScriptHash(byte[] script) {
    	return new UInt160(Digest.hash160(script));
    }

    @Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
//		code = Helper.hexToBytes(json.get("Code").asString());
//		parameter = Helper.hexToBytes(json.get("Parameter").asString());
	}
}
