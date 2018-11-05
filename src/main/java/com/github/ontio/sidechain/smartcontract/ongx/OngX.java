package com.github.ontio.sidechain.smartcontract.ongx;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.util.ArrayList;
import java.util.List;

public class OngX {

    private OntSdk sdk;
    private final String ongXContract = "0000000000000000000000000000000000000002";

    public OngX(OntSdk sdk) {
        this.sdk = sdk;
    }
    public String getContractAddress() {
        return ongXContract;
    }


    public String sendTransfer(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        Transaction tx = sdk.nativevm().ong().makeTransfer(sendAcct.getAddressU160().toBase58(),recvAddr,amount,
                payerAcct.getAddressU160().toBase58(), gaslimit,gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if(!sendAcct.equals(payerAcct)){
            sdk.addSign(tx, payerAcct);
        }
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.toHexString();
    }

    public Transaction makeTransfer(String sendAddr, String recvAddr, long amount, String payer, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransfer(sendAddr,recvAddr,amount,payer,gaslimit,gasprice);
    }
    public Transaction makeTransfer(State[] states, String payer, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransfer(states,payer,gaslimit,gasprice);
    }

    public String sendApprove(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        Transaction tx = sdk.nativevm().ong().makeApprove(sendAcct.getAddressU160().toBase58(),recvAddr,amount,
                payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if(!sendAcct.equals(payerAcct)){
            sdk.addSign(tx, payerAcct);
        }
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }
    public Transaction makeApprove(String sendAddr,String recvAddr,long amount,String payer,long gaslimit,long gasprice) throws Exception {
        return sdk.nativevm().ong().makeApprove(sendAddr,recvAddr,amount,payer,gaslimit,gasprice);
    }

    public String sendTransferFrom(Account sendAcct, String fromAddr, String toAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        Transaction tx = sdk.nativevm().ong().makeTransferFrom(sendAcct.getAddressU160().toBase58(),fromAddr,toAddr,amount,
                payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, new Account[][]{{sendAcct}});
        if(!sendAcct.equals(payerAcct)){
            sdk.addSign(tx, payerAcct);
        }
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }

    public Transaction makeTransferFrom(String sendAddr, String fromAddr, String toAddr,long amount,String payer,long gaslimit,long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransferFrom(sendAddr,fromAddr,toAddr,amount,payer,gaslimit,gasprice);
    }

    public String queryName() throws Exception {
        Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "name", new byte[]{0}, (String)null, 0L, 0L);
        Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     * @return
     * @throws Exception
     */
    public String querySymbol() throws Exception {
        Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "symbol", new byte[]{0}, (String)null, 0L, 0L);
        Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     * @return
     * @throws Exception
     */
    public long queryDecimals() throws Exception {
        Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "decimals", new byte[]{0}, (String)null, 0L, 0L);
        Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return "".equals(res) ? 0L : Long.valueOf(Helper.reverse(res), 16);
    }

    public long queryTotalSupply() throws Exception {
        Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "totalSupply", new byte[]{0}, (String)null, 0L, 0L);
        Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return res != null && !res.equals("") ? Long.valueOf(Helper.reverse(res), 16) : 0L;
    }

    public String unboundOng(String address) throws Exception {
        if (address != null && !address.equals("")) {
            String unboundOngStr = sdk.getSideChainConnectMgr().getAllowance("ong", Address.parse("0000000000000000000000000000000000000001").toBase58(), address);
            long unboundOng = Long.parseLong(unboundOngStr);
            return unboundOngStr;
        } else {
            throw new SDKException(ErrorCode.OtherError("address should not be null"));
        }
    }
    public long queryBalanceOf(String address) throws Exception {
        if (address != null && !address.equals("")) {
            List list = new ArrayList();
            list.add(Address.decodeBase58(address));
            byte[] arg = NativeBuildParams.createCodeParamsScript(list);
            Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "balanceOf", arg, (String)null, 0L, 0L);
            Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
            String res = ((JSONObject)obj).getString("Result");
            return res != null && !res.equals("") ? Long.valueOf(Helper.reverse(res), 16) : 0L;
        } else {
            throw new SDKException(ErrorCode.OtherError("address should not be null"));
        }
    }

    public long queryAllowance(String fromAddr, String toAddr) throws Exception {
        if (fromAddr != null && !fromAddr.equals("") && toAddr != null && !toAddr.equals("")) {
            List list = new ArrayList();
            list.add((new Struct()).add(new Object[]{Address.decodeBase58(fromAddr), Address.decodeBase58(toAddr)}));
            byte[] arg = NativeBuildParams.createCodeParamsScript(list);
            Transaction tx = this.sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)), "allowance", arg, (String)null, 0L, 0L);
            Object obj = sdk.getSideChainConnectMgr().sendRawTransactionPreExec(tx.toHexString());
            String res = ((JSONObject)obj).getString("Result");
            return res != null && !res.equals("") ? Long.valueOf(Helper.reverse(res), 16) : 0L;
        } else {
            throw new SDKException(ErrorCode.OtherError("parameter should not be null"));
        }
    }

    public String ongxSetSyncAddr(Account[] accounts,byte[][] allPubkeys,int M,String address, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || accounts.length ==0 || address==null|| address.equals("")|| allPubkeys == null || allPubkeys.length < accounts.length
                ||payer == null || gaslimit < 0||gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Address.decodeBase58(address));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)),"setSyncAddr",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(int i=0;i<accounts.length;i++){
            sdk.addMultiSign(tx, M,allPubkeys, accounts[i]);
        }
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }

    public String ongxSetSyncAddr(Account account,String address, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || address==null|| address.equals("") ||payer == null || gaslimit < 0||gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Address.decodeBase58(address));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)),"setSyncAddr",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx, payer);
        }
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }

    public String ongSwap(Account account, Swap swap, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || swap == null|| swap.value <0 || payer == null || gaslimit < 0||gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(swap.address, swap.value);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)),"ongSwap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.addSign(tx, account);
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();

    }


    public String ongxSwap(Account account, Swap swap, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || swap == null|| swap.value <0 || payer == null || gaslimit < 0||gasprice < 0){
            throw new SDKException(ErrorCode.ParamError);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(swap.address, swap.value);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongXContract)),"ongxSwap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.addSign(tx, account);
        sdk.getSideChainConnectMgr().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }
}
