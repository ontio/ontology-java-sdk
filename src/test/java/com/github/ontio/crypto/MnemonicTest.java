package com.github.ontio.crypto;

import io.github.novacrypto.bip39.Words;
import org.junit.Assert;
import org.junit.Test;

public class MnemonicTest {
    @Test
    public void TestMnemonic() {
        Assert.assertEquals(12, MnemonicCode.generateMnemonicCodesStr().split(" ").length);
        Assert.assertEquals(12, MnemonicCode.generateMnemonicCodesStr(Words.TWELVE).split(" ").length);
        Assert.assertEquals(15, MnemonicCode.generateMnemonicCodesStr(Words.FIFTEEN).split(" ").length);
        Assert.assertEquals(18, MnemonicCode.generateMnemonicCodesStr(Words.EIGHTEEN).split(" ").length);
        Assert.assertEquals(21, MnemonicCode.generateMnemonicCodesStr(Words.TWENTY_ONE).split(" ").length);
        Assert.assertEquals(24, MnemonicCode.generateMnemonicCodesStr(Words.TWENTY_FOUR).split(" ").length);
    }
}
