package com.github.ontio.core.asset;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.Transaction;
import com.github.ontio.core.TransactionType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.*;
import com.github.ontio.io.json.JNumber;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  脚本
 */
public class Fee implements Serializable, JsonSerializable {
    public long amount;
    public Address payer;
    public Fee(){

    }
    public Fee(long amount,Address payer){
        this.amount = amount;
        this.payer = payer;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        amount = reader.readLong();
        try {
            payer = reader.readSerializable(Address.class);
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
    public Object json() {
        Map json = new HashMap<>();
        json.put("Amount", amount);
        json.put("Payer", payer.toHexString());
        return json;
    }

    public static Address toScriptHash(byte[] script) {
    	return new Address(Digest.hash160(script));
    }

//    @Override
//	public void fromJson(JsonReader reader) {
//		JObject json = reader.json();
//		amount = new Double(json.get("Amount").asNumber()).longValue();
//        payer = Address.parse(json.get("Payer").asString());
//	}
//    public static Fee fromJsonD(JsonReader reader) throws IOException {
//        try {
//            Fee f = (Fee)Class.forName("com.github.ontio.core.asset.Fee").newInstance();
//            f.fromJson(reader);;
//            return f;
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//            ex.printStackTrace();
//            throw new IOException(ex);
//        }
//    }
}
