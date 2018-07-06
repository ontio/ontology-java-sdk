package demo.vmtest.utils;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class VmReader {
    ByteArrayInputStream ms;
    public BinaryReader reader;
    public byte[] code;

    public VmReader(byte[] bys) {
        ms = new ByteArrayInputStream(bys);
        reader = new BinaryReader(ms);
        code = bys;
    }

    public BinaryReader Reader() {
        return reader;
    }

    public byte ReadByte() {
        try {
            return reader.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean ReadBool() {
        try {
            byte b = reader.readByte();
            return b == 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] ReadBytes(int count) {
        try {
            return reader.readBytes(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] readVarBytes() {
        try {
            return reader.readVarBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int ReadUint16() throws Exception {
        return reader.readInt();
    }

    public int ReadUInt32() throws Exception {
        return reader.readInt();
    }

    public long ReadUInt64() throws Exception {
        return reader.readLong();
    }

    public int Position() {
        try {
            return code.length - reader.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int Length() {
        return code.length;
    }

    public int readVarInt() {
        try {
            return (int) reader.readVarInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int readInt16() {
        try {
            return Integer.parseInt(Helper.reverse(Helper.toHexString(reader.readBytes(2))), 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long readVarInt(long max) {
        try {
            return reader.readVarInt(max);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long Seek(long offset) {
        try {
            return reader.Seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
