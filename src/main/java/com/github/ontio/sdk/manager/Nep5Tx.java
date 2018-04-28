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

package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.*;
import com.github.ontio.core.payload.Vote;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;


/**
 *
 */
public class Nep5Tx {
    private OntSdk sdk;
    private String contractAddr = null;
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

    public Nep5Tx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.contractAddr = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return contractAddr;
    }

    public String sendInit() throws Exception {
        return sendInit(false);
    }

    public String sendInitPreExec() throws Exception {
        return sendInit(true);
    }

    public String sendInit(boolean preExec) throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Init");
        func.name = "init";
        if (preExec) {
            String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
            if (Integer.parseInt(result) > 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return result;
        } else {
            return sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSign(func, VmType.NEOVM.value());
        }
    }


    /**
     * @param sendAddr
     * @param password
     * @param amount
     * @param recvAddr
     * @return
     * @throws Exception
     */
    public String sendTransfer(String sendAddr, String password, String recvAddr, int amount) throws Exception {
        return sendTransfer(sendAddr, password, recvAddr, amount, false);
    }

    public String sendTransferPreExec(String sendAddr, String password, String recvAddr, int amount) throws Exception {
        return sendTransfer(sendAddr, password, recvAddr, amount, true);
    }

    public String sendTransfer(String sendAddr, String password, String recvAddr, int amount, boolean preExec) throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), amount);

        if (preExec) {
            String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithSignPreExec(sendAddr, password, func, VmType.NEOVM.value());
            if (Integer.parseInt(result) > 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return result;
        } else {
            return sdk.getSmartcodeTx().sendInvokeSmartCodeWithSign(sendAddr, password, func, VmType.NEOVM.value());
        }
    }

    public String sendBalanceOf(String addr) throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("BalanceOf");
        func.name = "balanceOf";
        func.setParamsValue(Address.decodeBase58(addr).toArray());
        String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
        return result;
    }

    public String sendTotalSupply() throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("TotalSupply");
        func.name = "totalSupply";
        func.setParamsValue();
        String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
        return result;
    }

    public String sendName() throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Name");
        func.name = "name";
        func.setParamsValue();
        String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
        return result;
    }

    public String sendDecimals() throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Decimals");
        func.name = "decimals";
        func.setParamsValue();
        String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
        return result;
    }

    public String sendSymbol() throws Exception {
        if (contractAddr == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Symbol");
        func.name = "symbol";
        func.setParamsValue();
        String result = (String) sdk.getSmartcodeTx().sendInvokeSmartCodeWithNoSignPreExec(func, VmType.NEOVM.value());
        return result;
    }

}
