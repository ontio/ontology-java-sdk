package com.github.ontio.core.asset;

import com.github.ontio.common.Address;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.*;
import com.github.ontio.io.json.JObject;

import java.io.IOException;

/**
 *  脚本
 */
public class TokenTransfer implements Serializable, JsonSerializable {
    public Address constracHash;
    public State[] states;

    public TokenTransfer(Address constracHash, State[] states){
        this.constracHash = constracHash;
        this.states = states;
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
        writer.writeSerializable(constracHash);
        writer.writeSerializableArray(states);
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

    public static Address toScriptHash(byte[] script) {
    	return new Address(Digest.hash160(script));
    }

    @Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
//		code = Helper.hexToBytes(json.get("Code").asString());
//		parameter = Helper.hexToBytes(json.get("Parameter").asString());
	}
}
