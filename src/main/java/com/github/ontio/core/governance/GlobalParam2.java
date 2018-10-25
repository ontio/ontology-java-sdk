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
    public GlobalParam2(int minAuthorizePos, int candidateFeeSplitNum, byte[] field1, byte[] field2, byte[] field3, byte[] field4,
                        byte[] field5, byte[] field6){
        this.minAuthorizePos = minAuthorizePos;
        this.candidateFeeSplitNum = candidateFeeSplitNum;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;

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
