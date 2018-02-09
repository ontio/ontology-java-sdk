package ontology.core;

import ontology.core.scripts.Program;
import ontology.crypto.ECC;
import ontology.common.Helper;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.common.UInt160;
import ontology.core.contract.Contract;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by zx on 2017/8/23.
 */
public class IdentityUpdateTransaction extends Transaction {
    public byte[] DID;
    public byte[] DDO;
    public ECPoint Updater;

    public IdentityUpdateTransaction() {
        super(TransactionType.IdentityUpdateTransaction);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        DID = reader.readVarBytes();
        DDO = reader.readVarBytes();
        Updater = ECC.secp256r1.getCurve().createPoint(
                new BigInteger(1,reader.readVarBytes()), new BigInteger(1,reader.readVarBytes()));
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(DID);
        writer.writeVarBytes(DDO);
        writer.writeVarBytes(Helper.removePrevZero(Updater.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(Updater.getYCoord().toBigInteger().toByteArray()));
    }

    @Override
    public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(Updater)));
        return hashes.stream().sorted().toArray(UInt160[]::new);
    }
}
