package com.github.neo.core.transaction;

import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.TransactionType;

public class TransferTransaction extends TransactionNeo {
	
	public TransferTransaction() {
		super(TransactionType.TransferTransaction);
	}
	@Override
	public Address[] getAddressU160ForVerifying() {
		return null;
	}
}
