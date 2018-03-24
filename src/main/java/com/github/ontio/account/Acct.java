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

package com.github.ontio.account;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.sm.SM2Utils;
import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.exception.SDKException;
import org.bouncycastle.math.ec.ECPoint;

import com.github.ontio.crypto.ECC;
import org.bouncycastle.crypto.generators.SCrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * account info, including privatekey/publicKey and publicHash
 */
public class Acct {
	
	/**
	 * privateKey, used for signing transaction
	 */
    public final byte[] privateKey ;
    
    /**
     * publickey, used for verifying signature info
     */
    public final ECPoint publicKey ;
    
    /**
     * publiekeyHash, used for identifing which account the contract belongs to
     */
    public final Address scriptHash ;

    public Acct(byte[] privateKey, String Algrithem) {
        if (privateKey.length != 32 && privateKey.length != 96 && privateKey.length != 104) {
        	throw new IllegalArgumentException();
        }
        this.privateKey = new byte[32];
        System.arraycopy(privateKey, privateKey.length - 32, this.privateKey, 0, 32);
        if(Algrithem.equals(KeyType.SM2.name())) {
            this.publicKey = SM2Utils.generatePubkey(this.privateKey);
            this.scriptHash = Address.toScriptHash(publicKey.getEncoded(true));
        }else {
            if (privateKey.length == 32) {
                this.publicKey = ECC.secp256r1.getG().multiply(new BigInteger(1, privateKey)).normalize();
            } else {
                byte[] encoded = new byte[65];
                encoded[0] = 0x04;
                System.arraycopy(privateKey, 0, encoded, 1, 64);
                this.publicKey = ECC.secp256r1.getCurve().decodePoint(encoded);
            }
            this.scriptHash = Address.toScriptHash(publicKey.getEncoded(true));
        }

    }
    
    public String export() {
        byte[] data = new byte[38];
        data[0] = (byte) 0x80;
        System.arraycopy(privateKey, 0, data, 1, 32);
        data[33] = (byte) 0x01;
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        System.arraycopy(checksum, 0, data, data.length - 4, 4);
        String wif = Base58.encode(data);
        Arrays.fill(data, (byte) 0);
        return wif;
    }

    public String export(String passphrase) {
        int N = 16384;
        int r = 8;
        int p = 8;
        Address script_hash = Address.addressFromPubKey(publicKey);
        String address = script_hash.toBase58();

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes())) ;
        byte[] addresshash =  Arrays.copyOfRange(addresshashTmp, 0, 4);

        byte[] derivedkey = SCrypt.generate(passphrase.getBytes(StandardCharsets.UTF_8), addresshash, N, r, p, 64);
        byte[] derivedhalf1 = new byte[32];
        byte[] derivedhalf2 = new byte[32];
        System.arraycopy(derivedkey, 0, derivedhalf1, 0, 32);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] derived = XOR(privateKey, derivedhalf1);
            byte[] encryptedkey = cipher.doFinal( derived);

            byte[] buffer = new byte[39];
            buffer[0] = (byte) 0x01;
            buffer[1] = (byte) 0x42;
            buffer[2] = (byte) 0xe0;
            System.arraycopy(addresshash, 0, buffer, 3, addresshash.length);
            System.arraycopy(encryptedkey, 0, buffer, 7, encryptedkey.length);
            return Base58.checkSumEncode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 私钥转换
    public static byte[] getPrivateKeyFromWIF(String wif) {
        if (wif == null) {
            throw new NullPointerException();
        }
        byte[] data = Base58.decode(wif);
        if (data.length != 38 || data[0] != (byte)0x80 || data[33] != 0x01) {
            throw new IllegalArgumentException();
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        for (int i = 0; i < 4; i++) {
            if (data[data.length - 4 + i] != checksum[i]) {
                throw new IllegalArgumentException();
            }
        }
        byte[] privateKey = new byte[32];
        System.arraycopy(data, 1, privateKey, 0, privateKey.length);
        Arrays.fill(data, (byte) 0);
        return privateKey;
    }
    // 私钥转换
    public static String getPrivateKey(String encrypted,String passphrase) throws Exception{
        if (encrypted == null) {
            throw new NullPointerException();
        }
        byte[] decoded = Base58.decodeChecked(encrypted);
        if (decoded.length != 43 || decoded[0] != (byte)0x01 || decoded[1] != (byte)0x42 || decoded[2] != (byte)0xe0){
            throw new SDKException(Error.getDescArgError("decoded 3 bytes error"));
        }
        byte[] data = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
        return decode(passphrase,data);
    }
    private static String decode(String passphrase,byte[] input) throws  Exception{
        int N = 16384;
        int r = 8;
        int p = 8;
        byte[] addresshash = new byte[4];
        byte[] encryptedkey = new byte[32];
        System.arraycopy(input, 3, addresshash, 0, 4);
        System.arraycopy(input, 7, encryptedkey, 0, 32);

        byte[] derivedkey = SCrypt.generate(passphrase.getBytes(StandardCharsets.UTF_8), addresshash, N, r, p, 64);
        byte[] derivedhalf1 = new byte[32];
        byte[] derivedhalf2 = new byte[32];
        System.arraycopy(derivedkey, 0, derivedhalf1, 0, 32);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] rawkey = cipher.doFinal( encryptedkey);

        String priKey = Helper.toHexString(XOR(rawkey, derivedhalf1));
        Acct account = new Acct(Helper.hexToBytes(priKey),"" );
        Address script_hash = Address.addressFromPubKey(account.publicKey);
        String address = script_hash.toBase58();
        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes())) ;
        byte[] addresshashNew =  Arrays.copyOfRange(addresshashTmp, 0, 4);

        if(!new String(addresshash).equals(new String(addresshashNew))){
            throw new SDKException(Error.getDescArgError("decode prikey passphrase error. " + Helper.toHexString(addresshash) + "," + Helper.toHexString(addresshashNew)));
        }
        return priKey;
    }
    private static byte[] XOR(byte[] x, byte[] y) throws Exception
    {
        if (x.length != y.length) {
            throw new Exception();
        }
        byte[] ret = new byte[x.length];
        for (int i=0; i < x.length; i++) {
            ret[i] = (byte)(x[i] ^ y[i]);
        }
        return ret;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Acct)) {
        	return false;
        }
        return scriptHash.equals(((Acct) obj).scriptHash);
    }

    @Override
    public int hashCode(){
        return scriptHash.hashCode();
    }
}
