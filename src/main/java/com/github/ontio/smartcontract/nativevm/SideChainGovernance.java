package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.sidechaingovernance.*;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.utils;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SideChainGovernance {

    private OntSdk sdk;
    private final String contractAddress = "0000000000000000000000000000000000000008";
    private final String SIDE_CHAIN = "sideChain";
    private final String SIDE_CHAIN_NODE_INFO = "sideChainNodeInfo";

    public SideChainGovernance(OntSdk sdk){
        this.sdk = sdk;
    }


    public String getSideChain(long sideChainId) throws ConnectorException, IOException {

        byte[] sideChainBytes = SIDE_CHAIN.getBytes();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        bw.writeInt((int)sideChainId);
        byte[] sideChainIdBytes = baos.toByteArray();
        byte[] key = new byte[sideChainBytes.length + sideChainIdBytes.length];
        System.arraycopy(sideChainBytes,0,key,0,sideChainBytes.length);
        System.arraycopy(sideChainIdBytes,0,key,sideChainBytes.length,sideChainIdBytes.length);
        String sideChainStr = sdk.getConnect().getStorage(Helper.reverse(contractAddress), Helper.toHexString(key));
        if (sideChainStr == null || sideChainStr==""){
            return null;
        }
        SideChain sideChain = new SideChain();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(sideChainStr));
        BinaryReader reader = new BinaryReader(bais);
        sideChain.deserialize(reader);
        return sideChain.toJson();
    }
    public SideChainNodeInfo getSideChainNodeInfo(String sideChainId) throws ConnectorException, IOException {
        byte[] sideChainNodeInfoBytes = SIDE_CHAIN_NODE_INFO.getBytes();
        byte[] sideChainIdBytes = sideChainId.getBytes();
        byte[] key = new byte[sideChainNodeInfoBytes.length + sideChainIdBytes.length];
        System.arraycopy(sideChainNodeInfoBytes,0,key,0,sideChainNodeInfoBytes.length);
        System.arraycopy(sideChainIdBytes,0,key,sideChainNodeInfoBytes.length,sideChainIdBytes.length);
        String sideChainStr = sdk.getConnect().getStorage(Helper.reverse(contractAddress), Helper.toHexString(key));
        if (sideChainStr == null || sideChainStr==""){
            return null;
        }
        SideChainNodeInfo sideChainNodeInfo = new SideChainNodeInfo();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(sideChainStr));
        BinaryReader reader = new BinaryReader(bais);
        sideChainNodeInfo.deserialize(reader);
        return sideChainNodeInfo;
    }
    public String registerSideChain(Account account, RegisterSideChainParam param, Identity identity, String password, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.sideChainID, param.address,param.ratio, param.deposit, param.ongPool,param.genesisBlock, param.caller, param.keyNo);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"registerSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        sdk.addSign(tx, identity.ontid,password,identity.controls.get(0).getSalt());
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String approveSideChain(Account[] accounts,byte[][] allPubkeys,int M, long sideChainID, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(sideChainID);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"approveSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String rejectSideChain(Account[] accounts, byte[][] allPubkeys, int M, long sideChainID, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(sideChainID);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"rejectSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String quitSideChain(Account account, QuitSideChainParam param, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.sideChainID, param.address);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"quitSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String approveQuitSideChain(Account[] accounts, byte[][] allPubkeys, int M, QuitSideChainParam param, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.sideChainID, param.address);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"approveQuitSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String ongSwap(Account account, SwapParam param, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.sideChainId, param.address, param.ongXAccount);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"ongSwap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String ongxSwap(Account account, SwapParam[] params, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || params == null || params.length == 0|| payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(params.length);
        for(SwapParam param : params) {
            struct.add(param.sideChainId, param.address, param.ongXAccount);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"ongxSwap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String inflation(Account account, InflationParam param, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.sideChainId, param.address, param.depositAdd, param.ongPoolAdd);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inflation",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String approveInflation(Account[] accounts,byte[][] allPubkeys,int M, long sideChainId, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(sideChainId);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"approveInflation",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String rejectInflation(Account[] accounts, byte[][] allPubkeys, int M, long sideChainId, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(sideChainId);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"rejectInflation",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String setGlobalParams(Account[] accounts,byte[][] allPubkeys, int M, Address swapAddress, Account payer, long gaslimit, long gasprice) throws Exception {
        if(accounts == null || swapAddress == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(swapAddress);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"setGlobalParams",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String getSyncAddress() throws ConnectorException, IOException, IllegalAccessException, InstantiationException {
        Object obj = sdk.getSideChainConnectMgr().getStorage(Helper.reverse(contractAddress), Helper.toHexString("globalParams".getBytes()));
        if(obj == null) {
            return null;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
        BinaryReader reader = new BinaryReader(in);
        Address addr =  utils.readAddress(reader);
        return addr.toBase58();
    }
    public String registerNodeToSideChain(Account account, NodeToSideChainParams params, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || params == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(params.peerPubkey, params.address, params.sideChainId);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"registerNodeToSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String quitNodeToSideChain(Account account, NodeToSideChainParams params, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || params == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(params.peerPubkey, params.address, params.sideChainId);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"quitNodeToSideChain",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}
