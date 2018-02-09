package ontology.core.contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Stream;

import ontology.common.Common;
import ontology.common.Helper;
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
    public byte[] redeemScript;
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
            _address = Common.toAddress(scriptHash());
        }
        return _address;
    }

    /**
     *  脚本散列值
     */
    private UInt160 _scriptHash;
    public UInt160 scriptHash() {
        if (_scriptHash == null) {
            _scriptHash = Program.toScriptHash(redeemScript);
        }
        return _scriptHash;
    }
    
    public boolean isStandard() {
    	if (redeemScript.length != 35) {
    		return false;
    	}
        if (redeemScript[0] != 33 || redeemScript[34] != ScriptOp.OP_CHECKSIG.getByte()) {
            return false;
        }
        return true;
    }
    
    public static Contract create(UInt160 publicKeyHash, ContractParameterType[] parameterList, byte[] redeemScript) {
    	Contract contract = new Contract();
    	contract.redeemScript = redeemScript;
    	contract.parameterList = parameterList;
    	contract.publicKeyHash = publicKeyHash;
    	return contract;
    }

    public static Contract createSignatureContract(ECPoint publicKey) {
        Contract contract = new Contract();
    	contract.redeemScript = createSignatureRedeemScript(publicKey);
    	contract.parameterList = new ContractParameterType[] { ContractParameterType.Signature };
    	contract.publicKeyHash = Program.toScriptHash(publicKey.getEncoded(true));
    	return contract;
    }
    
    public static byte[] createSignatureRedeemScript(ECPoint publicKey) {
        try (ScriptBuilder sb = new ScriptBuilder()) {
	        sb.push(publicKey.getEncoded(true));
	        sb.add(ScriptOp.OP_CHECKSIG);
	        return sb.toArray();
        }
    }
    public static byte[] createSignatureRedeemScript(String publicKey) {
        try (ScriptBuilder sb = new ScriptBuilder()) {
            sb.push(Helper.hexToBytes(publicKey));
            sb.add(ScriptOp.OP_CHECKSIG);
            return sb.toArray();
        }
    }
    public static Contract createMultiSigContract(UInt160 publicKeyHash, int m, ECPoint ...publicKeys) {
        Contract contract = new Contract();
    	contract.redeemScript = createMultiSigRedeemScript(m, publicKeys);
    	contract.parameterList = Stream.generate(() -> ContractParameterType.Signature).limit(m).toArray(ContractParameterType[]::new);
    	contract.publicKeyHash = publicKeyHash;
    	return contract;
    }
    
    public static byte[] createMultiSigRedeemScript(int m, ECPoint ...publicKeys) {
        if (!(1 <= m && m <= publicKeys.length && publicKeys.length <= 1024)) {
            throw new IllegalArgumentException();
        }
        try (ScriptBuilder sb = new ScriptBuilder()) {
	        sb.push(BigInteger.valueOf(m));
	        ECPoint[] ecPoint = Arrays.stream(publicKeys).sorted((o1,o2) -> {
//	        	if(o1.getYCoord().toString().compareTo(o2.getYCoord().toString()) == 0) {
//	        		return o1.getXCoord().toString().compareTo(o2.getXCoord().toString());
//	        	}
//	        	return o1.getYCoord().toString().compareTo(o2.getYCoord().toString());
                return Helper.toHexString(o1.getEncoded(true)).compareTo(Helper.toHexString(o2.getEncoded(true)));
	        }).toArray(ECPoint[]::new);
	        
	        for (ECPoint publicKey : ecPoint) {
	            sb.push(publicKey.getEncoded(true));
	        }
	        sb.push(BigInteger.valueOf(publicKeys.length));
	        sb.add(ScriptOp.OP_CHECKMULTISIG);
	        return sb.toArray();
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
    	redeemScript = reader.readVarBytes();
    }
    
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeSerializable(publicKeyHash);
    	byte[] buffer = new byte[parameterList.length];
    	for (int i = 0; i < buffer.length; i++) {
    		buffer[i] = (byte)parameterList[i].getValue();
    	}
        writer.writeVarBytes(buffer);
        writer.writeVarBytes(redeemScript);
    }
}
