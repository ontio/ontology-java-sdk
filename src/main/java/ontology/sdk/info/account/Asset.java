package ontology.sdk.info.account;

import com.alibaba.fastjson.JSON;

/**
 * 账户资产
 * 
 * @author 12146
 *
 */
public class Asset {
	public String assetid;	// 资产编号
	public long amount;		// 资产数量
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	
}