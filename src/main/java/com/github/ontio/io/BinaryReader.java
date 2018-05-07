/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.io;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.*;

import com.github.ontio.core.scripts.ScriptOp;
import org.bouncycastle.math.ec.ECPoint;

import com.github.ontio.crypto.ECC;

/*
 ******************************************************************************
 * public func for outside calling
 ******************************************************************************
 * 1. WriteVarUint func, depend on the inpute number's Actual number size,
 *    serialize to bytes.
 *      uint8  =>  (LittleEndian)num in 1 byte                 = 1bytes
 *      uint16 =>  0xfd(1 byte) + (LittleEndian)num in 2 bytes = 3bytes
 *      uint32 =>  0xfe(1 byte) + (LittleEndian)num in 4 bytes = 5bytes
 *      uint64 =>  0xff(1 byte) + (LittleEndian)num in 8 bytes = 9bytes
 * 2. ReadVarUint  func, this func will read the first byte to determined
 *    the num length to read.and retrun the uint64
 *      first byte = 0xfd, read the next 2 bytes as uint16
 *      first byte = 0xfe, read the next 4 bytes as uint32
 *      first byte = 0xff, read the next 8 bytes as uint64
 *      other else,        read this byte as uint8
 * 3. WriteVarBytes func, this func will output two item as serialization.
 *      length of bytes (uint8/uint16/uint32/uint64)  +  bytes
 * 4. WriteString func, this func will output two item as serialization.
 *      length of string(uint8/uint16/uint32/uint64)  +  bytes(string)
 * 5. ReadVarBytes func, this func will first read a uint to identify the
 *    length of bytes, and use it to get the next length's bytes to return.
 * 6. ReadString func, this func will first read a uint to identify the
 *    length of string, and use it to get the next bytes as a string.
 * 7. GetVarUintSize func, this func will return the length of a uint when it
 *    serialized by the WriteVarUint func.
 * 8. ReadBytes func, this func will read the specify lenth's bytes and retun.
 * 9. ReadUint8,16,32,64 read uint with fixed length
 * 10.WriteUint8,16,32,64 Write uint with fixed length
 * 11.ToArray SerializableData to ToArray() func.
 ******************************************************************************
 */
public class BinaryReader implements AutoCloseable {
	private DataInputStream reader;
	private byte[] array = new byte[8];
	private ByteBuffer buffer = ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN);
	
	public BinaryReader(InputStream stream) {
		this.reader = new DataInputStream(stream);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public void read(byte[] buffer) throws IOException {
		reader.readFully(buffer);
	}
	
	public void read(byte[] buffer, int index, int length) throws IOException {
		reader.readFully(buffer, index, length);
	}
	
	public boolean readBoolean() throws IOException {
		return reader.readBoolean();
	}
	
	public byte readByte() throws IOException {
		return reader.readByte();
	}
	
	public byte[] readBytes(int count) throws IOException {
		byte[] buffer = new byte[count];
		reader.readFully(buffer);
		return buffer;
	}
	
	public double readDouble() throws IOException {
		reader.readFully(array, 0, 8);
		return buffer.getDouble(0);
	}
	
	public ECPoint readECPoint() throws IOException {
		byte[] encoded;
		byte fb = reader.readByte();
		switch (fb)
		{
		case 0x00:
			encoded = new byte[1];
			break;
		case 0x02:
		case 0x03:
			encoded = new byte[33];
			encoded[0] = fb;
			reader.readFully(encoded, 1, 32);
			break;
		case 0x04:
			encoded = new byte[65];
			encoded[0] = fb;
			reader.readFully(encoded, 1, 64);
			break;
		default:
			throw new IOException();
		}
		return ECC.secp256r1.getCurve().decodePoint(encoded);
	}
	
	public String readFixedString(int length) throws IOException {
		byte[] data = readBytes(length);
		int count = -1;
		while (data[++count] != 0);
		return new String(data, 0, count, "UTF-8");
	}
	
	public float readFloat() throws IOException {
		reader.readFully(array, 0, 4);
		return buffer.getFloat(0);
	}
	
	public int readInt() throws IOException {
		reader.readFully(array, 0, 4);
		return buffer.getInt(0);
	}
	
	public long readLong() throws IOException {
		reader.readFully(array, 0, 8);
		return buffer.getLong(0);
	}
	
	public <T extends Serializable> T readSerializable(Class<T> t) throws InstantiationException, IllegalAccessException, IOException {
		T obj = t.newInstance();
		obj.deserialize(this);
		return obj;
	}
	
	public <T extends Serializable> T[] readSerializableArray(Class<T> t) throws InstantiationException, IllegalAccessException, IOException {
		@SuppressWarnings("unchecked")
		T[] array = (T[])Array.newInstance(t, (int)readVarInt(0x10000000));
		for (int i = 0; i < array.length; i++) {
			array[i] = t.newInstance();
			array[i].deserialize(this);
		}
		return array;
	}
	
	public short readShort() throws IOException {
		reader.readFully(array, 0, 2);
		return buffer.getShort(0);
	}
	
	public byte[] readVarBytes() throws IOException {
		return readVarBytes(0X7fffffc7);
	}
	public byte[] readVarBytes2() throws IOException {
		return readBytes((int)readVarInt2(0X7fffffc7));
	}
	public byte[] readVarBytes(int max) throws IOException {
		return readBytes((int)readVarInt(max));
	}
	
	public long readVarInt() throws IOException {
		return readVarInt(Long.MAX_VALUE);
	}
	
	public long readVarInt(long max) throws IOException {
        long fb = Byte.toUnsignedLong(readByte());
        long value;
        if (fb == 0xFD) {
            value = Short.toUnsignedLong(readShort());
        } else if (fb == 0xFE) {
            value = Integer.toUnsignedLong(readInt());
        } else if (fb == 0xFF) {
            value = readLong();
        } else {
			value = fb;
        }
        if (Long.compareUnsigned(value, max) > 0) {
        	throw new IOException();
        }
        return value;
	}
	public long readVarInt2(long max) throws IOException {
		long fb = Byte.toUnsignedLong(readByte());
		long value;
		if (fb == ScriptOp.OP_PUSHDATA1.getByte()) {
			value = Byte.toUnsignedLong(readByte());
		} else if (fb == ScriptOp.OP_PUSHDATA2.getByte()) {
			value = Short.toUnsignedLong(readShort());
		} else if (fb == ScriptOp.OP_PUSHDATA4.getByte()) {
			value = Integer.toUnsignedLong(readInt());
		} else{
			value = fb;
		}
		if (Long.compareUnsigned(value, max) > 0) {
			throw new IOException();
		}
		return value;
	}
	public String readVarString() throws IOException {
		return new String(readVarBytes(), "UTF-8");
	}
}
