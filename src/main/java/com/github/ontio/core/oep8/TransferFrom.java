package com.github.ontio.core.oep8;

public class TransferFrom {
    public byte[] spender;
    public byte[] from;
    public byte[] to;
    public byte[] tokenId;
    public long value;
    public TransferFrom(byte[] spender, byte[] from, byte[] to, byte[] tokenId, long value){
        this.spender = spender;
        this.from = from;
        this.to = to;
        this.tokenId = tokenId;
        this.value = value;
    }
}
