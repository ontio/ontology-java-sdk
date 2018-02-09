package ontology.sdk.info.asset;

import com.alibaba.fastjson.JSON;

import java.util.List;


public class UTXOsInfo {
	public String AssetId;
	public String AssetNme;
	public List<UTXOInfo> Utxo;
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}