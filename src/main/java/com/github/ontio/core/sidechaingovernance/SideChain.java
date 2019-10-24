package com.github.ontio.core.sidechaingovernance;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SideChain implements Serializable {
    public long sideChainId;
    public Address address;
    public long ratio;
    public long deposit;
    public long ongNum;
    public long ongPool;
    public byte status;
    public byte[] genesisBlock;

    public SideChain(){}
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.sideChainId = reader.readLong();
        try {
            this.address = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.ratio = reader.readLong();
        this.deposit = reader.readLong();
        this.ongNum = reader.readLong();
        this.ongPool = reader.readLong();
        this.status = reader.readByte();
        this.genesisBlock = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
    public String toJson(){
        Map map = new HashMap<>();
        map.put("sideChainId",this.sideChainId);
        map.put("address", this.address.toBase58());
        map.put("ratio", this.ratio);
        map.put("deposit", this.deposit);
        map.put("ongNum", this.ongNum);
        map.put("ongPool", this.ongPool);
        map.put("status", this.status);
        map.put("genesisBlock", Helper.toHexString(genesisBlock));
        return JSON.toJSONString(map);
    }
}
