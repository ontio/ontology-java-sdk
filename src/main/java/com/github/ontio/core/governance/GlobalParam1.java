package com.github.ontio.core.governance;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.utils;

import java.io.IOException;

public class GlobalParam1 implements Serializable {
    public long candidateFee;
    public int minInitStake;
    public int candidateNum;
    public int posLimit;
    public int A;
    public int B;
    public int yita;
    public int penalty;
    public GlobalParam1(){}
    public GlobalParam1(int CandidateFeeSplitNum, int A, int B, int Yita){
        this.candidateFee = CandidateFeeSplitNum;
        this.A = A;
        this.B = B;
        this.yita = Yita;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.candidateFee = utils.readVarInt(reader);
        this.minInitStake = (int)utils.readVarInt(reader);
        this.candidateNum = (int)utils.readVarInt(reader);
        this.posLimit = (int)utils.readVarInt(reader);
        this.A = (int)utils.readVarInt(reader);
        this.B = (int)utils.readVarInt(reader);
        this.yita = (int)utils.readVarInt(reader);
        this.penalty = (int)utils.readVarInt(reader);
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
