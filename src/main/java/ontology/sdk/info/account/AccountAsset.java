package ontology.sdk.info.account;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 账户资产
 * 
 * @author 12146
 *
 */
public class AccountAsset {
	public String address;
	public List<Asset> canUseAssets;	// 可用资产
	public List<Asset> freezeAssets;	// 冻结资产
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	
}