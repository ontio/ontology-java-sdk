package com.github.neo.core;


import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class TransactionAttribute implements Serializable {
	/**
	 *
	 */
	public TransactionAttributeUsage usage;
	/**
	 *
	 */
	public byte[] data;
	
	/**
	 *
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		// usage
        writer.writeByte(usage.value());
        // data
		if (usage == TransactionAttributeUsage.Script){
			writer.write(data);
		}else if( usage == TransactionAttributeUsage.DescriptionUrl
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
        if (usage == TransactionAttributeUsage.Script){
			data = reader.readBytes(20);
		}else if(usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
        			data = reader.readVarBytes(255);
        } else {
            throw new IOException();
        }
	}

	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}
}
