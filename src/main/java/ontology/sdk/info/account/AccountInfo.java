package ontology.sdk.info.account;

import com.alibaba.fastjson.JSON;

/**
 * 账户信息
 * 
 * @author 12146
 *
 */
public class AccountInfo {
	public String address;	// 合约地址
	public String pubkey;	// 公钥
//	private String prikey;	// 私钥
	private String priwif;	// 私钥wif
	public String encryptedprikey; //加密后的私钥
	public String pkhash;	// 公钥hash
	public void setPrikey(String prikey){
		//this.prikey = prikey;
	}
//	public String getPrikey(){
//		return prikey;
//	}
	public void setPriwif(String priwif){
		this.priwif = priwif;
	}
	public String getPriwif(){
		return priwif;
	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	
}