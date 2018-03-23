package com.github.ontio.core.payload;

import java.io.IOException;
import java.math.BigInteger;

import com.github.ontio.common.Helper;
import com.github.ontio.core.BookKeeperAction;
import com.github.ontio.core.TransactionType;
import com.github.ontio.core.Transaction;
import org.bouncycastle.math.ec.ECPoint;

import com.github.ontio.crypto.ECC;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

public class BookKeeper extends Transaction {
	public ECPoint issuer;
	public BookKeeperAction action;
	public byte[] cert;
	
	public BookKeeper() {
		super(TransactionType.BookKeeper);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		issuer = ECC.secp256r1.getCurve().createPoint(
        		new BigInteger(1,reader.readVarBytes()), new BigInteger(1,reader.readVarBytes()));
		action = BookKeeperAction.valueOf(reader.readByte());
		cert = reader.readVarBytes();
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeVarBytes(Helper.removePrevZero(issuer.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(issuer.getYCoord().toBigInteger().toByteArray()));
        writer.writeByte(action.value());
        writer.writeVarBytes(cert);
	}
}
