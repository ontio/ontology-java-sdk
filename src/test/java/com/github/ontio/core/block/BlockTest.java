package com.github.ontio.core.block;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void deserialize() {
    }


    @Test
    public void serialize() throws IOException, SDKException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        Block block = new Block();
        block.version = 1;
        block.height = 1;
        block.consensusPayload = "test".getBytes();
        block.prevBlockHash = new UInt256(Helper.hexToBytes("1d46ec977e10d297a53d77dfcb5fe5904734f2c62e156d0e893d1b7c050524a2"));
        block.blockRoot = new UInt256(Helper.hexToBytes("37614956f598e0b3c4c3105d9e94c8cf4aa0ac6adce4eff4189dd348d1f3dac2"));
        block.consensusData = 111;
        block.nextBookkeeper = Address.decodeBase58("AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve");
        block.bookkeepers = new byte[][]{"test".getBytes()};
        block.transactionsRoot = new UInt256(Helper.hexToBytes("b5b8cb62d5c1ccea510ce1c268259fab33069c343532c804743bd4c6029dbd35"));
        block.sigData = new String[]{"123ab2"};
        block.transactions = new Transaction[]{};
        block.serialize(binaryWriter);
        binaryWriter.flush();
        byte[] seril = byteArrayOutputStream.toByteArray();
        Block block1 = new Block();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(seril);
        block1.deserializeUnsigned(new BinaryReader(byteArrayInputStream));
        assertEquals(block,block1);
    }

}