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
import java.math.BigInteger;

import com.github.ontio.account.KeyType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.sm.SM2Utils;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.util.BigIntegers;

import com.github.ontio.common.Address;
import com.github.ontio.account.Acct;


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
    
    default byte[] sign(Acct account, String Algrithem) {
		if(Algrithem.equals(KeyType.SM2.name())){
    		byte[] rst = SM2Utils.generateSignature(account.privateKey,getHashData());
			return  rst;
		}
		ECDSASigner signer = new ECDSASigner();
    	signer.init(true, new ECPrivateKeyParameters(new BigInteger(1, account.privateKey), ECC.secp256r1));
		BigInteger[] bi = signer.generateSignature(Digest.sha256(Digest.sha256(Digest.sha256(getHashData()))));
    	byte[] signature = new byte[64];
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[0]), 0, signature, 0, 32);
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[1]), 0, signature, 32, 32);
    	return signature;
    }
    
    default boolean verifySignature(String Algrithem,ECPoint pubkey,byte[] data,byte[] signature) {
		boolean result = false;
		if(Algrithem.equals("SM2")) {
			result = SM2Utils.verifySignature(pubkey, data, signature);
		}else {
			ECDSASigner signer = new ECDSASigner();
			signer.init(false, new ECPublicKeyParameters(pubkey, ECC.secp256r1));
			byte[] r = new byte[32];
			byte[] s = new byte[32];
			System.arraycopy(signature, 0, r, 0, 32);
			System.arraycopy(signature, 32, s, 0, 32);
			result = signer.verifySignature(Digest.sha256(data), new BigInteger(1, r), new BigInteger(1, s));
		}
		return result;
    }
}
