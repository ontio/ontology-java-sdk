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

package com.github.ontio.core.transaction;

/**
 * list transaction types
 */
public enum TransactionType {
    Bookkeeping(0x00),
    Bookkeeper(0x02),
    Claim(0x03),
    Enrollment(0x04),
    Vote(0x05),
    DeployCode(0xd0),
    InvokeCode(0xd1),
    InvokeWasm(0xd2),
    TransferTransaction(0x80);

    private byte value;
    TransactionType(int v) {
        value = (byte)v;
    }
    public byte value() {
        return value;
    }

    public static TransactionType valueOf(byte v) {
    	for (TransactionType e : TransactionType.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
