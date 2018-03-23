package com.github.ontio.core.payload;

import java.io.IOException;

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
}
