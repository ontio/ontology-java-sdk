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

package com.github.ontio.core.payload;

import com.github.ontio.common.Helper;
import com.github.ontio.core.TransactionType;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.core.Transaction;
import com.github.ontio.crypto.ECC;
import com.github.ontio.io.BinaryReader;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by zx on 2018/2/1.
 */
public class Enrollment extends Transaction {
        public ECPoint pubKey;
        public Enrollment() {
            super(TransactionType.Enrollment);
        }
        @Override
        protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
            try {
                pubKey = ECC.secp256r1.getCurve().createPoint(
                        new BigInteger(1, reader.readVarBytes()), new BigInteger(1, reader.readVarBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
            writer.writeVarBytes(Helper.removePrevZero(pubKey.getXCoord().toBigInteger().toByteArray()));
            writer.writeVarBytes(Helper.removePrevZero(pubKey.getYCoord().toBigInteger().toByteArray()));
        }
}
