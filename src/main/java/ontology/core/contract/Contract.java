package ontology.core.contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Stream;

import ontology.common.Helper;
import ontology.crypto.Digest;
import org.bouncycastle.math.ec.ECPoint;

import ontology.common.UInt160;
import ontology.core.scripts.Program;
import ontology.core.scripts.ScriptBuilder;
import ontology.core.scripts.ScriptOp;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.Serializable;

/**
 *  所有合约的基类
 */
public class Contract implements Serializable {
    /**
     *  合约脚本代码
     */
    public byte[] addressU160;
    /**
     *  合约类型
     */
    public ContractParameterType[] parameterList;
    /**
     *  公钥散列值，用于标识该合约在钱包中隶属于哪一个账户
     */
    public UInt160 publicKeyHash;

    /**
     *  合约地址
     */
    private String _address;
    public String address() {
        if (_address == null) {
            _address = new UInt160(addressU160).toBase58();
        }
        return _address;
    }

    /**
     *  脚本散列值
     */
    private UInt160 _scriptHash;
    public UInt160 scriptHash() {
        if (_scriptHash == null) {
            _scriptHash = new UInt160(addressU160);
        }
        return _scriptHash;
    }
    
    public boolean isStandard() {
    	if (addressU160.length != 35) {
    		return false;
    	}
        if (addressU160[0] != 33 || addressU160[34] != ScriptOp.OP_CHECKSIG.getByte()) {
            return false;
        }
        return true;
    }
    
    public static Contract create(UInt160 publicKeyHash, ContractParameterType[] parameterList, byte[] redeemScript) {
    	Contract contract = new Contract();
    	contract.addressU160 = redeemScript;
    	contract.parameterList = parameterList;
    	contract.publicKeyHash = publicKeyHash;
    	return contract;
    }

    public static Contract createSignatureContract(ECPoint publicKey) {
        Contract contract = new Contract();
    	contract.addressU160 = addressFromPubKey(publicKey).toArray();//createSignatureRedeemScript(publicKey);
    	contract.parameterList = new ContractParameterType[] { ContractParameterType.Signature };
    	contract.publicKeyHash = Program.toScriptHash(publicKey.getEncoded(true));
    	return contract;
    }
    
//    public static byte[] createSignatureRedeemScript(ECPoint publicKey) {
//        try (ScriptBuilder sb = new ScriptBuilder()) {
//	        sb.push(publicKey.getEncoded(true));
//	        sb.add(ScriptOp.OP_CHECKSIG);
//	        return sb.toArray();
//        }
//    }

    public static UInt160 addressFromPubKey(ECPoint publicKey) {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                writer.writeVarBytes(Helper.removePrevZero(publicKey.getXCoord().toBigInteger().toByteArray()));
                writer.writeVarBytes(Helper.removePrevZero(publicKey.getYCoord().toBigInteger().toByteArray()));
                writer.flush();
                byte[] bys = Digest.hash160(ms.toByteArray());
                bys[0] = 0x01;
                UInt160 u160 = new UInt160(bys);
                return u160;
            }
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static UInt160 addressFromMultiPubKeys(int m, ECPoint... publicKeys) {
        if(m<=0 || m > publicKeys.length || publicKeys.length > 24){
            throw new IllegalArgumentException();
        }
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                writer.writeByte((byte)publicKeys.length);
                writer.writeByte((byte)m);
                ECPoint[] ecPoint = Arrays.stream(publicKeys).sorted((o1, o2) -> {
                    if (o1.getXCoord().toString().compareTo(o2.getXCoord().toString()) <= 0) {
                        return -1;
                    }
                    return 1;
                }).toArray(ECPoint[]::new);
                for(ECPoint publicKey:ecPoint) {
                    writer.writeVarBytes(Helper.removePrevZero(publicKey.getXCoord().toBigInteger().toByteArray()));
                    writer.writeVarBytes(Helper.removePrevZero(publicKey.getYCoord().toBigInteger().toByteArray()));
                }
                writer.flush();
                byte[] bys = Digest.hash160(ms.toByteArray());
                bys[0] = 0x02;
                UInt160 u160 = new UInt160(bys);
                return u160;
            }
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Contract)) {
        	return false;
        }
        return scriptHash().equals(((Contract) obj).scriptHash());
    }

    @Override
    public int hashCode() {
        return scriptHash().hashCode();
    }
    
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        try {
			publicKeyHash = reader.readSerializable(UInt160.class);
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
    	byte[] buffer = reader.readVarBytes();
    	parameterList = new ContractParameterType[buffer.length];
    	for (int i = 0; i < parameterList.length; i++) {
    		parameterList[i] = ContractParameterType.values()[buffer[i]];
    	}
    	addressU160 = reader.readVarBytes();
    }
    
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeSerializable(publicKeyHash);
    	byte[] buffer = new byte[parameterList.length];
    	for (int i = 0; i < buffer.length; i++) {
    		buffer[i] = (byte)parameterList[i].getValue();
    	}
        writer.writeVarBytes(buffer);
        writer.writeVarBytes(addressU160);
    }
}
