/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.sdk.info.account;

import com.alibaba.fastjson.JSON;

/**
 * 账户信息
 * 
 * @author 12146
 *
 */
public class AccountInfo {
	public String address;	// 地址
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