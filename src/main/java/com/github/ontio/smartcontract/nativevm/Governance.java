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

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;

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

    public String registerSyncNode(String userAddr,String pwd,String peerPubkey,long initPos,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        byte[] params = new RegisterSyncNodeParam(peerPubkey,userAddr,initPos).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"registerSyncNode",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        if(!userAddr.equals(payer)){
            sdk.addSign(tx,payer,payerpwd);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String approveSyncNode(String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new ApproveCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"approveSyncNode",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String registerCandidate(String userAddr,String pwd,String peerPubkey,int initPos,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new RegisterCandidateParam(peerPubkey,Address.decodeBase58(userAddr),initPos).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"registerCandidate",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        if(!userAddr.equals(payer)){
            sdk.addSign(tx,payer,payerpwd);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String approveCandidate(String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new ApproveCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"approveCandidate",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String rejectCandidate(String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new RejectCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"rejectCandidate",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String voteForPeer(String userAddr,String pwd,String peerPubkey[],int[] posList,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(Address.decodeBase58(userAddr),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteForPeer",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String unVoteForPeer(String userAddr,String pwd,String peerPubkey[],int[] posList,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(Address.decodeBase58(userAddr),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"unVoteForPeer",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String withdraw(String userAddr,String pwd,String peerPubkey[],int[] withdrawList,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != withdrawList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],withdrawList[i]);
        }
        byte[] params = new WithdrawParam(Address.decodeBase58(userAddr),peerPubkey,withdrawList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"withdraw",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String commitDpos(String userAddr,String pwd,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"commitDpos",new byte[]{}, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String blackNode(String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new BlackNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"blackNode",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String whiteNode(String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new WhiteNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"whiteNode",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String quitNode(String userAddr,String pwd,String peerPubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new QuitNodeParam(peerPubkey,Address.decodeBase58(userAddr)).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"quitNode",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,userAddr,pwd);
        if(!userAddr.equals(payer)){
            sdk.addSign(tx,payer,payerpwd);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String voteCommitDpos(String userAddr,String pwd,long pos,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new VoteCommitDposParam(userAddr,pos).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteCommitDpos",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        if(!userAddr.equals(payer)){
            sdk.addSign(tx,payer,payerpwd);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String updateConfig(Configuration config,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = config.toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String updateGlobalParam(int candidateFee,int minInitStake,int candidateNum,int A,int B,int Yita,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        byte[] params = new GovernanceGlobalParam(candidateFee,minInitStake,candidateNum,A,B,Yita).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateGlobalParam",params, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String callSplit(String payer,String payerpwd,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",new byte[]{}, VmType.Native.value(),payer,gaslimit,gasprice);
        sdk.signTx(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
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
    public RegisterCandidateParam(String peerPubkey,Address address,int initPos){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.initPos = initPos;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeInt(initPos);
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
    public void deserialize(BinaryReader reader) throws IOException{};

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