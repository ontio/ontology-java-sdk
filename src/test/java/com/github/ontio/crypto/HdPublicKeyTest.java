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
        HdPrivateKey rootKey = masterKey.fromPath();
        System.out.println(rootKey.base58Encode());
    }
}
