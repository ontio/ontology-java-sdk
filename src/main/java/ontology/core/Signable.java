package ontology.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import ontology.account.KeyType;
import ontology.common.Helper;
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
    UInt160[] getAddressU160ForVerifying();
    
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
		BigInteger[] bi = signer.generateSignature(Digest.sha256(Digest.sha256(Digest.sha256(getHashData()))));// dna
    	byte[] signature = new byte[64];
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[0]), 0, signature, 0, 32);
    	System.arraycopy(BigIntegers.asUnsignedByteArray(32, bi[1]), 0, signature, 32, 32);
		System.out.println("publicKey:"+ Helper.toHexString(account.publicKey.getEncoded(true)));
		System.out.println("signature:"+ Helper.toHexString(signature));
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
//00d1be6b3e520100000000000000006f210268f9f8ff314940dd6d068af67999c9fd87df729742714f93be0c4070681510550876616c7565733032056279746573036b65792a6469643a6f6e743a5451487a6a41344a69374b335a775841416f75624e71684e75724a4b58783177593555c10c41646441747472696275746501002435663137336437382d396165612d343334362d616463642d3436623936666164346639640101000000000000009d1f6b149ddd2324fdfc10992c8475648585a8c50000000000000000
//00d1be6b3e520100000000000000006f210268f9f8ff314940dd6d068af67999c9fd87df729742714f93be0c4070681510550876616c7565733032056279746573036b65792a6469643a6f6e743a5451487a6a41344a69374b335a775841416f75624e71684e75724a4b58783177593555c10c41646441747472696275746501002435663137336437382d396165612d343334362d616463642d3436623936666164346639640101000000000000009d1f6b149ddd2324fdfc10992c8475648585a8c50000000000000000