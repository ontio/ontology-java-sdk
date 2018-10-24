package com.github.ontio.core.governance;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.utils;

import java.io.IOException;

public class GlobalParam2 implements Serializable {
    public int minAuthorizePos;
    public int candidateFeeSplitNum;
    public byte[] field1;
    public byte[] field2;
    public byte[] field3;
    public byte[] field4;
    public byte[] field5;
    public byte[] field6;
    public GlobalParam2(){

    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.minAuthorizePos = (int)utils.readVarInt(reader);
        this.candidateFeeSplitNum = (int)utils.readVarInt(reader);
        this.field1 = reader.readVarBytes();
        this.field2 = reader.readVarBytes();
        this.field3 = reader.readVarBytes();
        this.field4 = reader.readVarBytes();
        this.field5 = reader.readVarBytes();
        this.field6 = reader.readVarBytes();

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
