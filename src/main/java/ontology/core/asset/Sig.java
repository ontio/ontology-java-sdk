package ontology.core.asset;

import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.crypto.Digest;
import ontology.crypto.ECC;
import ontology.io.*;
import ontology.io.json.JObject;
import ontology.io.json.JString;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

/**
 *  脚本
 */
public class Sig implements Serializable, JsonSerializable {
    public ECPoint[] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	long len = reader.readVarInt();
        pubKeys = new ECPoint[(int)len];
        for(int i=0;i<pubKeys.length;i++) {
            pubKeys[i] = ECC.secp256r1.getCurve().createPoint(
                    new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
        }
        len = reader.readVarInt();
        for(int i=0;i<sigData.length;i++) {
            sigData[i] = reader.readVarBytes();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeVarInt(pubKeys.length);
    	for(int i=0;i<pubKeys.length;i++) {
            writer.writeVarBytes(Helper.removePrevZero(pubKeys[i].getXCoord().toBigInteger().toByteArray()));
            writer.writeVarBytes(Helper.removePrevZero(pubKeys[i].getYCoord().toBigInteger().toByteArray()));
        }
        writer.writeVarInt(M);
        writer.writeVarInt(sigData.length);
        for (int i = 0; i < sigData.length; i++) {
            writer.writeVarBytes(sigData[i]);
        }
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
