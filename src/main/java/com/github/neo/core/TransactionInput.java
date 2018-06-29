package com.github.neo.core;


import com.github.ontio.common.UInt256;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;

/**
 *
 */
public class TransactionInput implements Serializable {
    /**
     *
     */
    public UInt256 prevHash;
    /**
     *
     */
    public short prevIndex;

    public TransactionInput() {
    }

    public TransactionInput(UInt256 prevHash, int prevIndex) {
        this.prevHash = prevHash;
        this.prevIndex = (short) prevIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (null == obj) {
        	return false;
        }
        if (!(obj instanceof TransactionInput)) {
        	return false;
        }
        TransactionInput other = (TransactionInput) obj;
        return prevHash.equals(other.prevHash) && prevIndex == other.prevIndex;
    }

    @Override
    public int hashCode() {
        return prevHash.hashCode() + prevIndex;
    }

    /**
	 *
	 */
    @Override
	public void deserialize(BinaryReader reader) throws IOException {
		try {
			prevHash = reader.readSerializable(UInt256.class);
			prevIndex = reader.readShort();
//			prevIndex = (short) reader.readVarInt();
		} catch (InstantiationException | IllegalAccessException e) {
		}
	}
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeSerializable(prevHash);
		writer.writeShort(prevIndex);
//		writer.writeVarInt(prevIndex);
	}


	@Override
	public String toString() {
		return "TransactionInput [prevHash=" + prevHash + ", prevIndex="
				+ prevIndex + "]";
	}
}
