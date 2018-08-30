package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.util.ArrayList;
import java.util.List;

public class Oep4 {
    private OntSdk sdk;
    private String contractAddress = null;
    private String oep4abi = "{\"hash\":\"0xcf409996f7abbf8afd659a298c26cd1239e0c19e\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Init\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"Transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"TransferMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"BalanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"},{\"name\":\"TotalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Approve\",\"parameters\":[{\"name\":\"owner\",\"type\":\"ByteArray\"},{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"amount\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"TransferFrom\",\"parameters\":[{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"amount\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"Allowance\",\"parameters\":[{\"name\":\"owner\",\"type\":\"ByteArray\"},{\"name\":\"spender\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"}],\"events\":[{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Void\"},{\"name\":\"approval\",\"parameters\":[{\"name\":\"onwer\",\"type\":\"ByteArray\"},{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Void\"}]}";

    public Oep4(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendInit(Account acct, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        return (String)sendInit(acct,payerAcct,gaslimit,gasprice,false);
    }

    public long sendInitPreExec(Account acct, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        return (long)sendInit(acct,payerAcct,gaslimit,gasprice,true);
    }

    private Object sendInit(Account acct, Account payerAcct,long gaslimit,long gasprice,boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Init");
        func.name = "init";
        if(preExec) {
            byte[] params = BuildParams.serializeAbiFunction(func);
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params,null,0, 0);
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
        Object obj = sdk.neovm().sendTransaction(contractAddress,acct,payerAcct,gaslimit,gasprice,func,preExec);
        return obj;
    }

    public String sendTransfer(Account acct, String recvAddr, long amount,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        return (String)sendTransfer(acct, recvAddr, amount,payerAcct,gaslimit,gasprice, false);
    }

    public long sendTransferPreExec(Account acct, String recvAddr, long amount) throws Exception {
        return (long)sendTransfer(acct, recvAddr, amount,acct,0,0, true);
    }

    private Object sendTransfer(Account acct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice, boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(acct == null || payerAcct == null || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        String sendAddr = acct.getAddressU160().toBase58();
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), amount);
        if(preExec) {
            byte[] params = BuildParams.serializeAbiFunction(func);

            Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params,null,0, 0);
            sdk.signTx(tx, new Account[][]{{acct}});
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            if (Integer.parseInt(((JSONObject) obj).getString("Result")) != 1){
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error: "+ obj));
            }
            return ((JSONObject) obj).getLong("Gas");
        }
        Object obj = sdk.neovm().sendTransaction(contractAddress,acct,payerAcct,gaslimit,gasprice,func, preExec);
        return obj;
    }
    public Transaction makeTransfer(String sendAddr,String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        if(sendAddr==null||sendAddr.equals("")||recvAddr == null || recvAddr.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    public String sendTransferMulti(Account[] accounts, State[] states,Account payerAcct,long gaslimit, long gasprice) throws Exception {
        if(accounts == null || states == null || payerAcct == null){
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        Transaction tx = makeTransferMulti(states,payerAcct,gaslimit,gasprice);
        boolean haspayer = false;
        for(Account account : accounts){
            if(account.equals(payerAcct)){
                haspayer = true;
            }
            sdk.addSign(tx, account);
        }
        if(!haspayer){
            sdk.addSign(tx, payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }
    public Transaction makeTransferMulti(State[] states,Account payerAcct,long gaslimit, long gasprice) throws SDKException {
        if(states == null || payerAcct == null){
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List paramList = new ArrayList<>();
        paramList.add("transferMulti".getBytes());
        for(State state : states){
            List list = new ArrayList();
            list.add(state.from.toArray());
            list.add(state.to.toArray());
            list.add(state.value);
            paramList.add(list);
        }
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,null,params,payerAcct.getAddressU160().toBase58(),20000,0);
        return tx;
    }

    public String sendApprove(Account owner, String spender, long amount,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(owner == null || payerAcct == null || gaslimit<0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Approve");
        func.name = "approve";
        func.setParamsValue(owner.getAddressU160().toArray(), Address.decodeBase58(spender).toArray(), amount);
        Object obj = sdk.neovm().sendTransaction(contractAddress,owner,payerAcct,gaslimit,gasprice,func, false);
        return (String) obj;
    }

    public Transaction makeApprove(String owner,String spender, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        if(owner==null||owner.equals("")||spender == null || spender.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Approve");
        func.name = "approve";
        func.setParamsValue(Address.decodeBase58(owner).toArray(), Address.decodeBase58(spender).toArray(), amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }


    public Object sendTransferFrom(Account sender, String from,String to, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(sender == null || payerAcct == null || gaslimit<0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("TransferFrom");
        func.name = "transferFrom";
        func.setParamsValue(sender.getAddressU160().toArray(), Address.decodeBase58(from).toArray(),Address.decodeBase58(to).toArray(), amount);
        Object obj = sdk.neovm().sendTransaction(contractAddress,sender,payerAcct,gaslimit,gasprice,func, false);
        return obj;
    }

    public Transaction makeTransferFrom(String sender, String from,String to, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(sender==null||sender.equals("")||from == null || from.equals("")||to== null || to.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0
                || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("TransferFrom");
        func.name = "transferFrom";
        func.setParamsValue(Address.decodeBase58(sender).toArray(), Address.decodeBase58(from).toArray(),Address.decodeBase58(to).toArray(), amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    public String queryAllowance(String owner, String spender) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(owner == null||owner.equals("")||spender==null||spender.equals("")){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Allowance");
        func.name = "allowance";
        func.setParamsValue(Address.decodeBase58(owner).toArray(), Address.decodeBase58(spender).toArray());
        Object obj =  sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        String balance = ((JSONObject) obj).getString("Result");
        if(balance.equals("")){
            balance = "00";
        }
        return balance;
    }

    public String queryBalanceOf(String addr) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(addr == null||addr.equals("")){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("BalanceOf");
        func.name = "balanceOf";
        func.setParamsValue(Address.decodeBase58(addr).toArray());
        Object obj =  sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        String balance = ((JSONObject) obj).getString("Result");
        if(balance.equals("")){
            balance = "00";
        }
        return balance;
    }

    public String queryTotalSupply() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
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
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
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
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
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
        AbiInfo abiinfo = JSON.parseObject(oep4abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Symbol");
        func.name = "symbol";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return ((JSONObject) obj).getString("Result");
    }


}
