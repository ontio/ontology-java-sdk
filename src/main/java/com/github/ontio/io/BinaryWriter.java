package com.github.ontio.io;

import java.io.*;
import java.nio.*;

import org.bouncycastle.math.ec.ECPoint;

public class BinaryWriter implements AutoCloseable {
	private DataOutputStream writer;
	private byte[] array = new byte[8];
	private ByteBuffer buffer = ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN);
	
	public BinaryWriter(OutputStream stream) {
		this.writer = new DataOutputStream(stream);
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
	
	public void write(byte[] buffer) throws IOException {
		writer.write(buffer);
	}

	public void write(byte[] buffer, int index, int length) throws IOException {
		writer.write(buffer, index, length);
	}
	
	public void writeBoolean(boolean v) throws IOException {
		writer.writeBoolean(v);
	}
	
	public void writeByte(byte v) throws IOException {
		writer.writeByte(v);
	}
	
	public void writeDouble(double v) throws IOException {
		buffer.putDouble(0, v);
		writer.write(array, 0, 8);
	}
	
	public void writeECPoint(ECPoint v) throws IOException {
		writer.write(v.getEncoded(true));
	}
	
	public void writeFixedString(String v, int length) throws IOException {
		if (v == null) {
			throw new IllegalArgumentException();
		}
		if (v.length() > length) {
			throw new IllegalArgumentException();
		}
		byte[] bytes = v.getBytes("UTF-8");
		if (bytes.length > length) {
			throw new IllegalArgumentException();
		}
		writer.write(bytes);
		if (bytes.length < length) {
			writer.write(new byte[length - bytes.length]);
		}
	}
	
	public void writeFloat(float v) throws IOException {
		buffer.putFloat(0, v);
		writer.write(array, 0, 4);
	}
	
	public void writeInt(int v) throws IOException {
		buffer.putInt(0, v);
		writer.write(array, 0, 4);
	}
	
	public void writeLong(long v) throws IOException {
		buffer.putLong(0, v);
		writer.write(array, 0, 8);
	}
	
	public void writeSerializable(Serializable v) throws IOException {
		v.serialize(this);
	}
	
	public void writeSerializableArray(Serializable[] v) throws IOException {
		writeVarInt(v.length);
		for (int i = 0; i < v.length; i++) {
			v[i].serialize(this);
		}
	}
	
	public void writeSerializableArray2(Serializable[] v) throws IOException {
		writeInt(v.length);
		for (int i = 0; i < v.length; i++) {
			v[i].serialize(this);
		}
	}
	
	public void writeShort(short v) throws IOException {
		buffer.putShort(0, v);
		writer.write(array, 0, 2);
	}
	
	public void writeVarBytes(byte[] v) throws IOException {
		writeVarInt(v.length);
		writer.write(v);
	}
	
	public void writeVarInt(long v) throws IOException {
        if (v < 0) {
            throw new IllegalArgumentException();
        }
        if (v < 0xFD) {
            writeByte((byte)v);
        } else if (v <= 0xFFFF) {
            writeByte((byte)0xFD);
            writeShort((short)v);
        } else if (v <= 0xFFFFFFFF) {
        	writeByte((byte)0xFE);
            writeInt((int)v);
        } else {
            writeByte((byte)0xFF);
            writeLong(v);
        }
	}
	
	public void writeVarString(String v) throws IOException {
		writeVarBytes(v.getBytes("UTF-8"));
	}
}
