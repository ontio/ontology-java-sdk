package com.github.ontio.core.ontid;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.math.BigInteger;

public class Signer implements Serializable {
    public byte[] id;
    public int index;


    public Signer(byte[] id, int index) {
        this.index = index;
        this.id = id;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.id);
        BigInteger index = BigInteger.valueOf(this.index);
        writer.writeVarBytes(Helper.BigIntToNeoBytes(index));
    }
}
