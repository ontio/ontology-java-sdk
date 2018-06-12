package com.github.ontio.io;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class BinaryWriterTest {

    @Test
    public void writeVarInt() throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(ms);
        binaryWriter.writeVarInt((long) 2544);
        binaryWriter.flush();
        assertNotNull(ms);
    }

    @Test
    public void write() throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(ms);
        binaryWriter.write("test".getBytes());
        binaryWriter.flush();
        assertNotNull(ms);
    }

    @Test
    public void writeInt() throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(ms);
        binaryWriter.writeInt(1);
        binaryWriter.flush();
        assertNotNull(ms);
    }

    @Test
    public void writeSerializable() throws IOException, SDKException {
//        ByteArrayOutputStream ms = new ByteArrayOutputStream();
//        BinaryWriter binaryWriter = new BinaryWriter(ms);
//        Address address = Address.decodeBase58("TA6nRD9DqGkE8xRJaB37bW2KQEz59ovKRH");
//        binaryWriter.writeSerializable(address);
//        binaryWriter.flush();
//        assertNotNull(ms);
    }

    @Test
    public void writeVarBytes() throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(ms);
        binaryWriter.writeVarBytes("test".getBytes());
        binaryWriter.flush();
        assertNotNull(ms);
    }

}