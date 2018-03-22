package ontology.core.asset;

import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.common.UInt256;
import ontology.crypto.Digest;
import ontology.crypto.ECC;
import ontology.io.*;
import ontology.io.json.JObject;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

/**
 *  脚本
 */
public class Fee implements Serializable, JsonSerializable {
    public long amount;
    public UInt160 payer;

    public Fee(long amount,UInt160 payer){
        this.amount = amount;
        this.payer = payer;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        amount = reader.readLong();
        try {
            payer = reader.readSerializable(UInt160.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeLong(amount);
        writer.writeSerializable(payer);
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
