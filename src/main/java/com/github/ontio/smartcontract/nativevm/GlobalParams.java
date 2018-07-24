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

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.globalparams.Param;
import com.github.ontio.core.globalparams.Params;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalParams {
    private OntSdk sdk;
    private final String contractAddress = "0000000000000000000000000000000000000004";
    public GlobalParams(OntSdk sdk) {
        this.sdk = sdk;
    }

    public boolean init() throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"init", new byte[]{}, null,0,0);
        return sdk.getConnect().sendRawTransaction(tx.toHexString());
    }

    public String transferAdmin(Account adminAccount,Address newAdminAddr,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if(adminAccount==null || newAdminAddr == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeTransferAdmin(newAdminAddr,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{adminAccount}});
        if(!adminAccount.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }
    public String transferAdmin(int M,Account[] accounts,Address newAdminAddr,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if(accounts.length== 0 || newAdminAddr == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(accounts.length < M) {
            throw new SDKException("the accounts length should not be less than M");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeTransferAdmin(newAdminAddr,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{accounts},new int[]{M});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public Transaction makeTransferAdmin(Address newAdminAddr,String payerAddr, long gaslimit,long gasprice) throws SDKException {
        if(newAdminAddr == null || payerAddr == null || payerAddr.equals("")){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(newAdminAddr.toArray());
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"transferAdmin",arg,payerAddr,gaslimit,gasprice);
        return tx;
    }

    public String acceptAdmin(Account account,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if(account == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeAcceptAdmin(account.getAddressU160(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }
    public String acceptAdmin(Address multiAddr,int M,Account[] accounts,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if(accounts.length== 0 || multiAddr == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(accounts.length < M) {
            throw new SDKException("the accounts length should not be less than M");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeAcceptAdmin(multiAddr,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{accounts},new int[]{M});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }
    public Transaction makeAcceptAdmin(Address multiAddr,String payerAddr, long gaslimit,long gasprice) throws SDKException {
        if(multiAddr == null || payerAddr == null || payerAddr.equals("")){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(multiAddr.toArray());
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"transferAdmin",arg,payerAddr,gaslimit,gasprice);
        return tx;
    }


    public String getGlobalParam(String[] paramNameList) throws SDKException, ConnectorException, IOException {
        if(paramNameList.length == 0) {
            throw new SDKException("parameter should not be less than 0");
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(paramNameList.length);
        for (int i = 0; i < paramNameList.length; i++) {
            struct.add(paramNameList[i]);
        }
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"getGlobalParam",arg,null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        return res;
    }

    public String setGlobalParam(Account operatorAccount,Params params, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(operatorAccount == null || params == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeSetGlobalParam(params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{operatorAccount}});
        if(!operatorAccount.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public String setGlobalParam(int M,Account[] operatorAccounts,Params params, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(operatorAccounts.length== 0 || params == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(operatorAccounts.length < M) {
            throw new SDKException("the operatorAccounts length should not be less than M");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeSetGlobalParam(params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{operatorAccounts},new int[]{M});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public Transaction makeSetGlobalParam(Params params,String payerAddr,long gaslimit,long gasprice) throws SDKException {
        if(params == null || payerAddr == null || payerAddr.equals("")){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(params.params.length);
        for(int i=0;i< params.params.length;i++){
            struct.add(params.params[i].key,params.params[i].value);
        }
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"setGlobalParam",arg,payerAddr,gaslimit,gasprice);
        return tx;
    }


    public String setOperator(Account adminAccount,Address addr,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(adminAccount == null || addr == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeSetOperator(addr,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{adminAccount}});
        if(!adminAccount.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public String setOperator(int M,Account[] accounts,Address addr,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(accounts.length == 0 || addr == null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(accounts.length < M){
            throw new SDKException("accounts length should not be less than M");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeSetOperator(addr,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{accounts},new int[]{M});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public Transaction makeSetOperator(Address addr,String payerAddr,long gaslimit,long gasprice) throws SDKException {
        if(addr==null || payerAddr == null || payerAddr.equals("")){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(addr.toArray());
        list.add(struct);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"transferAdmin",arg,payerAddr,gaslimit,gasprice);
        return tx;
    }

    public String createSnapshot(Account operatorAccount,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(operatorAccount==null || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeCreateSnapshot(payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{operatorAccount}});
        if(!operatorAccount.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }
    public String createSnapshot(int M,Account[] operatorAccounts,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        if(operatorAccounts.length==0 || payerAcct == null){
            throw new SDKException("parameter should not be null");
        }
        if(operatorAccounts.length < M){
            throw new SDKException("accounts length should not be less than M");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeCreateSnapshot(payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{operatorAccounts},new int[]{M});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b) {
            return tx.hash().toHexString();
        }
        return null;
    }
    public Transaction makeCreateSnapshot(String payerAddr,long gaslimit,long gasprice) throws SDKException {
        if(payerAddr == null || payerAddr.equals("")){
            throw new SDKException("parameter should not be null");
        }
        if(gaslimit < 0 || gasprice < 0 ){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"createSnapshot",new byte[]{0},payerAddr,gaslimit,gasprice);
        return tx;
    }
}
