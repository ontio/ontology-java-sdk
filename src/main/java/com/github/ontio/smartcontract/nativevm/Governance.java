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
import com.github.ontio.common.*;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/5/24
 */
public class Governance {
    private OntSdk sdk;
    private final String contractAddress = "ff00000000000000000000000000000000000007";
    public Governance(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String registerCandidate(Account account, String peerPubkey, int initPos, String ontid,String ontidpwd, long keyNo, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        byte[] params = new RegisterCandidateParam(peerPubkey,account.getAddressU160(),initPos,ontid.getBytes(),keyNo).toArray();
        System.out.println(peerPubkey);
        System.out.println(Helper.toHexString(params));
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"registerCandidate",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        sdk.addSign(tx,ontid,ontidpwd);
        if(!account.getAddressU160().toBase58().equals(payerAcct.getAddressU160().toBase58())){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String getPeerPoolMap() throws ConnectorException, IOException {
        String view = sdk.getConnect().getStorage(contractAddress,Helper.toHexString("governanceView".getBytes()));
        GovernanceView governanceView = new GovernanceView();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(view));
        BinaryReader br = new BinaryReader(bais);
        governanceView.deserialize(br);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        bw.writeInt(governanceView.view);

        byte[] t = baos.toByteArray();
        byte[] t1 = "peerPool".getBytes();
        byte[] t2 = new byte[t1.length + t.length];
        System.arraycopy(t1,0,t2,0,t1.length);
        System.arraycopy(t,0,t2,t1.length,t.length);
        String value = sdk.getConnect().getStorage(contractAddress,Helper.toHexString(t2));
        ByteArrayInputStream bais2 = new ByteArrayInputStream(Helper.hexToBytes(value));
        BinaryReader reader = new BinaryReader(bais2);
        int length = reader.readInt();
        Map map = new HashMap<String,PeerPoolItem>();
        for(int i = 0;i < length;i++){
            PeerPoolItem item = new PeerPoolItem();
            item.deserialize(reader);
            map.put(item.peerPubkey,item);
        }
        return JSONObject.toJSONString(map);
    }
    public String approveCandidate(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new ApproveCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"approveCandidate",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String rejectCandidate(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new RejectCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"rejectCandidate",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String voteForPeer(Account account,String peerPubkey[],int[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(account.getAddressU160(),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteForPeer",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String unVoteForPeer(Account account,String peerPubkey[],int[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(account.getAddressU160(),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"unVoteForPeer",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
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
    public String withdraw(Account account,String peerPubkey[],int[] withdrawList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != withdrawList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],withdrawList[i]);
        }
        byte[] params = new WithdrawParam(account.getAddressU160(),peerPubkey,withdrawList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"withdraw",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
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
    public String commitDpos(Account account,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"commitDpos",new byte[]{}, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String blackNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new BlackNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"blackNode",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String whiteNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new WhiteNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"whiteNode",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new QuitNodeParam(peerPubkey,account.getAddressU160()).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"quitNode",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
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
    public String voteCommitDpos(Account account,long pos,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new VoteCommitDposParam(account.getAddressU160().toBase58(),pos).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteCommitDpos",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
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
    public String updateConfig(Configuration config,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = config.toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String updateGlobalParam(int candidateFee,int minInitStake,int candidateNum,int A,int B,int Yita,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new GovernanceGlobalParam(candidateFee,minInitStake,candidateNum,A,B,Yita).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateGlobalParam",params, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String callSplit(Account payerAcct,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",new byte[]{}, VmType.Native.value(),payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}
class PeerPoolItem implements Serializable{
    int index;
    String peerPubkey;
    Address address;
    int status;
    long initPos;
    long totalPos;
    PeerPoolItem(){}
    PeerPoolItem(int index,String peerPubkey,Address address,int status,long initPos,long totalPos){
        this.index = index;
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.status = status;
        this.initPos = initPos;
        this.totalPos = totalPos;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.index = reader.readInt();
        this.peerPubkey = reader.readVarString();
        try {
            this.address = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.status = reader.readByte();
        this.initPos = reader.readLong();
        this.totalPos = reader.readLong();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeInt(index);
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeByte((byte)status);
        writer.writeLong(initPos);
        writer.writeLong(totalPos);
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
    public int initPos;
    public byte[] caller;
    public long keyNo;
    public RegisterCandidateParam(String peerPubkey,Address address,int initPos,byte[] caller,long keyNo){
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
        writer.writeInt(initPos);
        writer.writeVarBytes(caller);
        writer.writeLong(keyNo);
    }
}
class VoteForPeerParam implements Serializable {
    public Address address;
    public String[] peerPubkeys;
    public int[] posList;
    public VoteForPeerParam(Address address,String[] peerPubkeys,int[] posList){
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
        writer.writeInt(posList.length);
        for(int pos: posList){
            writer.writeInt(pos);
        }
    }
}
class WithdrawParam implements Serializable {
    public Address address;
    public String[] peerPubkeys;
    public int[] withdrawList;
    public WithdrawParam(Address address,String[] peerPubkeys,int[] withdrawList){
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
        writer.writeInt(peerPubkeys.length);
        for(String peerPubkey : peerPubkeys){
            writer.writeVarString(peerPubkey);
        }
        writer.writeInt(withdrawList.length);
        for(int withdraw : withdrawList){
            writer.writeInt(withdraw);
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
class VoteCommitDposParam implements Serializable {
    public String address;
    public long pos;
    public VoteCommitDposParam(String address,long pos){
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
    public int N = 7;
    public int C = 2;
    public int K = 7;
    public int L = 112;
    public int blockMsgDelay = 10000;
    public int hashMsgDelay = 10000;
    public int peerHandshakeTimeout = 10;
    public int maxBlockChangeView = 1000;
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeInt(N);
        writer.writeInt(C);
        writer.writeInt(K);
        writer.writeInt(L);
        writer.writeInt(blockMsgDelay);
        writer.writeInt(hashMsgDelay);
        writer.writeInt(peerHandshakeTimeout);
        writer.writeInt(maxBlockChangeView);
    }
}
class GovernanceGlobalParam implements Serializable {
    public int candidateFee;
    public int minInitStake;
    public int candidateNum;
    public int A;
    public int B;
    public int Yita;
    GovernanceGlobalParam(int candidateFee,int minInitStake,int candidateNum,int A,int B,int Yita){
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
        writer.writeInt(candidateFee);
        writer.writeInt(minInitStake);
        writer.writeInt(candidateNum);
        writer.writeInt(A);
        writer.writeInt(B);
        writer.writeInt(Yita);
    }
}