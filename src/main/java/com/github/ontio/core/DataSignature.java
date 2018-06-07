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

import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;

import java.io.IOException;
import java.util.HashSet;


public class DataSignature implements Signable {
    private Account account;
    private byte[] data;
    private SignatureScheme scheme;

    public DataSignature() {
    }

    public DataSignature(byte[] data) {
        this.data = data;
    }

    public DataSignature(SignatureScheme scheme, Account acct, byte[] data) {
        this.scheme = scheme;
        this.account = acct;
        this.data = data;
    }
    public DataSignature(SignatureScheme scheme, Account acct, String data) {
        this.scheme = scheme;
        this.account = acct;
        this.data = data.getBytes();
    }

    public byte[] getData() {
        return data;
    }

    public byte[] signature() {
        try {
            byte[] signData = sign(account, scheme);
            return signData;
        } catch (Exception e) {
            throw new RuntimeException(ErrorCode.DataSignatureErr);
        }
    }

    @Override
    public byte[] sign(Account account, SignatureScheme scheme) throws Exception {
        return account.generateSignature(getHashData(), scheme, null);
    }

    @Override
    public boolean verifySignature(Account account, byte[] data, byte[] signature) throws Exception {
        return account.verifySignature(data, signature);
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    }

    @Override
    public void deserializeUnsigned(BinaryReader reader) throws IOException {
    }

    @Override
    public void serializeUnsigned(BinaryWriter writer) throws IOException {
        writer.write(data);
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    }
}
