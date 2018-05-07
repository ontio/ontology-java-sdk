package com.github.ontio.io;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class BinaryWriterTest {

    @Test
    public void writeVarInt() throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(ms);
        binaryWriter.writeVarInt((long)254);
        System.out.println(binaryWriter);
    }
}