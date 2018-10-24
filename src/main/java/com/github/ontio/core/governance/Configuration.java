package com.github.ontio.core.governance;

import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.utils;

import java.io.IOException;

public class Configuration implements Serializable {
    public int N;
    public int C;
    public int K;
    public int L;
    public int BlockMsgDelay;
    public int HashMsgDelay;
    public int PeerHandshakeTimeout;
    public int MaxBlockChangeView;
    public Configuration(){

    }
    public Configuration(int N,int C,int K,int L,int BlockMsgDelay, int HashMsgDelay,int PeerHandshakeTimeout, int MaxBlockChangeView){
        this.N = N;
        this.C = C;
        this.K = K;
        this.L = L;
        this.BlockMsgDelay = BlockMsgDelay;
        this.HashMsgDelay = HashMsgDelay;
        this.PeerHandshakeTimeout = PeerHandshakeTimeout;
        this.MaxBlockChangeView = MaxBlockChangeView;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

        this.N = (int)utils.readVarInt(reader);
        this.C = (int)utils.readVarInt(reader);
        this.K = (int)utils.readVarInt(reader);
        this.L = (int)utils.readVarInt(reader);
        this.BlockMsgDelay = (int)utils.readVarInt(reader);
        this.HashMsgDelay = (int)utils.readVarInt(reader);
        this.PeerHandshakeTimeout = (int)utils.readVarInt(reader);
        this.MaxBlockChangeView = (int)utils.readVarInt(reader);
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
