package com.github.ontio.core.program;

public class ProgramInfo {
    public byte[][] publicKey;
    public short m;
    public ProgramInfo(){}
    public ProgramInfo(byte[][] publicKey,short m){
        this.publicKey = publicKey;
        this.m = m;
    }

    public byte[][] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[][] publicKey) {
        this.publicKey = publicKey;
    }

    public void setM(short m) {
        this.m = m;
    }
    public short getM() {
        return m;
    }
}
