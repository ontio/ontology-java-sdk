package ontology.sdk.info.asset;

import com.alibaba.fastjson.JSON;

public class UTXOInfo {
	public String Txid;
	public String Index;
	public long Value;
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}