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

package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.exception.SDKException;


/**
 *
 */
public class Nep5 {
    private OntSdk sdk;
    private String contractAddress = null;
    private String nep5abi = "{\"hash\":\"0xd17d91a831c094c1fd8d8634b8cd6fa9fbaedc99\",\"entrypoint\":\"Main\"," +
            "\"functions\":[{\"name\":\"Name\",\"parameters\":[],\"returntype\":\"String\"}," +
            "{\"name\":\"Symbol\",\"parameters\":[],\"returntype\":\"String\"}," +
            "{\"name\":\"Decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"}," +
            "{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"}," +
            "{\"name\":\"Init\",\"parameters\":[],\"returntype\":\"Boolean\"}," +
            "{\"name\":\"TotalSupply\",\"parameters\":[],\"returntype\":\"Integer\"}," +
            "{\"name\":\"Transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"}," +
            "{\"name\":\"BalanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"}]," +
            "\"events\":[{\"name\":\"transfer\",\"parameters\":[{\"name\":\"arg1\",\"type\":\"ByteArray\"},{\"name\":\"arg2\",\"type\":\"ByteArray\"},{\"name\":\"arg3\",\"type\":\"Integer\"}],\"returntype\":\"Void\"}]}";

    public Nep5(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendInit(String payer,String password,long gaslimit,long gas) throws Exception {
        return (String)sendInit(payer,password,gaslimit,gas,false);
    }

    public long sendInitGetGasLimit() throws Exception {
        return (long)sendInit(null,null,0,0,true);
    }

    private Object sendInit(String payer,String password,long gaslimit,long gas,boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Init");
        func.name = "init";
        Object obj = sdk.neovm().sendTransaction(contractAddress,payer,password,gaslimit,gas,func,preExec);
        if(preExec) {
            if (Integer.parseInt(((JSONObject) obj).getString("Result")) != 1){
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return ((JSONObject) obj).getLong("Gas");
        }
        return obj;
    }


    /**
     * @param sendAddr
     * @param password
     * @param amount
     * @param recvAddr
     * @return
     * @throws Exception
     */
    public String sendTransfer(String sendAddr, String password, String recvAddr, long amount,long gaslimit,long gas) throws Exception {
        return (String)sendTransfer(sendAddr, password, recvAddr, amount,gaslimit,gas, false);
    }

    public long sendTransferGetGasLimit(String sendAddr, String password, String recvAddr, long amount) throws Exception {
        return (long)sendTransfer(sendAddr, password, recvAddr, amount,0,0, true);
    }

    private Object sendTransfer(String sendAddr, String password, String recvAddr, long amount,long gaslimit, long gas,boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), amount);
        if(preExec) {
            byte[] params = BuildParams.serializeAbiFunction(func);

            Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, VmType.NEOVM.value(), null,0, 0);
            sdk.signTx(tx, sendAddr, password);
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            if (Integer.parseInt(((JSONObject) obj).getString("Result")) != 1){
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return ((JSONObject) obj).getLong("Gas");
        }
        Object obj = sdk.neovm().sendTransaction(contractAddress,sendAddr,password,gaslimit,gas,func, preExec);
        return obj;
    }

    public String queryBalanceOf(String addr) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("BalanceOf");
        func.name = "balanceOf";
        func.setParamsValue(Address.decodeBase58(addr).toArray());
        Object obj =  sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }

    public String queryTotalSupply() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("TotalSupply");
        func.name = "totalSupply";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }

    public String queryName() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Name");
        func.name = "name";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }

    public String queryDecimals() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Decimals");
        func.name = "decimals";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }

    public String querySymbol() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Symbol");
        func.name = "symbol";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }


}
