package com.github.ontio.core.governance;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;

public class GlobalParam implements Serializable {
    public int candidateFeeSplitNum;
    public int A;
    public int B;
    public int yita;
    public GlobalParam(){}
    public GlobalParam(int CandidateFeeSplitNum, int A, int B, int Yita){
        this.candidateFeeSplitNum = CandidateFeeSplitNum;
        this.A = A;
        this.B = B;
        this.yita = Yita;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.candidateFeeSplitNum = (int)reader.readVarInt();
        this.A = (int)reader.readVarInt();
        this.B = (int)reader.readVarInt();
        this.yita = (int)reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
