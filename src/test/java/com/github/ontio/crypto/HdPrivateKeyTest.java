/*
 * Copyright (C) 2018-2019 The ontology Authors
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

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.bip32.HdPrivateKey;
import com.github.ontio.crypto.bip32.HdPublicKey;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class HdPrivateKeyTest {
    @Test
    public void TestMasterKeyFromMnemonic() throws Exception {
        String code = "ritual month sign shop champion number mask leave anchor critic boring clinic";
        HdPrivateKey masterKey = HdPrivateKey.masterKeyFromMnemonic(code);
        HdPublicKey masterPubKey = masterKey.getHdPublicKey();
        Assert.assertEquals("xpub661MyMwAqRbcFipovJxHFBNJ9YZDn4NdPVcf6bFnwqo5RwgGAf1yEcGVRVwNmhBRmcvzforP5aWQefsNveyCxPdJ4L6oydpm9XLZ7PLCBXd", masterPubKey.base58Encode());
        Assert.assertEquals("942a2d2546105da4ab795f89847b6bc1bff3b90180dd811a906bb73a284f1c2e", masterKey.toHexString());
        Account masterAcct = new Account(masterKey.getPrivateKey(), SignatureScheme.SHA256WITHECDSA);
        Assert.assertEquals(Helper.toHexString(masterAcct.serializePrivateKey()), masterKey.toHexString());
        Assert.assertEquals(Helper.toHexString(masterAcct.serializePublicKey()), masterPubKey.toHexString());
        Assert.assertEquals("ANQ6eYZ3Age8iXxTeaAzQR1GLbshaESWgL", masterAcct.getAddressU160().toBase58());
        acctValidity(masterAcct);
    }

    @Test
    public void TestRootKey() throws Exception {
        HdPrivateKey masterKey = HdPrivateKey.base58Decode("xprv9s21ZrQH143K3EkLpHRGt3RZbWijNben2Gh4JCrBPWG6Z9M7d7higox1aCzTm77JCa7FGoAsy8jgtMMyqqDk25DXssjbEBzqR6yr9gqNimh");
        HdPrivateKey rootKey = masterKey.fromPath();
        HdPrivateKey rootPriKey = masterKey.fromPath();
        Assert.assertEquals("66ed0c7f0476d64752ec1d14beaf0693af6641ccce6401ba6f30c41048f5f9de", rootPriKey.toHexString());
        Account rootAcct = new Account(rootPriKey.getPrivateKey(), SignatureScheme.SHA256WITHECDSA);
        Assert.assertEquals("AZN8hReHwhsdaowbBLcDsGHeAnjoTXAyRs", rootAcct.getAddressU160().toBase58());
        Assert.assertEquals(rootPriKey.base58Encode(), HdPrivateKey.base58Decode(rootPriKey.base58Encode()).base58Encode());
    }

    @Test
    public void TestBase58() throws Exception {
        ArrayList<String> addressList = new ArrayList<>(
                Arrays.asList(
                        "AX9gZt82urcv5fHu13QTiU3ZczYDJPBnb1",
                        "ALhLmVQ4xV3ZSHzcB9p2zwkqqgHuhjQ4nM",
                        "AGoDL81KzTEAaPxBzWDRwmgyoZNRPJEiSz",
                        "APSnKt2w2YAruZHmnq3TpYqTEihvrFjAAK",
                        "AP4nB1yct8D6si1C6T3bdevHqJiKpxrwsu",
                        "AG3Qq59LUCxNDcyrpSQFBtfnje7oRTPnMV",
                        "AX5mTNWyeXWpTb6L3y53VpgyuspRYXzgAk",
                        "AMLZhVzfFecX6tw2r26p8kxwFjx6gUL7X7",
                        "AeUjHPsbmwCwNg9QK3gFH2nwypgeqDqfox",
                        "APWuVP5JqsYc8aHKGAwXg9cufmHtxz62EW"
                )
        );
        HdPrivateKey masterKey = HdPrivateKey.base58Decode("xprv9s21ZrQH143K3EkLpHRGt3RZbWijNben2Gh4JCrBPWG6Z9M7d7higox1aCzTm77JCa7FGoAsy8jgtMMyqqDk25DXssjbEBzqR6yr9gqNimh");
        HdPrivateKey rootKey = masterKey.fromPath();
        Assert.assertEquals("xprv9y1wY3ovCV9wWRTw8VJwkyjuaV9vbmLb8kLuaAXyzBGQReZETHBHEab9BUdE9m5iCnfHyxABpomdqa6m4RCGzaC3iBdTQ1MPHxcF3RMXFD4", rootKey.base58Encode());
        HdPrivateKey actualRootKey = HdPrivateKey.base58Decode(rootKey.base58Encode());
        Assert.assertEquals("66ed0c7f0476d64752ec1d14beaf0693af6641ccce6401ba6f30c41048f5f9de", actualRootKey.toHexString());
        for (int i = 0; i < 10; i++) {
            HdPrivateKey childKey = rootKey.fromPath(String.format("0/%d", i));
            Account childAcct = new Account(childKey.getPrivateKey(), SignatureScheme.SHA256WITHECDSA);
            Assert.assertEquals(addressList.get(i), childAcct.getAddressU160().toBase58());
            Assert.assertEquals(Helper.toHexString(childAcct.serializePrivateKey()), childKey.toHexString());
            acctValidity(childAcct);
        }
    }

    @Test
    public void TestChildKey() throws SDKException {
        HdPrivateKey rootPriKey = HdPrivateKey.base58Decode("xprv9y1wY3ovCV9wWRTw8VJwkyjuaV9vbmLb8kLuaAXyzBGQReZETHBHEab9BUdE9m5iCnfHyxABpomdqa6m4RCGzaC3iBdTQ1MPHxcF3RMXFD4");
        ArrayList<String> childKeyList = new ArrayList<>(
                Arrays.asList(
                        "130d422b03a9b8f2246e850f2680cb61666b7d7da94a8ae1e754d12f10f9d7fa",
                        "8b7bb7a40ef05729fba70eec6b397f69c08d68a511428917f4f8828c5975ff50",
                        "59afb74c850af049c46359a55cd11edeefe32bd17956d36f0f7b5302c758d23e",
                        "d38b59946b0a6c359ac860b9d2632edf8ea297b11679a4ab8e06d515cc18c7bc",
                        "d1dac074d6361b3a0f9454affd67788e2b5cd8cc1564ffd653b36133359d13a2",
                        "2c700989f69f557c3dab063c62dae5a3c1b295b3044279ff645963e2c3a60877",
                        "2b4b47e351cb8eb717bba086b6109c312c6e196d93f96f898ae888c001baab97",
                        "b208e4c0cb6dbf5b17acbf98526d42a1e2c9e0f653e577247dd6722c932fc6f3",
                        "584540f8e3a5b37d04856c0e48267f7d42be0d3aa5610dbc7433901dc7bfe30a",
                        "feac85feda73a5cd07ece0eb5990b953ded921952a67642f0d57fca762fa8175"
                )
        );
        for (int i = 0; i < 10; i++) {
            HdPrivateKey childKey = rootPriKey.fromPath(String.format("0/%d", i));
            Assert.assertEquals(childKeyList.get(i), childKey.toHexString());
            Assert.assertEquals(childKey.base58Encode(), HdPrivateKey.base58Decode(childKey.base58Encode()).base58Encode());
        }
    }

    private void acctValidity(Account acct) throws Exception {
        byte[] msg = "Attack!".getBytes(StandardCharsets.UTF_8);
        byte[] signature = acct.generateSignature(msg, acct.getSignatureScheme(), null);
        Assert.assertTrue(acct.verifySignature(msg, signature));
    }

}
