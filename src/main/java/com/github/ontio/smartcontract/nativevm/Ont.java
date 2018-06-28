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

package com.github.ontio.smartcontract.nativevm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.asset.*;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.AbiFunction;
import com.github.ontio.smartcontract.nativevm.abi.AbiInfo;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Ont {
    private OntSdk sdk;
    private final String ontContract = "0000000000000000000000000000000000000001";
    private String nativeAbi = "{\"hash\":\"0000000000000000000000000000000000000001\",\"functions\":[{\"name\":\"init\",\"parameters\":[],\"returntype\":\"Bool\"},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"transfers\",\"type\":\"Struct\",\"subType\":[{\"name\":\"from\",\"type\":\"Address\"},{\"name\":\"to\",\"type\":\"Address\"},{\"name\":\"value\",\"type\":\"Int\"}]}],\"returntype\":\"Bool\"},{\"name\":\"approve\",\"parameters\":[{\"name\":\"from\",\"type\":\"Address\"},{\"name\":\"to\",\"type\":\"Address\"},{\"name\":\"value\",\"type\":\"Int\"}],\"returntype\":\"Bool\"},{\"name\":\"transferFrom\",\"parameters\":[{\"name\":\"sender\",\"type\":\"Address\"},{\"name\":\"from\",\"type\":\"Address\"},{\"name\":\"to\",\"type\":\"Address\"},{\"name\":\"value\",\"type\":\"Int\"}],\"returntype\":\"Bool\"},{\"name\":\"name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"decimals\",\"parameters\":[],\"returntype\":\"Int\"},{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"Int\"},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"account\",\"type\":\"Address\"}],\"returntype\":\"Int\"},{\"name\":\"allowance\",\"parameters\":[{\"name\":\"account\",\"type\":\"Address\"}],\"returntype\":\"Int\"}],\"events\":[{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"Address\"},{\"name\":\"to\",\"type\":\"Address\"},{\"name\":\"value\",\"type\":\"Int\"}]}]}";
    public Ont(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String getContractAddress() {
        return ontContract;
    }

    /**
     *
     * @param sendAcct
     * @param recvAddr
     * @param amount
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransfer(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (sendAcct == null || payerAcct == null ) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }

        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }

        Transaction tx = makeTransfer(sendAcct.getAddressU160().toBase58(), recvAddr, amount, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if (!sendAcct.equals(payerAcct)) {
            sdk.addSign(tx, payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param sendAccts
     * @param recvAddr
     * @param amount
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransferFromMultiSignAddr(Account[] sendAccts,String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (sendAccts == null || sendAccts.length <= 1 || payerAcct == null ) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }
        byte[][] pks = new byte[sendAccts.length][];
        for(int i=0;i<pks.length;i++){
            pks[i] = sendAccts[i].serializePublicKey();
        }
        Address multiAddr = Address.addressFromMultiPubKeys(sendAccts.length,pks);
        Transaction tx = makeTransfer(multiAddr.toBase58(), recvAddr, amount, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{sendAccts});
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    /**
     * @param sender
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeTransfer(String sender, String recvAddr, long amount, String payer, long gaslimit, long gasprice) throws Exception {
        if (sender == null || recvAddr == null || recvAddr.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }

//        AbiInfo abiinfo = JSON.parseObject(nativeAbi, AbiInfo.class);
//        AbiFunction func = abiinfo.getFunction("transfer");
//        func.name = "transfer";
//        func.setParamsValue(new Struct().add(Address.decodeBase58(sender),Address.decodeBase58(recvAddr),amount));
//        System.out.println(func.toString());
//        byte[] params = NativeBuildParams.serializeAbiFunction(func);
//        System.out.println("params:"+Helper.toHexString(params));

        List list = new ArrayList();
        Struct[] structs = new Struct[]{new Struct().add(Address.decodeBase58(sender),Address.decodeBase58(recvAddr),amount)};
        list.add(structs);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"transfer",args,payer,gaslimit, gasprice);
        return tx;
    }

    public Transaction makeTransfer(State[] states, String payer, long gaslimit, long gasprice) throws Exception {
        if (states == null || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if(gasprice < 0 || gaslimit < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List list = new ArrayList();
        Struct[] structs = new Struct[states.length];
        for (int i = 0; i < states.length; i++) {
            structs[i] = new Struct().add(states[i].from, states[i].to, states[i].value);
        }
        list.add(structs);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)), "transfer", args, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param address
     * @return
     * @throws Exception
     */
    public long queryBalanceOf(String address) throws Exception {
        if (address == null || address.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("address should not be null"));
        }
        List list = new ArrayList();
        list.add(Address.decodeBase58(address));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"balanceOf",arg,null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res == null || res.equals("")) {
            return 0;
        }
        return Long.valueOf(res, 16);
    }

    /**
     * @param fromAddr
     * @param toAddr
     * @return
     * @throws Exception
     */
    public long queryAllowance(String fromAddr, String toAddr) throws Exception {
        if (fromAddr == null || fromAddr.equals("") || toAddr == null || toAddr.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(Address.decodeBase58(fromAddr),Address.decodeBase58(toAddr)));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"allowance",arg,null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res == null || res.equals("")) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @param sendAcct
     * @param recvAddr
     * @param amount
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendApprove(Account sendAcct, String recvAddr, long amount, Account payerAcct,  long gaslimit, long gasprice) throws Exception {
        if (sendAcct == null || payerAcct == null ) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }
        Transaction tx = makeApprove(sendAcct.getAddressU160().toBase58(), recvAddr, amount, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if (!sendAcct.equals(payerAcct)) {
            sdk.addSign(tx, payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     * @param sender
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeApprove(String sender, String recvAddr, long amount, String payer, long gaslimit, long gasprice) throws Exception {
        if (sender == null || recvAddr == null || recvAddr.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(Address.decodeBase58(sender),Address.decodeBase58(recvAddr),amount));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"approve",args,payer,gaslimit, gasprice);
        return tx;
    }

    /**
     *
     * @param sendAcct
     * @param fromAddr
     * @param toAddr
     * @param amount
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransferFrom(Account sendAcct, String fromAddr, String toAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (sendAcct == null || payerAcct == null ) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }
        Transaction tx = makeTransferFrom(sendAcct.getAddressU160().toBase58(), fromAddr, toAddr, amount, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if (!sendAcct.equals(payerAcct)) {
            sdk.addSign(tx, payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     * @param sender
     * @param fromAddr
     * @param toAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeTransferFrom(String sender, String fromAddr, String toAddr, long amount, String payer, long gaslimit, long gasprice) throws Exception {
        if (sender == null || fromAddr == null || fromAddr.equals("") || toAddr == null || toAddr.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameters should not be null"));
        }
        if (amount <= 0 || gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("amount or gasprice or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(Address.decodeBase58(sender), Address.decodeBase58(fromAddr), Address.decodeBase58(toAddr), amount));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"transferFrom",args,payer,gaslimit, gasprice);
        return tx;
    }

    /**
     * @return
     * @throws Exception
     */
    public String queryName() throws Exception {
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"name",new byte[]{0},null,0,0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     * @return
     * @throws Exception
     */
    public String querySymbol() throws Exception {
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"symbol",new byte[]{0},null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     * @return
     * @throws Exception
     */
    public long queryDecimals() throws Exception {
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"decimals",new byte[]{0},null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res, 16);
    }

    /**
     * @return
     * @throws Exception
     */
    public long queryTotalSupply() throws Exception {
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontContract)),"totalSupply",new byte[]{0},null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res == null || res.equals("")) {
            return 0;
        }
        return Long.valueOf(res, 16);
    }

}
