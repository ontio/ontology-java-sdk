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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.github.ontio.account.Account;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import com.github.ontio.common.Address;


public interface Signable extends Serializable {

    void deserializeUnsigned(BinaryReader reader) throws IOException;

    void serializeUnsigned(BinaryWriter writer) throws IOException;

    Address[] getAddressU160ForVerifying();
    
    default byte[] getHashData() {
    	try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
	    	try (BinaryWriter writer = new BinaryWriter(ms)) {
	            serializeUnsigned(writer);
	            writer.flush();
	            return ms.toByteArray();
	        }
    	} catch (IOException ex) {
    		throw new UnsupportedOperationException(ex);
    	}
    }
	default byte[] sign(Account account, SignatureScheme scheme) throws Exception {
		return account.generateSignature(Digest.sha256(Digest.sha256(getHashData())), scheme,null);
	}
	default boolean verifySignature(Account account, byte[] data, byte[] signature) throws Exception {
		return account.verifySignature(data, signature);
	}

}
