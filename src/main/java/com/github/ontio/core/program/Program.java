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

package com.github.ontio.core.program;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.sdk.exception.SDKException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @Description:
 * @date 2018/6/10
 */
public class Program {
    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException {
        ScriptBuilder sb = new ScriptBuilder();
        sigData = Arrays.stream(sigData).sorted((o1, o2) -> {
            return Helper.toHexString(o1).compareTo(Helper.toHexString(o2));
        }).toArray(byte[][]::new);
        for (byte[] sig : sigData) {
            sb.push(sig);
        }
        return sb.toArray();
    }
    public static byte[] ProgramFromPubKey(byte[] publicKey) throws Exception {
        ScriptBuilder sb = new ScriptBuilder();
        sb.push(publicKey);
        sb.add(ScriptOp.OP_CHECKSIG);
        return sb.toArray();
    }
    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
        int n = publicKeys.length;

        if (m <= 0 || m > n || n > 24) {
            throw new SDKException(ErrorCode.ParamError);
        }
        try (ScriptBuilder sb = new ScriptBuilder()) {
            sb.push(BigInteger.valueOf(m));
            publicKeys = Arrays.stream(publicKeys).sorted((o1, o2) -> {
                return Helper.toHexString(o1).compareTo(Helper.toHexString(o2));
            }).toArray(byte[][]::new);

            for (byte[] publicKey : publicKeys) {
                sb.push(publicKey);
            }
            System.out.println(Helper.toHexString(sb.toArray()));
            sb.push(BigInteger.valueOf(publicKeys.length));
            sb.add(ScriptOp.OP_CHECKMULTISIG);
            return sb.toArray();
        }
    }
}
