package com.github.ontio.core;

import com.github.ontio.account.Acct;
import com.github.ontio.common.Inventory;
import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.info.account.AccountInfo;
import com.github.ontio.common.InventoryType;

import java.io.IOException;
import java.util.HashSet;


public class DataSignature extends Inventory {
    private Acct acct;
    private AccountInfo acctInfo;
    private String data;
    private String algrithem;
    public DataSignature(){
    }
    public DataSignature(String data){
        this.data = data;
    }
    public DataSignature(String alg, Acct acct, String data){
        this.algrithem = alg;
        this.acct = acct;
        this.data = data;
    }
    public String getData(){
        return data;
    }
    public byte[] signature() {
        try {
            SignatureContext context = new SignatureContext(this);
            byte[] signData = context.signable.sign(acct,algrithem);
            return signData;
        } catch (Exception e) {
            throw new RuntimeException("Data signature error.");
        }
    }
    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public final InventoryType inventoryType() {
        return InventoryType.TX;
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        HashSet<Address> hashes = new HashSet<Address>();
        hashes.add(Address.addressFromPubKey(acct.publicKey));
        return hashes.stream().sorted().toArray(Address[]::new);
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    }

    @Override
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
    }

    @Override
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.write(data.getBytes());
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    }
}
