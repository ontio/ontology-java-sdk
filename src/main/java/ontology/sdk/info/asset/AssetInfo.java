package ontology.sdk.info.asset;

import com.alibaba.fastjson.JSON;

public class AssetInfo {

	public int StateVersion;
	public String AssetId;
	public int AssetType;
	public String Name;
	public long Amount;
	public long Available;
	public PubKey Owner;
	public int Precision;
	public String Admin;
	public String Issuer;
	public long Expiration;
	public boolean IsFrozen;
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
class PubKey{
	public String X;
	public String Y;
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}