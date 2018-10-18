package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.oep8.Oep8State;
import com.github.ontio.core.oep8.TransferFrom;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Oep8 {
    private OntSdk sdk;
    private String contractAddress = null;
    private String oep8abi = "{\"functions\":[{\"name\":\"Revert\",\"parameters\":[{\"name\":\"\",\"type\":\"\"}]," +
            "\"returntype\":\"\"},{\"name\":\"Require\",\"parameters\":[{\"name\":\"condition\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"RequireScriptHash\",\"parameters\":[{\"name\":\"key\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"RequireWitness\",\"parameters\":[{\"name\":\"witness\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"\"},{\"name\":\"args\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"name\",\"parameters\":[{\"name\":\"tokenId\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"symbol\",\"parameters\":[{\"name\":\"tokenId\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"totalSupply\",\"parameters\":[{\"name\":\"tokenId\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"acct\",\"type\":\"ByteArray\"},{\"name\":\"tokenId\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transfer\",\"parameters\":[{\"name\":\"fromAcct\",\"type\":\"ByteArray\"},{\"name\":\"toAcct\",\"type\":\"ByteArray\"},{\"name\":\"tokenId\",\"type\":\"ByteArray\"},{\"name\":\"amount\",\"type\":\"Integer\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transferMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"\"}," +
            "{\"name\":\"approve\",\"parameters\":[{\"name\":\"owner\",\"type\":\"ByteArray\"},{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"tokenId\",\"type\":\"ByteArray\"},{\"name\":\"amount\",\"type\":\"Integer\"}],\"returntype\":\"\"}," +
            "{\"name\":\"approveMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"\"}," +
            "{\"name\":\"allowance\",\"parameters\":[{\"name\":\"owner\",\"type\":\"ByteArray\"},{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"tokenId\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transferFrom\",\"parameters\":[{\"name\":\"spender\",\"type\":\"ByteArray\"},{\"name\":\"fromAcct\",\"type\":\"ByteArray\"},{\"name\":\"toAcct\",\"type\":\"ByteArray\"},{\"name\":\"tokenId\",\"type\":\"ByteArray\"},{\"name\":\"amount\",\"type\":\"Integer\"}],\"returntype\":\"\"}," +
            "{\"name\":\"transferFromMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"\"}," +
            "{\"name\":\"compound\",\"parameters\":[{\"name\":\"acct\",\"type\":\"ByteArray\"}],\"returntype\":\"\"}," +
            "{\"name\":\"concatkey\",\"parameters\":[{\"name\":\"str1\",\"type\":\"\"},{\"name\":\"str2\",\"type\":\"\"}],\"returntype\":\"\"}," +
            "{\"name\":\"init\",\"parameters\":[],\"returntype\":\"\"}," +
            "{\"name\":\"createMultiKindsPumpkin\",\"parameters\":[{\"name\":\"\",\"type\":\"Array\"}],\"returntype\":\"\"}," +
            "{\"name\":\"checkTokenPrefix\",\"parameters\":[{\"name\":\"tokenPrefix\",\"type\":\"\"}],\"returntype\":\"\"}]}";
    public Oep8(OntSdk sdk) {
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

    public long sendInitPreExec(Account acct, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        return (long)sendInit(acct,payerAcct,gaslimit,gasprice,true);
    }

    private Object sendInit(Account acct, Account payerAcct,long gaslimit,long gasprice,boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("init");
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

    public String sendTransfer(Account acct, String recvAddr, byte[] tokenId, long amount,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        return (String)sendTransfer(acct, recvAddr, tokenId, amount,payerAcct,gaslimit,gasprice, false);
    }

    public long sendTransferPreExec(Account acct, String recvAddr, byte[] tokenId, long amount) throws Exception {
        return (long)sendTransfer(acct, recvAddr, tokenId, amount,acct,0,0, true);
    }

    private Object sendTransfer(Account acct, String recvAddr,byte[] tokenId, long amount, Account payerAcct, long gaslimit, long gasprice, boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(acct == null || payerAcct == null || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        String sendAddr = acct.getAddressU160().toBase58();
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), tokenId, amount);
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
    public Transaction makeTransfer(String sendAddr,String recvAddr, byte[] tokenId, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        if(sendAddr==null||sendAddr.equals("")||recvAddr == null || recvAddr.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("Transfer");
        func.name = "transfer";
        func.setParamsValue(Address.decodeBase58(sendAddr).toArray(), Address.decodeBase58(recvAddr).toArray(), tokenId, amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    public String sendTransferMulti(Account[] accounts, Oep8State[] states, Account payerAcct, long gaslimit, long gasprice) throws Exception {
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
    public Transaction makeTransferMulti(Oep8State[] states,Account payerAcct,long gaslimit, long gasprice) throws Exception {
        if(states == null || payerAcct == null){
            throw new SDKException("params should not be null");
        }
        if(gaslimit < 0 || gasprice< 0){
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }
        List paramList = new ArrayList<>();
        paramList.add("transferMulti".getBytes());
        List tempList = new ArrayList();
        for(Oep8State state : states){
            List list = new ArrayList();
            list.add(state.from);
            list.add(state.to);
            list.add(state.tokenId);
            list.add(state.value);
            tempList.add(list);
        }
        paramList.add(tempList);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,null,params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        return tx;
    }

    public String sendApprove(Account owner, String spender, byte[] tokenId, long amount,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(owner == null || payerAcct == null || gaslimit<0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("approve");
        func.name = "approve";
        func.setParamsValue(owner.getAddressU160().toArray(), Address.decodeBase58(spender).toArray(), tokenId, amount);
        Object obj = sdk.neovm().sendTransaction(contractAddress,owner,payerAcct,gaslimit,gasprice,func, false);
        return (String) obj;
    }

    public Transaction makeApprove(String owner,String spender, byte[] tokenId,long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        if(owner==null||owner.equals("")||spender == null || spender.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("approve");
        func.name = "approve";
        func.setParamsValue(Address.decodeBase58(owner).toArray(), Address.decodeBase58(spender).toArray(), tokenId, amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    public String sendApproveMulti(Account[] owner, Oep8State[] states,Account payerAcct, long gaslimit,long gasprice) throws Exception {
        if(owner==null||owner.equals("")||states == null || payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        Transaction tx = makeApproveMulti(states, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        boolean haspayer = false;
        for(Account account : owner){
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

    public Transaction makeApproveMulti(Oep8State[] states, String payerAcct, long gaslimit, long gasprice) throws Exception{
        if(states==null|| payerAcct==null ||gaslimit < 0 || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List paramList = new ArrayList<>();
        paramList.add("approveMulti".getBytes());
        List tempList = new ArrayList();
        for(Oep8State state : states){
            List list = new ArrayList();
            list.add(state.from);
            list.add(state.to);
            list.add(state.tokenId);
            list.add(state.value);
            tempList.add(list);
        }
        paramList.add(tempList);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payerAcct,gaslimit, gasprice);
        return tx;
    }

    public String sendTransferFromMulti(Account[] sender, TransferFrom[] states, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(sender == null || payerAcct == null || gaslimit<0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamError);
        }
        Transaction tx = makeTransferFromMulti(states, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        boolean haspayer = false;
        for(Account account : sender){
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

    public Transaction makeTransferFromMulti(TransferFrom[] states, String payerAcct, long gaslimit, long gasprice) throws Exception {
        if(states==null|| payerAcct==null ||gaslimit < 0
                || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List paramList = new ArrayList<>();
        paramList.add("transferFromMulti".getBytes());
        List tempList = new ArrayList();
        for(TransferFrom state : states){
            List list = new ArrayList();
            list.add(state.spender);
            list.add(state.from);
            list.add(state.to);
            list.add(state.tokenId);
            list.add(state.value);
            tempList.add(list);
        }
        paramList.add(tempList);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payerAcct,gaslimit, gasprice);
        return tx;
    }

    public String sendTransferFrom(Account sender, String from,String to, byte[] tokenId, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(sender == null || payerAcct == null || gaslimit<0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("transferFrom");
        func.name = "transferFrom";
        func.setParamsValue(sender.getAddressU160().toArray(), Address.decodeBase58(from).toArray(),Address.decodeBase58(to).toArray(), tokenId, amount);
        Object obj = sdk.neovm().sendTransaction(contractAddress,sender,payerAcct,gaslimit,gasprice,func, false);
        return (String) obj;
    }

    public Transaction makeTransferFrom(String sender, String from,String to, byte[] tokenId, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(sender==null||sender.equals("")||from == null || from.equals("")||to== null || to.equals("")|| amount <=0 || payerAcct==null ||gaslimit < 0
                || gasprice<0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("transferFrom");
        func.name = "transferFrom";
        func.setParamsValue(Address.decodeBase58(sender).toArray(), Address.decodeBase58(from).toArray(),Address.decodeBase58(to).toArray(), tokenId, amount);
        byte[] params = BuildParams.serializeAbiFunction(func);
        String payer = payerAcct.getAddressU160().toBase58();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(getContractAddress(), null, params, payer,gaslimit, gasprice);
        return tx;
    }

    public String sendCompound(Account account, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(account == null || payerAcct == null || gaslimit<0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("compound");
        func.setParamsValue(account.getAddressU160().toArray());
        Object obj = sdk.neovm().sendTransaction(contractAddress,account,payerAcct,gaslimit,gasprice,func, false);
        return (String) obj;
    }

    public String queryAllowance(String owner, String spender, byte[] tokenId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(owner == null||owner.equals("")||spender==null||spender.equals("")){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("allowance");
        func.name = "allowance";
        func.setParamsValue(Address.decodeBase58(owner).toArray(), Address.decodeBase58(spender).toArray(), tokenId);
        Object obj =  sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        String balance = ((JSONObject) obj).getString("Result");
        if(balance.equals("")){
            balance = "00";
        }
        return balance;
    }

    public long queryBalanceOf(String addr, byte[] tokenId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if(addr == null||addr.equals("")){
            throw new SDKException(ErrorCode.ParamError);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("balanceOf");
        func.name = "balanceOf";
        func.setParamsValue(Address.decodeBase58(addr).toArray(), tokenId);
        Object obj =  sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        String balance = ((JSONObject) obj).getString("Result");
        if(balance.equals("")){
            balance = "00";
        }
        return Long.parseLong(Helper.reverse(balance), 16);
    }

    public long queryTotalSupply(byte[] tokenId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("totalSupply");
        func.name = "totalSupply";
        func.setParamsValue(tokenId);
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return Long.parseLong(Helper.reverse(((JSONObject) obj).getString("Result")),16);
    }

    public String queryName(byte[] tokenId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("name");
        func.name = "name";
        func.setParamsValue(tokenId);
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return new String(Helper.hexToBytes(((JSONObject) obj).getString("Result")));
    }

    public Long queryDecimals() throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("decimals");
        func.name = "decimals";
        func.setParamsValue();
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return Long.valueOf(((JSONObject) obj).getString("Result"), 16);
    }

    public String querySymbol(byte[] tokenId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        AbiInfo abiinfo = JSON.parseObject(oep8abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction("symbol");
        func.name = "symbol";
        func.setParamsValue(tokenId);
        Object obj =   sdk.neovm().sendTransaction(contractAddress,null,null,0,0,func, true);
        return new String(Helper.hexToBytes(Helper.reverse(((JSONObject) obj).getString("Result"))));
    }
}
