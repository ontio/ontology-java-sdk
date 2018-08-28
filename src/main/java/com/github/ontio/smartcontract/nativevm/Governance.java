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
import com.github.ontio.common.*;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.governance.PeerPoolItem;
import com.github.ontio.core.governance.AuthorizeInfo;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Governance {
    private OntSdk sdk;
    private final String contractAddress = "0000000000000000000000000000000000000007";
    private final String AUTHORIZE_INFO_POOL = "authorizeInfoPool";
    public Governance(OntSdk sdk) {
        this.sdk = sdk;
    }
    public String getContractAddress() {
        return contractAddress;
    }
    /**
     *
     * @param account
     * @param peerPubkey
     * @param initPos
     * @param ontid
     * @param ontidpwd
     * @param keyNo
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String registerCandidate(Account account, String peerPubkey, long initPos, String ontid,String ontidpwd,byte[] salt,  long keyNo, Account payerAcct, long gaslimit, long gasprice) throws Exception{
//        byte[] params = new RegisterCandidateParam(peerPubkey,account.getAddressU160(),initPos,ontid.getBytes(),keyNo).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"registerCandidate",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey,account.getAddressU160(),initPos,ontid.getBytes(),keyNo));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"registerCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        sdk.addSign(tx,ontid,ontidpwd,salt);
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice) throws Exception{

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey,account.getAddressU160()));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"unRegisterCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String withdrawOng(Account account,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        List list = new ArrayList();
        list.add(new Struct().add(account.getAddressU160()));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"withdrawOng",args,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String withdrawFee(Account account, Account payerAcct,long gaslimit,long gasprice) throws Exception {
        List list = new ArrayList();
        list.add(new Struct().add(account.getAddressU160()));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"withdrawFee",args,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String updateSplitCurve(Account adminAcct,int[] Yi,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Yi.length);
        for (int yi : Yi){
            struct.add(yi);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"updateSplitCurve",args,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{adminAcct}});
        if(!adminAcct.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String ChangeAuthorization(Account account,String peerPubkey,String address, int ifAuthorize,Account payerAcct,long gaslimit,long gasprice) throws Exception {
        List list = new ArrayList();
        Struct struct = new Struct();
        list.add(struct.add(peerPubkey,Address.decodeBase58(address).toArray(),Long.valueOf(ifAuthorize)));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"updateSplitCurve",args,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param peerPubkey
     * @return
     * @throws ConnectorException
     * @throws IOException
     */
    public String getPeerInfo(String peerPubkey) throws ConnectorException, IOException {
        return getPeerPoolMap(peerPubkey);
    }

    /**
     * 
     * @return
     * @throws ConnectorException
     * @throws IOException
     */
    public String getPeerInfoAll() throws ConnectorException, IOException {
        return getPeerPoolMap(null);
    }
    /**
     *
     * @return
     * @throws ConnectorException
     * @throws IOException
     */
    private String getPeerPoolMap(String peerPubkey) throws ConnectorException, IOException {
        String view = sdk.getConnect().getStorage(Helper.reverse(contractAddress),Helper.toHexString("governanceView".getBytes()));
        GovernanceView governanceView = new GovernanceView();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(view));
        BinaryReader br = new BinaryReader(bais);
        governanceView.deserialize(br);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        bw.writeInt(governanceView.view);

        byte[] viewBytes = baos.toByteArray();
        byte[] peerPoolBytes = "peerPool".getBytes();
        byte[] keyBytes = new byte[peerPoolBytes.length + viewBytes.length];
        System.arraycopy(peerPoolBytes,0,keyBytes,0,peerPoolBytes.length);
        System.arraycopy(viewBytes,0,keyBytes,peerPoolBytes.length,viewBytes.length);
        String value = sdk.getConnect().getStorage(Helper.reverse(contractAddress),Helper.toHexString(keyBytes));
        ByteArrayInputStream bais2 = new ByteArrayInputStream(Helper.hexToBytes(value));
        BinaryReader reader = new BinaryReader(bais2);
        int length = reader.readInt();
        Map peerPoolMap = new HashMap<String,PeerPoolItem>();
        for(int i = 0;i < length;i++){
            PeerPoolItem item = new PeerPoolItem();
            item.deserialize(reader);
            peerPoolMap.put(item.peerPubkey,item.Json());
        }
        if(peerPubkey != null) {
            if(!peerPoolMap.containsKey(peerPubkey)) {
                return null;
            }
            return JSON.toJSONString(peerPoolMap.get(peerPubkey));
        }
        return JSON.toJSONString(peerPoolMap);
    }

    /**
     *
     * @param peerPubkey
     * @param addr
     * @return
     */
    public AuthorizeInfo getAuthorizeInfo(String peerPubkey,Address addr) {
        byte[] peerPubkeyPrefix = Helper.hexToBytes(peerPubkey);
        byte[] address = addr.toArray();
        byte[] authorizeInfoPool = AUTHORIZE_INFO_POOL.getBytes();
        byte[] key = new byte[authorizeInfoPool.length + peerPubkeyPrefix.length + address.length];
        System.arraycopy(authorizeInfoPool,0,key,0,authorizeInfoPool.length);
        System.arraycopy(peerPubkeyPrefix,0,key,authorizeInfoPool.length,peerPubkeyPrefix.length);
        System.arraycopy(address,0,key,authorizeInfoPool.length + peerPubkeyPrefix.length,address.length);
        String res = null;
        try {
            res = sdk.getConnect().getStorage(Helper.reverse(contractAddress),Helper.toHexString(key));
            if(res !=null && !res.equals("")){
                return Serializable.from(Helper.hexToBytes(res), AuthorizeInfo.class);
            }
        } catch (ConnectorException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String approveCandidate(Account adminAccount, String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new ApproveCandidateParam(peerPubkey).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"approveCandidate",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"approveCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{adminAccount}});
        if(!adminAccount.equals(payerAcct)) {
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param multiAddress
     * @param M
     * @param accounts
     * @param publicKeys
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String approveCandidate(Address multiAddress,int M, Account[] accounts,byte[][] publicKeys,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{

        byte[][] pks = new byte[accounts.length+publicKeys.length][];
        for(int i=0;i<accounts.length;i++){
            pks[i] = accounts[i].serializePublicKey();
        }
        for(int i = 0;i< publicKeys.length;i++){
            pks[i+accounts.length] = publicKeys[i];
        }
        if(!multiAddress.equals(Address.addressFromMultiPubKeys(M,pks))){
            throw new SDKException(ErrorCode.ParamErr("mutilAddress doesnot match accounts and publicKeys"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"approveCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        Sig[] sigs = new Sig[1];
        sigs[0] = new Sig();
        sigs[0].pubKeys = new byte[pks.length][];
        sigs[0].sigData = new byte[M][];
        sigs[0].M = M;
        for (int i = 0; i < pks.length; i++) {
            sigs[0].pubKeys[i] = pks[i];
        }
        for (int i = 0; i< sigs[0].M; i++) {
            byte[] signature = tx.sign(accounts[i], accounts[i].getSignatureScheme());
            sigs[0].sigData[i] = signature;
        }
        tx.sigs = sigs;
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String rejectCandidate(Account adminAccount,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new RejectCandidateParam(peerPubkey).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"rejectCandidate",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"rejectCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{adminAccount}});
        if (!adminAccount.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param multiAddress
     * @param M
     * @param accounts
     * @param publicKeys
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String rejectCandidate(Address multiAddress,int M,Account[] accounts,byte[][] publicKeys,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[][] pks = new byte[accounts.length + publicKeys.length][];
        for(int i=0; i < accounts.length; i++){
            pks[i] = accounts[i].serializePublicKey();
        }
        for(int i=0; i < publicKeys.length; i++){
            pks[i+accounts.length] = publicKeys[i];
        }
        if(!multiAddress.equals(Address.addressFromMultiPubKeys(M,pks))){
            throw new SDKException(ErrorCode.ParamErr("mutilAddress doesnot match accounts and publicKeys"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"rejectCandidate",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        Sig[] sigs = new Sig[1];
        sigs[0] = new Sig();
        sigs[0].pubKeys = new byte[pks.length][];
        sigs[0].sigData = new byte[M][];
        sigs[0].M = M;
        for (int i = 0; i < pks.length; i++) {
            sigs[0].pubKeys[i] = pks[i];
        }
        for (int i = 0; i< sigs[0].M; i++) {
            byte[] signature = tx.sign(accounts[i], accounts[i].getSignatureScheme());
            sigs[0].sigData[i] = signature;
        }
        tx.sigs = sigs;
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param posList
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String AuthorizeForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(account.getAddressU160());
        struct.add(peerPubkey.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(peerPubkey[i]);
        }
        struct.add(posList.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(posList[i]);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"authorizeForPeer",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param posList
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String unAuthorizeForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(account.getAddressU160());
        struct.add(peerPubkey.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(peerPubkey[i]);
        }
        struct.add(posList.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(posList[i]);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"unAuthorizeForPeer",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param withdrawList
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != withdrawList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],withdrawList[i]);
        }
//        byte[] params = new WithdrawParam(account.getAddressU160(),peerPubkey,withdrawList).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"withdraw",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(account.getAddressU160());
        struct.add(peerPubkey.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(peerPubkey[i]);
        }
        struct.add(withdrawList.length);
        for(int i =0; i< peerPubkey.length;i++){
            struct.add(withdrawList[i]);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"withdraw",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param adminAccount
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String commitDpos(Account adminAccount, Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"commitDpos",new byte[]{},payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"commitDpos",new byte[]{0},payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{adminAccount}});
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String commitDpos(Address multiAddress,int M,Account[] accounts,byte[][] publicKeys,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[][] pks = new byte[accounts.length + publicKeys.length][];
        for(int i=0; i < accounts.length; i++){
            pks[i] = accounts[i].serializePublicKey();
        }
        for(int i=0; i < publicKeys.length; i++){
            pks[i+accounts.length] = publicKeys[i];
        }
        if(!multiAddress.equals(Address.addressFromMultiPubKeys(M,pks))){
            throw new SDKException(ErrorCode.ParamErr("mutilAddress doesnot match accounts and publicKeys"));
        }
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"commitDpos",new byte[]{0},payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);

        Sig[] sigs = new Sig[1];
        sigs[0] = new Sig();
        sigs[0].pubKeys = new byte[pks.length][];
        sigs[0].sigData = new byte[M][];
        sigs[0].M = M;
        for (int i = 0; i < pks.length; i++) {
            sigs[0].pubKeys[i] = pks[i];
        }
        for (int i = 0; i< sigs[0].M; i++) {
            byte[] signature = tx.sign(accounts[i], accounts[i].getSignatureScheme());
            sigs[0].sigData[i] = signature;
        }
        tx.sigs = sigs;
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String blackNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new BlackNodeParam(peerPubkey).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"blackNode",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"blackNode",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String whiteNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new WhiteNodeParam(peerPubkey).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"whiteNode",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"whiteNode",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new QuitNodeParam(peerPubkey,account.getAddressU160()).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"quitNode",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(peerPubkey,account.getAddressU160()));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"quitNode",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }


    /**
     *
     * @param config
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String updateConfig(Configuration config,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = config.toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        List list = new ArrayList();
        list.add(new Struct().add(config.toArray()));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"updateConfig",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param candidateFee
     * @param minInitStake
     * @param candidateNum
     * @param A
     * @param B
     * @param Yita
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String updateGlobalParam(long candidateFee,long minInitStake,long candidateNum,long A,long B,long Yita,Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        byte[] params = new GovernanceGlobalParam(candidateFee,minInitStake,candidateNum,A,B,Yita).toArray();
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateGlobalParam",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        List list = new ArrayList();
        list.add(new Struct().add(candidateFee,minInitStake,candidateNum,A,B,Yita));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"updateGlobalParam",args,payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * 
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String callSplit(Account payerAcct,long gaslimit,long gasprice) throws Exception{
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",new byte[]{},payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"callSplit",new byte[]{},payerAcct.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}

class GovernanceView implements Serializable{
    int view;
    int height;
    UInt256 txhash;
    GovernanceView(){
    }
    GovernanceView(int view,int height,UInt256 txhash){
        this.view = view;
        this.height = height;
        this.txhash = txhash;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.view = reader.readInt();
        this.height = reader.readInt();
        try {
            this.txhash = reader.readSerializable(UInt256.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeInt(view);
        writer.writeInt(height);
        writer.writeSerializable(txhash);
    }
}
class RegisterSyncNodeParam implements Serializable {
    public String peerPubkey;
    public String address;
    public long initPos;
    public RegisterSyncNodeParam(String peerPubkey,String address,long initPos){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.initPos = initPos;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
        writer.writeVarString(address);
        writer.writeLong(initPos);
    }
}
class ApproveCandidateParam implements Serializable {
    public String peerPubkey;
    public ApproveCandidateParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
    }
}
class RejectCandidateParam implements Serializable {

    public String peerPubkey;
    RejectCandidateParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
    }
}
class RegisterCandidateParam implements Serializable {
    public String peerPubkey;
    public Address address;
    public long initPos;
    public byte[] caller;
    public long keyNo;
    public RegisterCandidateParam(String peerPubkey,Address address,long initPos,byte[] caller,long keyNo){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.initPos = initPos;
        this.caller = caller;
        this.keyNo = keyNo;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeVarInt(initPos);
        writer.writeVarBytes(caller);
        writer.writeLong(keyNo);
    }
}
class AuthorizeForPeerParam implements Serializable {
    public Address address;
    public String[] peerPubkeys;
    public long[] posList;
    public AuthorizeForPeerParam(Address address,String[] peerPubkeys,long[] posList){
        this.address = address;
        this.peerPubkeys = peerPubkeys;
        this.posList = posList;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException{};

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeSerializable(address);
        writer.writeVarInt(peerPubkeys.length);
        for(String peerPubkey: peerPubkeys){
            writer.writeVarString(peerPubkey);
        }
        writer.writeVarInt(posList.length);
        for(long pos: posList){
            writer.writeVarInt(pos);
        }
    }
}
class WithdrawParam implements Serializable {
    public Address address;
    public String[] peerPubkeys;
    public long[] withdrawList;
    public WithdrawParam(Address address,String[] peerPubkeys,long[] withdrawList){
        this.address = address;
        this.peerPubkeys = peerPubkeys;
        this.withdrawList = withdrawList;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException{
    };

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeSerializable(address);
        writer.writeVarInt(peerPubkeys.length);
        for(String peerPubkey : peerPubkeys){
            writer.writeVarString(peerPubkey);
        }
        writer.writeVarInt(withdrawList.length);
        for(long withdraw : withdrawList){
            writer.writeVarInt(withdraw);
        }
    }
}
class QuitNodeParam implements Serializable {
    public String peerPubkey;
    public Address address;
    public QuitNodeParam(String peerPubkey,Address address){
        this.peerPubkey = peerPubkey;
        this.address = address;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
    }
}
class BlackNodeParam implements Serializable {
    public String peerPubkey;
    public BlackNodeParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
    }
}
class WhiteNodeParam implements Serializable {
    public String peerPubkey;
    public WhiteNodeParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
    }
}
class AuthorizeCommitDposParam implements Serializable {
    public String address;
    public long pos;
    public AuthorizeCommitDposParam(String address,long pos){
        this.pos = pos;
        this.address = address;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(address);
        writer.writeVarString(String.valueOf(pos));
    }
}
class Configuration implements Serializable {
    public long N = 7;
    public long C = 2;
    public long K = 7;
    public long L = 112;
    public long blockMsgDelay = 10000;
    public long hashMsgDelay = 10000;
    public long peerHandshakeTimeout = 10;
    public long maxBlockChangeView = 1000;
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarInt(N);
        writer.writeVarInt(C);
        writer.writeVarInt(K);
        writer.writeVarInt(L);
        writer.writeVarInt(blockMsgDelay);
        writer.writeVarInt(hashMsgDelay);
        writer.writeVarInt(peerHandshakeTimeout);
        writer.writeVarInt(maxBlockChangeView);
    }
}
class GovernanceGlobalParam implements Serializable {
    public long candidateFee;
    public long minInitStake;
    public long candidateNum;
    public long A;
    public long B;
    public long Yita;
    GovernanceGlobalParam(long candidateFee,long minInitStake,long candidateNum,long A,long B,long Yita){
        this.candidateFee = candidateFee;
        this.minInitStake = minInitStake;
        this.candidateNum = candidateNum;
        this.A = A;
        this.B = B;
        this.Yita = Yita;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarInt(candidateFee);
        writer.writeVarInt(minInitStake);
        writer.writeVarInt(candidateNum);
        writer.writeVarInt(A);
        writer.writeVarInt(B);
        writer.writeVarInt(Yita);
    }
}
