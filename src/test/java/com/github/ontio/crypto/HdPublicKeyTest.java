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

import com.github.ontio.crypto.bip32.HdPrivateKey;
import com.github.ontio.crypto.bip32.HdPublicKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class HdPublicKeyTest {
    @Test
    public void TestBase58() throws Exception {
        ArrayList<String> childKeysFromMaterKey = new ArrayList<>(
                Arrays.asList(
                        "xpub6B8Gwg8EyCxhfunuHCPJzQSLyU2MEv4HzSBthbzWtkLMm9PK8GZuVT1qDSJ8jkKiwXdYzgUWgXV1fvbvvqBU1wKrJMXtrkJ4MghnsKbBR5D",
                        "xpub6B8Gwg8EyCxhixXNFY2khGpFADQF7Sq4Gf3e5w6buzWxjXhrqN2nbjCBmnHuTTf692RQTUvWsXTEgtaJMUqnEgDz9WcJuBiAh2eubcEVotS",
                        "xpub6B8Gwg8EyCxho65BfKpBtT2w4aVTrrtakypNbSn2RYYSWyuoEZDxCAB2bY9pVMPX1L1QqzANqSoMaSmnLVW9RRbSDy38SRsSc5ZdX9x7VTx",
                        "xpub6B8Gwg8EyCxhqzA6x6YhqEA27tu2h5Wk5hTHknbEQGCnw2immnB7Mj2SY1G2Z3sSutYncgrbUb2nsguLvZfvURo8L3zhGKECciKVDgjPgju",
                        "xpub6B8Gwg8EyCxhsdKE1ZeizXX9dKKhRjDj4hQ1Rihu5y3z244whdm2p1YrBtBsYKJUzX5Ks5RrngQfFY5VK6P3cQWMoB96u5ytCknmrzozrAN",
                        "xpub6B8Gwg8EyCxhvcNvuG5y6ExgHTkV3gr9Y8NKczn6zcT5MrBWLeTMFb6YhPL2SHWvyKD3zLSSvcfycuJzpL5kL4HNH3bT4U5Fkd1WZndXcov",
                        "xpub6B8Gwg8EyCxhydcj9rvsRyrx9Ns1uoYedTJn8GScff2T2vzfefetgjS1dv4K3xrffSkguDwK73YtFdhxgwsMbn8gqtaqJUR8tQ64yBckngK",
                        "xpub6B8Gwg8EyCxhzEzosFjaCaK4T7Baxasg2mfrKovbnx2TqBJGfaaZjKiozCX2zzYBc79kgAv8UmYtnoo1dsjXGg3kME9aKyMLTeESdoJNup5",
                        "xpub6B8Gwg8EyCxi2PpauJ4mhjxRsf4n7HKjUY65HnXfrCJTtYJH25LjrthVdrQZXoik9cXTwdm9qLtoWLcHXB3KkMkZUJPXbWenoFkzxLZ9tuU",
                        "xpub6B8Gwg8EyCxi74DcH9AdErRcSDfiYnH3CCUS2Kb2CsrhtYATVugBiVYGBungGJphArg3peaDUh6hwJnxCUSbsqcqhZkKK35bMkK4WLtario"
                )
        );
        HdPrivateKey masterKey = HdPrivateKey.base58Decode("xprv9s21ZrQH143K3EkLpHRGt3RZbWijNben2Gh4JCrBPWG6Z9M7d7higox1aCzTm77JCa7FGoAsy8jgtMMyqqDk25DXssjbEBzqR6yr9gqNimh");
        HdPublicKey masterPubKey = masterKey.getHdPublicKey();
        Assert.assertEquals("03908a0ce4ccc0e706e09905db4f04eb150bc42fb6cd96a7a2c8396873fa973262", masterPubKey.toHexString());
        String encodeMasterPubKey = masterPubKey.base58Encode();
        Assert.assertEquals("xpub661MyMwAqRbcFipovJxHFBNJ9YZDn4NdPVcf6bFnwqo5RwgGAf1yEcGVRVwNmhBRmcvzforP5aWQefsNveyCxPdJ4L6oydpm9XLZ7PLCBXd", encodeMasterPubKey);
        HdPublicKey actualMasterPubKey = HdPublicKey.base58Decode(encodeMasterPubKey);
        Assert.assertEquals(masterPubKey.toHexString(), actualMasterPubKey.toHexString());
        for (int i = 0; i < 10; i++) {
            HdPublicKey childKey = masterPubKey.fromPath(String.format("0/%d", i));
            Assert.assertEquals(childKeysFromMaterKey.get(i), childKey.base58Encode());
        }
    }

    @Test
    public void TestChildKey() throws Exception {
        ArrayList<String> childPubKeys = new ArrayList<>(
                Arrays.asList(
                        "03d36b121866ddb5d18cd73b4f08a0b33d4e78765353122af304c274afa46234f1",
                        "0294423e98e78befeec802f3bb710d23e07cde2eac693f04b0457f0088987a6c3d",
                        "024573478ec397c11187a80654eb61632195e10b790abcd3c7a4ebf9bb656d08a2",
                        "03a4d828a8d10d6af8ae575b8edd751053179b9c20966c5ae3140b524269114b94",
                        "02547df85c98586055f7f991ee2eb2ae12c6dcfc8f3dc47d07b6416f3118206aa2",
                        "03b4713e88d3015e91b827c87d8de204f6807e57e4ca4a048c4cff81643b816fd1",
                        "0366faf9e5c4984bed43afa2cebad2ea957bbce308a3d100e5bd7ee0a7b8a49a89",
                        "0374eb62dab8294566d804bd7d1e3475bc1c66e08824f1b77dd18b539ba192a6aa",
                        "0349d87e57ca1bd3d720625ad973d24cba51c94ca33352b462e851ebbb5f12a47d",
                        "02d34bdd023d4cfe9461a60222cbb6ef34f09a1ec4208d15e30800aa4c20c8be28"
                )
        );
        ArrayList<String> childPrvKeys = new ArrayList<>(
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

        HdPrivateKey rootPriKey = HdPrivateKey.base58Decode("xprv9y1wY3ovCV9wWRTw8VJwkyjuaV9vbmLb8kLuaAXyzBGQReZETHBHEab9BUdE9m5iCnfHyxABpomdqa6m4RCGzaC3iBdTQ1MPHxcF3RMXFD4");
        HdPublicKey rootPubKey = rootPriKey.getHdPublicKey();
        for (int i = 0; i < 10; i++) {
            HdPublicKey childPubKey = rootPubKey.fromPath(String.format("0/%d", i));
            HdPrivateKey childPrvKey = rootPriKey.fromPath(String.format("0/%d", i));
            Assert.assertEquals(childPrvKeys.get(i), childPrvKey.toHexString());
            Assert.assertEquals(childPubKeys.get(i), childPubKey.toHexString());
            Assert.assertEquals(childPubKey.base58Encode(), HdPublicKey.base58Decode(childPubKey.base58Encode()).base58Encode());
        }
    }
}
