package ontology.sdk.info.transaction;

import com.alibaba.fastjson.JSON;

/**
 * 交易输出
 * 
 * @author 12146
 *
 */
public class TxOutputInfo {
	public String address;	// 地址
	public String assetid;	// 资产编号
	public long amount;		// 资产数量
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}