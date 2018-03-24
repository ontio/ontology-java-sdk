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
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.common.Address;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class InvokeCodeTransaction extends Transaction {
	public long gasLimit;
	public byte vmType;
	public byte[] code;
	public ECPoint invoker;

	public InvokeCodeTransaction() {
		super(TransactionType.InvokeCodeTransaction);
	}
	public InvokeCodeTransaction(ECPoint invoker) {
		super(TransactionType.InvokeCodeTransaction);
		this.invoker = invoker;
	}
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			gasLimit = reader.readLong();
			vmType = reader.readByte();
			code = reader.readVarBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeLong(gasLimit);
		writer.writeByte(vmType);
		writer.writeVarBytes(code);
	}
	@Override
	public Address[] getAddressU160ForVerifying() {
		HashSet<Address> hashes = new HashSet<Address>(Arrays.asList(super.getAddressU160ForVerifying()));
		if(invoker != null) {
			hashes.add(Address.addressFromPubKey(invoker));
		}
		return hashes.stream().sorted().toArray(Address[]::new);
	}
	@Override
	public Object json() {
		Map obj = (Map)super.json();
		Map payload = new HashMap();
		payload.put("Code", Helper.toHexString(code));
		payload.put("GasLimit",gasLimit);
		payload.put("VmType",vmType& Byte.MAX_VALUE);
		obj.put("Payload",payload);
		return obj;
	}
}
