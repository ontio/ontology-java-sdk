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
