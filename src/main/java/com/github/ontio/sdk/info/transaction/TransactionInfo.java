package com.github.ontio.sdk.info.transaction;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 交易信息
 * 
 * @author 12146
 *
 */
public class TransactionInfo {
	public String txid;				// 交易编号
	public String type;				// 交易类型
	public String attrs;			// 描述
	
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}