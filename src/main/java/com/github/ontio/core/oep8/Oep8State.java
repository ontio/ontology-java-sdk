package com.github.ontio.core.oep8;

public class Oep8State {
    public byte[] from;
    public byte[] to;
    public byte[] tokenId;
    public long value;
    public Oep8State(byte[] from, byte[] to, byte[] tokenId, long value) {
        this.from = from;
        this.to = to;
        this.tokenId = tokenId;
        this.value = value;
    }
}
