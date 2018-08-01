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

package com.github.ontio.crypto;

import java.security.*;

import com.github.ontio.common.Helper;
import org.bouncycastle.asn1.x9.*;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;

public class ECC {
	private static final X9ECParameters secp256r1nc = ECNamedCurveTable.getByName("secp256r1");
	public static final ECDomainParameters secp256r1 = new ECDomainParameters(secp256r1nc.getCurve(), secp256r1nc.getG(), secp256r1nc.getN(), secp256r1nc.getH(), secp256r1nc.getSeed());
	private static final X9ECParameters sm2p256v1nc = ECNamedCurveTable.getByName("sm2p256v1");
	public static final ECDomainParameters sm2p256v1 = new ECDomainParameters(sm2p256v1nc.getCurve(), sm2p256v1nc.getG(), sm2p256v1nc.getN(), sm2p256v1nc.getH(), sm2p256v1nc.getSeed());

	public static int compare(ECPoint a, ECPoint b) {
		if (a == b) {
			return 0;
		}
		int result = a.getXCoord().toBigInteger().compareTo(b.getXCoord().toBigInteger());
		if (result != 0) {
			return result;
		}
		return a.getYCoord().toBigInteger().compareTo(b.getYCoord().toBigInteger());
	}
	
	
	public static String toString(ECPoint p) {
		return Helper.toHexString(p.getEncoded(true));
	}

	
	public static byte[] generateKey(int len) {
		byte[] key = new byte[len];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(key);
		return key;
	}
	
	public static byte[] generateKey() {
		return generateKey(32);
	}
}
