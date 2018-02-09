package ontology.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import ontology.account.KeyType;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.util.BigIntegers;

import ontology.common.UInt160;
import ontology.crypto.*;
import ontology.io.*;
import ontology.account.Acct;
import ontology.crypto.sm.*;

/**
 *  为需要签名的数据提供一个接口
 */
public interface Signable extends Serializable {
    /**
     *  反序列化未签名的数据
     *  <param name="reader">数据来源</param>
     * @throws IOException 
     */
    void deserializeUnsigned(BinaryReader reader) throws IOException;
    
    /**
     *  序列化未签名的数据
     *  <param name="writer">存放序列化后的结果</param>
     * @throws IOException 
     */
    void serializeUnsigned(BinaryWriter writer) throws IOException;

    /**
     *  获得需要校验的脚本Hash值
     *  <returns>返回需要校验的脚本Hash值</returns>
     */
    UInt160[] getScriptHashesForVerifying();
    
    default byte[] getHashData() {
    	try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
	    	try (BinaryWriter writer = new BinaryWriter(ms)) {
	            serializeUnsigned(writer);
	            writer.flush();
	            return ms.toByteArray();
	        }
    	} catch (IOException ex) {
    		throw new UnsupportedOperationException(ex);
    	}
    }
    
    default byte[] sign(Acct account, String Algrithem) {
		if(Algrithem.equals(KeyType.SM2.name())){
    		byte[] rst = SM2Utils.generateSignature(account.privateKey,getHashData());
			return  rst;
		}
		ECDSASigner signer = new ECDSASigner();
    	signer.init(true, new ECPrivateKeyParameters(new BigInteger(1, account.privateKey), ECC.secp256r1));
		BigInteger[] bi = signer.generateSignature(Digest.sha256(getHashData()));// dna
    	byte[] signature = new byte[64];
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[0]), 0, signature, 0, 32);
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[1]), 0, signature, 32, 32);
    	return signature;
    }
    
    default boolean verifySignature(String Algrithem,ECPoint pubkey,byte[] data,byte[] signature) {
		boolean result = false;
		if(Algrithem.equals("SM2")) {
			result = SM2Utils.verifySignature(pubkey, data, signature);
		}else {
			ECDSASigner signer = new ECDSASigner();
			signer.init(false, new ECPublicKeyParameters(pubkey, ECC.secp256r1));
			byte[] r = new byte[32];
			byte[] s = new byte[32];
			System.arraycopy(signature, 0, r, 0, 32);
			System.arraycopy(signature, 32, s, 0, 32);
			result = signer.verifySignature(Digest.sha256(data), new BigInteger(1, r), new BigInteger(1, s));
		}
		return result;
    }
	default boolean verifySignature() {
    	return true;
    }
}
