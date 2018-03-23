package com.github.ontio.core.payload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.core.TransactionType;
import com.github.ontio.core.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

public class BookKeeping extends Transaction {
	private long nonce; // nonce is not exist when version=2

	public BookKeeping() {
		super(TransactionType.BookKeeping);
	}

	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		nonce = reader.readLong();
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		if(version == 3) {
			writer.writeLong(nonce);
		}
	}
	@Override
	public Object json() {
		Map obj = (Map)super.json();
		Map payload = new HashMap();
		payload.put("Nonce", nonce);
		obj.put("Payload",payload);
		return obj;
	}
}
