package com.github.ontio.core.asset;

import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.core.TransactionAttribute;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.*;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.json.JArray;
import com.github.ontio.io.json.JNumber;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *  脚本
 */
public class Sig implements Serializable, JsonSerializable {
    public ECPoint[] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	int len = (int)reader.readVarInt();
        pubKeys = new ECPoint[(int)len];
        for(int i=0;i<pubKeys.length;i++) {
            pubKeys[i] = ECC.secp256r1.getCurve().createPoint(
                    new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
        }
        M = (int)reader.readVarInt();
        len = (int)reader.readVarInt();
        sigData = new byte[len][];
        for(int i=0;i<sigData.length;i++) {
            sigData[i] = reader.readVarBytes();
            System.out.println(Helper.toHexString(sigData[i]));
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


    public static Address toScriptHash(byte[] script) {
    	return new Address(Digest.hash160(script));
    }
    public Object json() {
        Map json = new HashMap<>();
        json.put("M", M);
        json.put("PubKeys", Arrays.stream(pubKeys).map(p->Helper.toHexString(p.getEncoded(true))).toArray(Object[]::new));
        json.put("sigData", Arrays.stream(sigData).map(p->Helper.toHexString(p)).toArray(Object[]::new));
        return json;
    }
//    @Override
//    public void fromJson(JsonReader reader) {
//        JObject json = reader.json();
//        M = new Double(json.get("M").asNumber()).intValue();
//       // pubKeys = Address.parse(json.get("Payer").asString());
//        //JArray array = (JArray) json.get("SigData");
//        //sigData =reader.readSerializableArray(Byte.class, array.size(), "SigData");
//    }
//    public static Sig fromJsonD(JsonReader reader) throws IOException {
//        try {
//            Sig f = (Sig)Class.forName("com.github.ontio.core.asset.Sig").newInstance();
//            f.fromJson(reader);;
//            return f;
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//            ex.printStackTrace();
//            throw new IOException(ex);
//        }
//    }
}
