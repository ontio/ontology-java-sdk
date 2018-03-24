/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.core;

import com.github.ontio.account.Acct;
import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.IOException;
import java.util.HashSet;


public class DataSignature implements Signable {
    private Acct acct;
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
