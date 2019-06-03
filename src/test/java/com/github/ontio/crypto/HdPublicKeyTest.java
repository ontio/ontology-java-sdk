package com.github.ontio.crypto;

import com.github.ontio.crypto.bip32.HdPublicKey;
import org.junit.Test;

public class HdPublicKeyTest {
    @Test
    public void TestBase58() {
        HdPublicKey rootPubKey = HdPublicKey.base58Decode("");
    }
}
