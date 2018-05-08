package com.github.ontio.io;

import com.github.ontio.common.Helper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BinaryReaderTest {

    @Test
    public void readVarBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        binaryWriter.writeVarBytes("12".getBytes());

        ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BinaryReader binaryReader = new BinaryReader(bin);
        byte[] res = binaryReader.readVarBytes();
        assertTrue(Arrays.equals(res,"12".getBytes()));
    }

    @Test
    public void readVarInt() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        binaryWriter.writeVarInt(123);

        ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BinaryReader binaryReader = new BinaryReader(bin);
        long res = binaryReader.readVarInt();
        assertEquals(123,res);
    }

    @Test
    public void readBoolean() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        binaryWriter.writeBoolean(true);

        ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BinaryReader binaryReader = new BinaryReader(bin);
        boolean res = binaryReader.readBoolean();
        assertTrue(res);
    }

    @Test
    public void readByte() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        byte a = 'a';
        binaryWriter.writeByte(a);

        ByteArrayInputStream bin = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BinaryReader binaryReader = new BinaryReader(bin);
        byte res = binaryReader.readByte();
        assertEquals(res,'a');
    }
}