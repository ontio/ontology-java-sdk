package com.github.ontio.core.asset;

import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class ContractTest {

    Address address;

    @Before
    public void setUp() {
        address = Address.decodeBase58("TA6nRD9DqGkE8xRJaB37bW2KQEz59ovKRH");
    }

    @Test
    public void serialize() throws IOException {
        Contract contract = new Contract((byte)1,"test".getBytes(),address,"test","t".getBytes());
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(bs);
        contract.serialize(binaryWriter);
        binaryWriter.flush();
        byte[] seril = bs.toByteArray();
        assertNotNull(seril);

        Contract contract1 = new Contract((byte)1,"test2".getBytes(),address,"test2","t2".getBytes());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(seril);
        BinaryReader binaryReader = new BinaryReader(byteArrayInputStream);
        contract1.deserialize(binaryReader);
        assertNotNull(binaryReader);

    }
}