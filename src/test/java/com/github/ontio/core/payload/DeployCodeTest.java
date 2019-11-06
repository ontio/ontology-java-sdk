package com.github.ontio.core.payload;

import com.github.ontio.core.VmType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DeployCodeTest {

    @Test
    public void serializeExclusiveData() throws IOException {
        DeployCode deployCode = new DeployCode();
        deployCode.version = "1";
        deployCode.author = "sss";
        deployCode.name = "sss";
        deployCode.code = "test".getBytes();
        deployCode.description = "test";
        deployCode.email = "test";
        deployCode.needStorage = true;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        deployCode.serializeExclusiveData(binaryWriter);

        byte[] selr = byteArrayOutputStream.toByteArray();

        DeployCode deployCode1 = new DeployCode();
        deployCode1.deserializeExclusiveData(new BinaryReader(new ByteArrayInputStream(selr)));
        assertEquals(deployCode.version,deployCode1.version);
    }
}