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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.VmType;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.merkle.MerkleVerifier;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.claim.Claim;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;

import java.awt.color.CMMException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static java.awt.SystemColor.info;

public class OntId {
    private OntSdk sdk;
    private String contractAddress = "ff00000000000000000000000000000000000003";


    public OntId(OntSdk sdk) {
        this.sdk = sdk;
    }


    public String getContractAddress() {
        return contractAddress;
    }

    /**
     *
     * @param ident
     * @param password
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRegister(Identity ident, String password,String payer,String payerpwd,long gaslimit,long gasprice,boolean isPreExec) throws Exception {
        if(ident ==null || password ==null || password.equals("")||payer==null || payer.equals("")||payerpwd ==null || payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        Transaction tx = makeRegister(ident.ontid,password,payer,gaslimit,gasprice);
        Identity identity = sdk.getWalletMgr().addOntIdController(ident.ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        sdk.signTx(tx, ident.ontid, password);
        sdk.addSign(tx,payer,payerpwd);
        if(isPreExec){
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            String result = ((JSONObject) obj).getString("Result");
            if (Integer.parseInt(result) == 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
        }else{
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
        }
        return tx.hash().toHexString();
    }

    public String sendRegisterPreExec(Identity ident, String password,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        return sendRegister(ident, password, payer,payerpwd, gaslimit,gasprice, true);
    }
    public String sendRegister(Identity ident, String password,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        return sendRegister(ident, password, payer,payerpwd, gaslimit,gasprice, false);
    }
    /**
     *
     * @param ident
     * @param password
     * @return
     * @throws Exception
     */
    public long sendRegisterGetGasLimit(Identity ident, String password) throws Exception {
        if(ident ==null || password ==null || password.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        Transaction tx = makeRegister(ident.ontid,password,null,0,0);
        sdk.signTx(tx, ident.ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ident.ontid, info.encryptedPrikey, info.ontid);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String result = ((JSONObject) obj).getString("Result");
        if (Integer.parseInt(result) == 0) {
            throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
        }
        return ((JSONObject) obj).getLong("Gas");
    }

    /**
     *
     * @param ontid
     * @param password
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRegister(String ontid,String password,String payer,long gaslimit,long gasprice) throws Exception {
        if(password ==null || password.equals("")||payer==null || payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ontid,password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = BuildParams.buildParams(info.ontid,pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithPublicKey",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }


    /**
     *
     * @param ident
     * @param password
     * @param attributes
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */

    public String sendRegisterWithAttrs(Identity ident, String password,Attribute[] attributes,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ident ==null || password ==null || password.equals("")||payer==null || payer.equals("")||payerpwd ==null || payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        Transaction tx = makeRegisterWithAttrs(ontid,password,attributes,payer,gaslimit,gasprice);
        sdk.signTx(tx, ontid, password);
        sdk.addSign(tx,payer,payerpwd);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        return tx.hash().toHexString();
    }

    /**
     *
     * @param ontid
     * @param password
     * @param attributes
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRegisterWithAttrs(String ontid, String password, Attribute[] attributes, String payer, long gaslimit, long gasprice) throws Exception {
        if(password ==null || password.equals("")||payer==null || payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ontid, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = BuildParams.buildParams(ontid, pk, attributes);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithAttributes",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    private byte[] serializeAttributes(Attribute[] attributes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        bw.writeSerializableArray(attributes);
        return baos.toByteArray();
    }
    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetPublicKeys(String ontid) throws SDKException, ConnectorException, IOException {
        if(ontid == null || ontid.equals("")){
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getPublicKeys", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader br = new BinaryReader(bais);
        List pubKeyList = new ArrayList();
        while (true){
            try{
                Map publicKeyMap = new HashMap();
                publicKeyMap.put("PubKeyId",ontid + "#keys-" + String.valueOf(br.readInt()));
                byte[] pubKey = br.readVarBytes();
                publicKeyMap.put("Type",KeyType.fromLabel(pubKey[0]));
                publicKeyMap.put("Curve", Curve.fromLabel(pubKey[1]));
                publicKeyMap.put("Value",Helper.toHexString(pubKey));
                pubKeyList.add(publicKeyMap);
            }catch (Exception e){
                break;
            }
        }
        return JSON.toJSONString(pubKeyList);
    }

    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetKeyState(String ontid,int index) throws SDKException, ConnectorException, IOException {
        if(ontid ==null || ontid.equals("")||index <0){
            throw new SDKException(ErrorCode.ParamErr("parameter is wrong"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes(),index);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getKeyState", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return new String(Helper.hexToBytes(res));
    }

    public String sendGetAttributes(String ontid) throws SDKException, ConnectorException, IOException {
        if(ontid == null || ontid.equals("")){
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getAttributes", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            return res;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader br = new BinaryReader(bais);
        List attrsList = new ArrayList();
        while (true){
            try{
                Map attributeMap = new HashMap();
                attributeMap.put("Key", new String(br.readVarBytes()));
                attributeMap.put("Type",new String(br.readVarBytes()));
                attributeMap.put("Value",new String(br.readVarBytes()));
                attrsList.add(attributeMap);
            }catch (Exception e){
                break;
            }
        }

        return JSON.toJSONString(attrsList);
    }


    /**
     *
     * @param ontid
     * @param password
     * @param newpubkey
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String ontid, String password, String newpubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        return sendAddPubKey(ontid,null,password,newpubkey,payer,payerpwd,gaslimit,gasprice);
    }

    /**
     *
     * @param ontid
     * @param recoveryAddr
     * @param password
     * @param newpubkey
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String ontid,String recoveryAddr, String password, String newpubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||payer==null || payer.equals("")||payerpwd==null || payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr;
        if(recoveryAddr != null){
            addr = recoveryAddr.replace(Common.didont, "");
        }else{
            addr = ontid.replace(Common.didont,"");
        }
        Transaction tx = makeAddPubKey(ontid,recoveryAddr,password,newpubkey,payer,gaslimit,gasprice);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param newpubkey
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddPubKey(String ontid,String password,String newpubkey,String payer,long gaslimit,long gasprice) throws Exception {
        return makeAddPubKey(ontid,null,password,newpubkey,payer,gaslimit,gasprice);
    }

    /**
     *
     * @param ontid
     * @param recoveryAddr
     * @param password
     * @param newpubkey
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddPubKey(String ontid,String recoveryAddr,String password,String newpubkey,String payer,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||payer==null || payer.equals("")||newpubkey==null||newpubkey.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        byte[] parabytes;
        if(recoveryAddr == null){
            AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid,password);
            byte[] pk = Helper.hexToBytes(info.pubkey);
            parabytes = BuildParams.buildParams(ontid.getBytes(),Helper.hexToBytes(newpubkey),pk);
        }else{
            parabytes = BuildParams.buildParams(ontid,Helper.hexToBytes(newpubkey),Address.decodeBase58(recoveryAddr).toArray());
        }
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addKey",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }


    /**
     *
     * @param ontid
     * @param password
     * @param removePubkey
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, String password, String removePubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        return sendRemovePubKey(ontid,null,password,removePubkey,payer,payerpwd,gaslimit,gasprice);
    }

    /**
     *
     * @param ontid
     * @param recoveryAddr
     * @param password
     * @param removePubkey
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, String recoveryAddr,String password, String removePubkey,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||payer==null || payer.equals("")||removePubkey==null||removePubkey.equals("")
                ||payerpwd==null||payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemovePubKey(ontid,recoveryAddr,password,removePubkey,payer,gaslimit,gasprice);
        String addr;
        if(recoveryAddr == null){
            addr = ontid.replace(Common.didont, "");
        }else{
            addr = recoveryAddr.replace(Common.didont, "");
        }
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param removePubkey
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemovePubKey(String ontid, String password, String removePubkey,String payer,long gaslimit,long gasprice) throws Exception {
        return makeRemovePubKey(ontid,null,password,removePubkey,payer,gaslimit,gasprice);
    }

    /**
     *
     * @param ontid
     * @param recoveryAddr
     * @param password
     * @param removePubkey
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemovePubKey(String ontid,String recoveryAddr, String password, String removePubkey,String payer,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||payer==null || payer.equals("")||removePubkey==null||removePubkey.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes;
        if(recoveryAddr == null){
            AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid.replace(Common.didont,""), password);
            byte[] pk = Helper.hexToBytes(info.pubkey);
            parabytes= BuildParams.buildParams(ontid,Helper.hexToBytes(removePubkey),pk);
        }else{
            parabytes= BuildParams.buildParams(ontid,Helper.hexToBytes(removePubkey),Address.decodeBase58(recoveryAddr).toArray());
        }

        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"removeKey",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param recoveryAddr
     * @param payer
     * @param payerpwd
     * @param gasprice
     * @return
     * @throws Exception
     */

    public String sendAddRecovery(String ontid, String password, String recoveryAddr,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||payer==null || payer.equals("")||recoveryAddr==null||recoveryAddr.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeAddRecovery(ontid,password,recoveryAddr,payer,gaslimit,gasprice);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param recoveryAddr
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddRecovery(String ontid, String password, String recoveryAddr,String payer,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||payer==null || payer.equals("")||recoveryAddr ==null||recoveryAddr.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = BuildParams.buildParams(ontid,Address.decodeBase58(recoveryAddr),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addRecovery",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param newRecovery
     * @param oldRecovery
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeChangeRecovery(ontid,newRecovery,oldRecovery,password,gaslimit,gasprice);
        sdk.signTx(tx, oldRecovery, password);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param newRecovery
     * @param oldRecovery
     * @param password
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gaslimit,long gasprice) throws SDKException {
        if(ontid==null || ontid.equals("")||password ==null || password.equals("")||newRecovery==null||newRecovery.equals("")||oldRecovery==null||oldRecovery.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = BuildParams.buildParams(ontid,Address.decodeBase58(newRecovery),Address.decodeBase58(oldRecovery));
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"changeRecovery",parabytes, VmType.Native.value(), oldRecovery,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param newRecovery
     * @param oldRecovery
     * @param addresses
     * @param password
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    private String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery,String[] addresses, String[] password,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null || password.length==0||newRecovery==null||newRecovery.equals("")||oldRecovery==null||oldRecovery.equals("")||
                addresses==null||addresses.length==0){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if(addresses.length != password.length) {
            throw new SDKException(ErrorCode.ParamErr("address.length is not same with password.length"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        com.github.ontio.account.Account[] accounts = new com.github.ontio.account.Account[addresses.length];
        for(int i = 0; i< addresses.length; i++){
            accounts[i] = sdk.getWalletMgr().getAccount(addresses[i],password[i]);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"changeRecovery",parabytes, VmType.Native.value(), oldRecovery,gaslimit,gasprice);
        sdk.signTx(tx, new com.github.ontio.account.Account[][]{accounts});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param attributes
     * @param payer
     * @param payerpwd
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddAttributes(String ontid, String password, Attribute[] attributes,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null||attributes==null|| attributes.length ==0 ||payer==null||payer.equals("")||payerpwd==null||payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeAddAttributes(ontid,password,attributes,payer,gaslimit,gasprice);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param attributes
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddAttributes(String ontid, String password, Attribute[] attributes,String payer,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null||attributes==null|| attributes.length==0 ||payer==null||payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = BuildParams.buildParams(ontid,attributes,pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addAttributes",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param path
     * @param payer
     * @param payerpwd
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRemoveAttribute(String ontid,String password,String path,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null||payer==null||payer.equals("")||path==null||path.equals("")||payerpwd==null||payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeRemoveAttribute(ontid,password,path,payer,gaslimit,gasprice);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param path
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAttribute(String ontid,String password,String path,String payer,long gaslimit,long gasprice) throws Exception {
        if(ontid==null || ontid.equals("")||password ==null||payer==null||payer.equals("")||path==null||path.equals("")||payer==null||payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gasprice < 0 || gaslimit<0){
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes(),path.getBytes(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"removeAttribute",parabytes, VmType.Native.value(), addr,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param txhash
     * @return
     * @throws Exception
     */
    public Object getMerkleProof(String txhash) throws Exception {
        if(txhash==null||txhash.equals("")){
            throw new SDKException(ErrorCode.ParamErr("txhash should not be null"));
        }
        Map proof = new HashMap();
        Map map = new HashMap();
        int height = sdk.getConnect().getBlockHeightByTxHash(txhash);
        map.put("Type", "MerkleProof");
        map.put("TxnHash", txhash);
        map.put("BlockHeight", height);

        Map tmpProof = (Map) sdk.getConnect().getMerkleProof(txhash);
        UInt256 txroot = UInt256.parse((String) tmpProof.get("TransactionsRoot"));
        int blockHeight = (int) tmpProof.get("BlockHeight");
        UInt256 curBlockRoot = UInt256.parse((String) tmpProof.get("CurBlockRoot"));
        int curBlockHeight = (int) tmpProof.get("CurBlockHeight");
        List hashes = (List) tmpProof.get("TargetHashes");
        UInt256[] targetHashes = new UInt256[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            targetHashes[i] = UInt256.parse((String) hashes.get(i));
        }
        map.put("MerkleRoot", curBlockRoot.toHexString());
        map.put("Nodes", MerkleVerifier.getProof(txroot, blockHeight, targetHashes, curBlockHeight + 1));
        proof.put("Proof", map);
        return proof;
    }

    /**
     *
     * @param merkleProof
     * @return
     * @throws Exception
     */
    public boolean verifyMerkleProof(String merkleProof) throws Exception {
        if(merkleProof ==null||merkleProof.equals("")){
            throw new SDKException(ErrorCode.ParamErr("claim should not be null"));
        }
        try {
            JSONObject obj = JSON.parseObject(merkleProof);
            Map proof = (Map) obj.getJSONObject("Proof");
            String txhash = (String) proof.get("TxnHash");
            int blockHeight = (int) proof.get("BlockHeight");
            UInt256 merkleRoot = UInt256.parse((String) proof.get("MerkleRoot"));
            Block block = sdk.getConnect().getBlock(blockHeight);
            if (block.height != blockHeight) {
                throw new SDKException("blockHeight not match");
            }
            boolean containTx = false;
            for (int i = 0; i < block.transactions.length; i++) {
                if (block.transactions[i].hash().toHexString().equals(txhash)) {
                    containTx = true;
                }
            }
            if(!containTx){
                throw new SDKException(ErrorCode.OtherError("not contain this tx"));
            }
            UInt256 txsroot = block.transactionsRoot;

            List nodes = (List) proof.get("Nodes");
            return MerkleVerifier.Verify(txsroot,  nodes, merkleRoot);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDKException(e);
        }
    }

    /**
     *
     * @param signerOntid
     * @param password
     * @param context
     * @param claimMap
     * @param metaData
     * @param clmRevMap
     * @param expire
     * @return
     * @throws Exception
     */
    public String createOntIdClaim(String signerOntid, String password, String context, Map<String, Object> claimMap, Map metaData,Map clmRevMap,long expire) throws Exception {
        if(signerOntid==null|| signerOntid.equals("")||password==null||password.equals("")||context==null||context.equals("")||claimMap==null||metaData ==null||clmRevMap==null||expire<0){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(expire < System.currentTimeMillis()/1000){
            throw new SDKException(ErrorCode.ExpireErr);
        }
        Claim claim = null;
        try {
            String sendDid = (String) metaData.get("Issuer");
            String receiverDid = (String) metaData.get("Subject");
            if (sendDid == null || receiverDid == null) {
                throw new SDKException(ErrorCode.DidNull);
            }
            String issuerDdo = sendGetDDO(sendDid);
            JSONArray owners = JSON.parseObject(issuerDdo).getJSONArray("Owners");
            if (owners == null) {
                throw new SDKException(ErrorCode.NotExistCliamIssuer);
            }
            String pubkeyId = null;
            com.github.ontio.account.Account acct = sdk.getWalletMgr().getAccount(signerOntid, password);
            String pk = Helper.toHexString(acct.serializePublicKey());
            for (int i = 0; i < owners.size(); i++) {
                JSONObject obj = owners.getJSONObject(i);
                if (obj.getString("Value").equals(pk)) {
                    pubkeyId = obj.getString("PubKeyId");
                    break;
                }
            }
            if (pubkeyId == null) {
                throw new SDKException(ErrorCode.NotFoundPublicKeyId);
            }
            String[] receiverDidStr = receiverDid.split(":");
            if (receiverDidStr.length != 3) {
                throw new SDKException(ErrorCode.DidError);
            }
            claim = new Claim(sdk.getWalletMgr().getSignatureScheme(), acct, context, claimMap, metaData,clmRevMap,pubkeyId,expire);
            return claim.getClaimStr();
        } catch (SDKException e) {
            throw new SDKException(ErrorCode.CreateOntIdClaimErr);
        }
    }

    /**
     *
     * @param claim
     * @return
     * @throws Exception
     */
    public boolean verifyOntIdClaim(String claim) throws Exception {
        if(claim==null){
            throw new SDKException(ErrorCode.ParamErr("claim should not be null"));
        }
        DataSignature sign = null;
        try {

            String[] obj = claim.split("\\.");
            if (obj.length != 3) {
                throw new SDKException(ErrorCode.ParamError);
            }
            byte[] payloadBytes = Base64.getDecoder().decode(obj[1].getBytes());
            JSONObject payloadObj = JSON.parseObject(new String(payloadBytes));
            String issuerDid = payloadObj.getString("iss");
            String[] str = issuerDid.split(":");
            if (str.length != 3) {
                throw new SDKException(ErrorCode.DidError);
            }
            String issuerDdo = sendGetDDO(issuerDid);
            JSONArray owners = JSON.parseObject(issuerDdo).getJSONArray("Owners");
            if (owners == null) {
                throw new SDKException(ErrorCode.NotExistCliamIssuer);
            }
            byte[] signatureBytes = Base64.getDecoder().decode(obj[2]);
            byte[] headerBytes = Base64.getDecoder().decode(obj[0].getBytes());
            JSONObject header = JSON.parseObject(new String(headerBytes));
            String kid = header.getString("kid");
            String id = kid.split("#keys-")[1];
            String pubkeyStr = owners.getJSONObject(Integer.parseInt(id) - 1).getString("Value");
            sign = new DataSignature();
            byte[] data = (obj[0] + "." + obj[1]).getBytes();
            return sign.verifySignature(new Account(false, Helper.hexToBytes(pubkeyStr)), data, signatureBytes);
        } catch (Exception e) {
            throw new SDKException(ErrorCode.VerifyOntIdClaimErr);
        }
    }


    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetDDO(String ontid) throws SDKException, ConnectorException, IOException {
        if(ontid==null){
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = BuildParams.buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getDDO", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        Map map = parseDdoData2(ontid,res);
        if (map.size() == 0) {
            return "";
        }
        return JSON.toJSONString(map);
    }
    private Map parseDdoData2(String ontid, String obj) throws IOException {
        byte[] bys = Helper.hexToBytes(obj);

        ByteArrayInputStream bais = new ByteArrayInputStream(bys);
        BinaryReader br = new BinaryReader(bais);
        byte[] publickeyBytes;
        byte[] attributeBytes;
        byte[] recoveryBytes;
        try{
            publickeyBytes = br.readVarBytes();
        }catch (Exception e){
            publickeyBytes = new byte[]{};
        }
        try{
            attributeBytes = br.readVarBytes();
        }catch (Exception e){
            attributeBytes = new byte[]{};
        }
        try {
            recoveryBytes = br.readVarBytes();
        }catch (Exception e){
            recoveryBytes = new byte[]{};
        }
        List pubKeyList = new ArrayList();
        if(publickeyBytes.length != 0){
            ByteArrayInputStream bais1 = new ByteArrayInputStream(publickeyBytes);
            BinaryReader br1 = new BinaryReader(bais1);
            while (true) {
                try {
                    Map publicKeyMap = new HashMap();
                    publicKeyMap.put("PubKeyId",ontid + "#keys-" + String.valueOf(br1.readInt()));
                    byte[] pubKey = br1.readVarBytes();
                    publicKeyMap.put("Type",KeyType.fromLabel(pubKey[0]));
                    publicKeyMap.put("Curve",Curve.fromLabel(pubKey[1]));
                    publicKeyMap.put("Value",Helper.toHexString(pubKey));
                    pubKeyList.add(publicKeyMap);
                } catch (Exception e) {
                    break;
                }
            }
        }
        List attrsList = new ArrayList();
        if(attributeBytes.length != 0){
            ByteArrayInputStream bais2 = new ByteArrayInputStream(attributeBytes);
            BinaryReader br2 = new BinaryReader(bais2);
            while (true) {
                try {
                    Map<String, Object> attributeMap = new HashMap();
                    attributeMap.put("Key",new String(br2.readVarBytes()));
                    attributeMap.put("Type",new String(br2.readVarBytes()));
                    attributeMap.put("Value",new String(br2.readVarBytes()));
                    attrsList.add(attributeMap);
                } catch (Exception e) {
                    break;
                }
            }
        }

        Map map = new HashMap();
        map.put("Owners",pubKeyList);
        map.put("Attributes",attrsList);
        if(recoveryBytes.length != 0){
            map.put("Recovery", Address.parse(Helper.toHexString(recoveryBytes)).toBase58());
        }
        map.put("OntId",ontid);
        return map;
    }
}
