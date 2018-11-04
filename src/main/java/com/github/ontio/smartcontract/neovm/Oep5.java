package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.github.ontio.smartcontract.neovm.oep5.Oep5Param;

import java.util.ArrayList;
import java.util.List;

public class Oep5 {
    private OntSdk sdk;
    private String contractAddress = null;
    private String oep5abi = "{\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"\"},{\"name\":\"args\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"name\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"symbol\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"owner\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"ownerOf\",\"parameters\":[{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transfer\",\"parameters\":[{\"name\":\"toAcct\",\"type\":\"ByteArray\"},{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transferMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"\"}," +
            "{\"name\":\"approve\",\"parameters\":[{\"name\":\"toAcct\",\"type\":\"ByteArray\"},{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"takeOwnership\",\"parameters\":[{\"name\":\"toAcct\",\"type\":\"ByteArray\"},{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"concatkey\",\"parameters\":[{\"name\":\"str1\",\"type\":\"\"},{\"name\":\"str2\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"init\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"totalSupply\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"queryTokenIDByIndex\",\"parameters\":[{\"name\":\"idx\",\"type\":\"Integer\"}],\"returntype\":\"\"}," +
            "{\"name\":\"queryTokenByID\",\"parameters\":[{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"getApproved\",\"parameters\":[{\"name\":\"tokenID\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"createMultiTokens\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"createOneToken\",\"parameters\":[{\"name\":\"name\",\"type\":\"\"},{\"name\":\"url\",\"type\":\"\"},{\"name\":\"type\",\"type\":\"\"}],\"returntype\":\"\"}]}";

    public Oep5(OntSdk sdk){
        this.sdk = sdk;
    }
    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendInit(Account acct, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        return (String)sendInit(acct,payerAcct,gaslimit,gasprice,false);
    }
    private Object sendInit(Account acct, Account payerAcct,long gaslimit,long gasprice,boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("init");
        if(preExec) {
            byte[] params = BuildParams.serializeAbiFunction(func);
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params,null,gaslimit, gasprice);
            if (acct != null) {
                sdk.signTx(tx, new Account[][]{{acct}});
            }
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            if (Integer.parseInt(((JSONObject) obj).getString("Result")) != 1){
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error: "+ obj));
            }
            return ((JSONObject) obj).getLong("Gas");
        }
        if(acct == null || payerAcct == null){
            throw new SDKException(ErrorCode.ParamError);
        }
        Object obj = sdk.neovm().sendTransaction(Helper.reverse(contractAddress),acct,payerAcct,gaslimit,gasprice,func,preExec);
        return obj;
    }

    public String ownerOf(byte[] tokenID) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("ownerOf");
        func.name = "ownerOf";
        func.setParamsValue(tokenID);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }

    public String transfer(Account owner, Oep5Param oep5Transfer, Account payer, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("transfer");
        func.name = "transfer";
        func.setParamsValue(oep5Transfer.toAcct, oep5Transfer.tokenId);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),owner,payer,gaslimit,gasprice,func, false);
        return (String) obj;
    }
    public String transferMulti(Account[] owners, Oep5Param[] oep5Transfers, Account payer, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeTransferMulti(oep5Transfers, payer, gaslimit, gasprice);
        boolean haspayer = false;
        for(Account account : owners){
            if(account.equals(payer)){
                haspayer = true;
            }
            sdk.addSign(tx, account);
        }
        if(!haspayer){
            sdk.addSign(tx, payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public Transaction makeTransferMulti(Oep5Param[] oep5Transfers, Account payerAcct, long gaslimit, long gasprice) throws SDKException {
        if(oep5Transfers == null || payerAcct == null){
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List paramList = new ArrayList<>();
        paramList.add("transferMulti".getBytes());
        List tempList = new ArrayList();
        for(Oep5Param oep5Transfer : oep5Transfers){
            List list = new ArrayList();
            list.add(oep5Transfer.toAcct);
            list.add(oep5Transfer.tokenId);
            tempList.add(list);
        }
        paramList.add(tempList);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress),null,params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        return tx;
    }

    public String approve(Account owner, Oep5Param oep5Param, Account payer, long gaslimit, long gasprice) throws Exception {
        if (owner == null || oep5Param == null || payer == null) {
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("approve");
        func.setParamsValue(oep5Param.toAcct, oep5Param.tokenId);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),owner,payer,gaslimit,gasprice,func, false);
        return (String) obj;
    }
    public String takeOwnership(Account owner, Oep5Param oep5Param, Account payer, long gaslimit, long gasprice) throws Exception {
        if (owner == null || oep5Param == null || payer == null) {
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("takeOwnership");
        func.setParamsValue(oep5Param.toAcct, oep5Param.tokenId);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),owner,payer,gaslimit,gasprice,func, false);
        return (String) obj;
    }

    public String queryTotalSupply() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("totalSupply");
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return Helper.BigIntFromNeoBytes(Helper.hexToBytes(((JSONObject) obj).getString("Result"))).toString();
    }
    public String queryTokenIDByIndex(String idx) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("queryTokenIDByIndex");
        func.name = "queryTokenIDByIndex";
        func.setParamsValue(idx);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }
    public String queryTokenByID(String tokenID) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("queryTokenByID");
        func.setParamsValue(tokenID);
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }
    public String getApproved(String tokenID) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("getApproved");
        func.name = "getApproved";
        func.setParamsValue(Helper.hexToBytes(tokenID));
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }
    public String queryName() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("name");
        func.name = "name";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return new String(Helper.hexToBytes(((JSONObject) obj).getString("Result")));
    }

    public String querySymbol() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("symbol");
        func.name = "symbol";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        return new String(Helper.hexToBytes(((JSONObject) obj).getString("Result")));
    }

    public String queryBalanceOf(String addr) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(addr == null||addr.equals("")){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("balanceOf");
        func.name = "balanceOf";
        func.setParamsValue(Address.decodeBase58(addr).toArray());
        Object obj =  sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        String balance = ((JSONObject) obj).getString("Result");
        if(balance.equals("")){
            balance = "00";
        }
        return Helper.BigIntFromNeoBytes(Helper.hexToBytes(balance)).toString();
    }
    public String queryTokenIDByIndex(int index) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(index < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep5abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("queryTokenIDByIndex");
        func.setParamsValue((long)index);
        Object obj =  sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        String tokenId = ((JSONObject) obj).getString("Result");
        if(tokenId.equals("")){
            tokenId = "00";
        }
        return tokenId;
    }
}

