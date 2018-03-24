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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Digest {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static byte[] hash160(byte[] value) {
		return ripemd160(sha256(value));
	}
	
	public static byte[] hash256(byte[] value) {
		return sha256(sha256(value));
	}
	
	public static byte[] ripemd160(byte[] value) {
		try {
			MessageDigest md = MessageDigest.getInstance("RipeMD160");
			return md.digest(value);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] sha256(byte[] value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return md.digest(value);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] sha256(byte[] value, int offset, int length) {
		if (offset != 0 || length != value.length) {
			byte[] array = new byte[length];
			System.arraycopy(value, offset, array, 0, length);
			value = array;
		}
		return sha256(value);
	}
}
