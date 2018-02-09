package ontology.sdk.info.mutil;

import com.alibaba.fastjson.JSON;

/**
 * Transaction Joiner, which mostly used to be ouputs of transaction, named recver
 * and it includes receiver'address,assetid,and asset amount
 * 
 * @author 12146
 *
 */
public class TxJoiner {
	public String address;
	public String assetid;
	public long value;
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}