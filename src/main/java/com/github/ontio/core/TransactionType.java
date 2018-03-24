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

package com.github.ontio.core;

/**
 * list transaction types supported by DNA 
 */
public enum TransactionType {
    /**
     *  used for accounting
     */
    BookKeeping(0x00),
    /**
     *  used for accounting
     */
    IssueTransaction(0x01),
    /**
     *  
     */
    BookKeeper(0x02),
    Claim(0x03),
    Enrollment(0x04),
    Vote(0x05),
    /**
     * 
     */
    DataFile(0x12),
    /**
     * 
     */
    DeployCodeTransaction(0xd0),
    InvokeCodeTransaction(0xd1),
    /**
     *  
     */
    PrivacyPayload(0x20),
    /**
     *  
     */
    RegisterTransaction(0x40),
    /**
     *  used for transfering Transaction, this is 
     */
    TransferTransaction(0x80), 
    /**
     * used for storing certificate
     */
    RecordTransaction(0x81),
    
    /**
     * 账本状态资产
     */
    StateUpdateTransaction(0x90),
    
    /**
     * 账本状态资产控制
     */
    IdentityUpdateTransaction(0x91),
    
    /**
     * 销毁资产
     */
    DestroyTransaction(0x18),
    
    ;

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
