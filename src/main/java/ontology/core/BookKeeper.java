package ontology.core;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;

import ontology.common.Helper;
import ontology.crypto.ECC;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;

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
