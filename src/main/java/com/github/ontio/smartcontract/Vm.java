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

package com.github.ontio.smartcontract;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.OntSdk;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.Parameter;
import com.github.ontio.sdk.info.AccountInfo;
import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.rmi.dgc.VMID;
import java.util.*;

/**
 *
 */
public class Vm {
    private OntSdk sdk;
    private String contractAddress = null;
    public static  String NATIVE_INVOKE_NAME = "Ontology.Native.Invoke";
    public String getCodeAddress() {
        return contractAddress;
    }
    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public Vm(OntSdk sdk) {
        this.sdk = sdk;
    }


    /**
     *
     * @param codeStr
     * @param needStorage
     * @param name
     * @param codeVersion
     * @param author
     * @param email
     * @param desp
     * @param vmtype
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public DeployCode makeDeployCodeTransaction(String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype,String payer,long gaslimit,long gasprice) throws SDKException {
        DeployCode tx = new DeployCode();
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        tx.attributes = new Attribute[1];
        tx.attributes[0] = new Attribute();
        tx.attributes[0].usage = AttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = Helper.hexToBytes(codeStr);
        tx.version = codeVersion;
        tx.needStorage = needStorage;
        tx.name = name;
        tx.author = author;
        tx.email = email;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        tx.description = desp;
        return tx;
    }

    public InvokeCode makeInvokeCodeTransaction(String codeAddr,String method,byte[] params, String payer,long gaslimit,long gasprice) throws SDKException {

        Contract contract = new Contract((byte) 0, Address.parse(codeAddr), method, params);
        params = Helper.addBytes(new byte[]{0x67}, contract.toArray());
        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[1];
        tx.attributes[0] = new Attribute();
        tx.attributes[0].usage = AttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = params;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        return tx;
    }
    /**
     *
     * @param params
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public InvokeCode makeInvokeCodeTransaction(byte[] params,String payer,long gaslimit,long gasprice) throws SDKException {
//        if(vmtype == VmType.NEOVM.value()) {
//            Contract contract = new Contract((byte) 0,Address.parse(codeAddr), "", params);
//            params = Helper.addBytes(new byte[]{0x67}, contract.toArray());
//        }else if(vmtype == VmType.WASMVM.value()) {
//            Contract contract = new Contract((byte) 1,Address.parse(codeAddr), method, params);
//            params = contract.toArray();
//        } else if(vmtype == VmType.Native.value()) {
//            Contract contract = new Contract((byte) 0,Address.parse(codeAddr), method, params);
//            params = contract.toArray();
//        }
        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[0];
//        tx.attributes[0] = new Attribute();
//        tx.attributes[0].usage = AttributeUsage.Nonce;
//        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = params;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        return tx;
    }

    public Transaction buildNativeParams(Address codeAddr,String initMethod,byte[] args,String payer,long gaslimit,long gasprice) throws SDKException {
        ScriptBuilder sb = new ScriptBuilder();
        sb.push(args);
        sb.push(initMethod.getBytes());
        sb.push(codeAddr.toArray());
        sb.push(BigInteger.valueOf(0));
        sb.add(ScriptOp.OP_SYSCALL);
        sb.push(NATIVE_INVOKE_NAME.getBytes());
        System.out.println(Helper.toHexString(sb.toArray()));
        Transaction tx = makeInvokeCodeTransaction(sb.toArray(),payer,gaslimit,gasprice);
        return tx;
    }
}
