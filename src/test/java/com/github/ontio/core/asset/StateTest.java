package com.github.ontio.core.asset;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class StateTest {
    @Test
    public void deserialize() throws SDKException, IOException {
        State state = new State(Address.decodeBase58("AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve"),Address.decodeBase58("AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve"),1000L);
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(bais);
        Transfers transfers = new Transfers(new State[]{state});
        System.out.println(Helper.toHexString(transfers.toArray()));


        state.serialize(bw);

        State state1 = new State();
        ByteArrayInputStream baos = new ByteArrayInputStream(bais.toByteArray());
        BinaryReader br = new BinaryReader(baos);
        state1.deserialize(br);
    }
}