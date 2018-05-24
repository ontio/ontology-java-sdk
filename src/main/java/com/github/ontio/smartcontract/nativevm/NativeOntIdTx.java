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
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
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

public class NativeOntIdTx {
    private OntSdk sdk;
    private String contractAddress = "ff00000000000000000000000000000000000003";


    public NativeOntIdTx(OntSdk sdk) {
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
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password,String payer,String payerpassword,long gas) throws Exception {
        return sendRegister(ident, password, payer,payerpassword, gas, false);
    }

    /**
     *
     * @param ident
     * @param password
     * @param payerpassword
     * @param payer
     * @param gas
     * @return
     * @throws Exception
     */
    public Identity sendRegisterPreExec(Identity ident, String password,String payerpassword,String payer, long gas) throws Exception {
        return sendRegister(ident, password, payer,payerpassword, gas, true);
    }

    private Identity sendRegister(Identity ident, String password, String payer,String payerpassword,long gas,boolean preExec) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        Transaction tx = makeRegister(ident.ontid,password,payer,gas);
        sdk.signTx(tx, ident.ontid, password);
        sdk.addSign(tx,payer,payerpassword);
        Identity identity = sdk.getWalletMgr().addOntIdController(ident.ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        if(preExec){
            Object obj = (String) sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            String result = ((JSONObject) obj).getString("Result");
            if (Integer.parseInt(result) == 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
        }else{
            b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
        }
        return identity;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param payer
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeRegister(String ontid,String password,String payer,long gas) throws Exception {
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ontid,password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(info.ontid.getBytes(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithPublicKey",parabytes, VmType.Native.value(), payer,gas);
        return tx;
    }



    /**
     *
     * @param ident
     * @param password
     * @param attrsMap
     * @param payer
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */

    public Identity sendRegisterWithAttrs(Identity ident, String password,Map<String, Object> attrsMap,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        Transaction tx = makeRegisterWithAttrs(ontid,password,attrsMap,payer,gas);
        sdk.signTx(tx, ontid, password);
        sdk.addSign(tx,payer,payerpassword);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        return identity;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param attrsMap
     * @param payer
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeRegisterWithAttrs(String ontid,String password,Map<String, Object> attrsMap,String payer,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ontid, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(), pk, serializeAttributes(attrsMap));
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithAttributes",parabytes, VmType.Native.value(), payer,gas);
        return tx;
    }

    private byte[] serializeAttributes(Map<String, Object> attrsMap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter writer = new BinaryWriter(byteArrayOutputStream);

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            if (val instanceof BigInteger) {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("Integer".getBytes());
                writer.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof byte[]) {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("ByteArray".getBytes());
                writer.writeVarBytes(new String((byte[]) val).getBytes());
            } else if (val instanceof Boolean) {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("Boolean".getBytes());
                writer.writeVarBytes(String.valueOf((boolean) val).getBytes());
            } else if (val instanceof Integer) {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("Integer".getBytes());
                writer.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof String) {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("String".getBytes());
                writer.writeVarBytes(((String) val).getBytes());
            } else {
                writer.writeVarBytes(e.getKey().getBytes());
                writer.writeVarBytes("Object".getBytes());
                writer.writeVarBytes(JSON.toJSONString(val).getBytes());
            }
        }
        return byteArrayOutputStream.toByteArray();
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
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getPublicKeys", parabytes, VmType.Native.value(), null,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            throw new SDKException(ErrorCode.ResultIsNull);
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
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes(),index);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getKeyState", parabytes, VmType.Native.value(), null,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        return new String(Helper.hexToBytes(res));
    }

    public String sendGetAttributes(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getAttributes", parabytes, VmType.Native.value(), null,0);
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
     * @return
     * @throws Exception
     */

    public String sendAddPubKey(String ontid, String password, String newpubkey,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeAddPubKey(ontid,password,newpubkey,payer,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpassword);
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
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeAddPubKey(String ontid,String password,String newpubkey,String payer,long gas) throws Exception {
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid.replace(Common.didont,""), password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(newpubkey),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addKey",parabytes, VmType.Native.value(), payer,gas);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param removePubkey
     * @param payer
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */

    public String sendRemovePubKey(String ontid, String password, String removePubkey,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeRemovePubKey(ontid,password,removePubkey,payer,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpassword);
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
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeRemovePubKey(String ontid, String password, String removePubkey,String payer,long gas) throws Exception {
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid.replace(Common.didont,""), password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(removePubkey),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"removeKey",parabytes, VmType.Native.value(), payer,gas);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param recovery
     * @param payer
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */

    public String sendAddRecovery(String ontid, String password, String recovery,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeAddRecovery(ontid,password,recovery,payer,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpassword);
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
     * @param recovery
     * @param payer
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeAddRecovery(String ontid, String password, String recovery,String payer,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(recovery).toArray(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addRecovery",parabytes, VmType.Native.value(), payer,gas);
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
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeChangeRecovery(ontid,newRecovery,oldRecovery,password,gas);
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
     * @param gas
     * @return
     * @throws SDKException
     */
    public Transaction makeChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gas) throws SDKException {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"changeRecovery",parabytes, VmType.Native.value(), oldRecovery,gas);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param newRecovery
     * @param oldRecovery
     * @param addresses
     * @param password
     * @return
     * @throws Exception
     */
    private String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery,String[] addresses, String[] password,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
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
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"changeRecovery",parabytes, VmType.Native.value(), oldRecovery,gas);
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
     * @param attrsMap
     * @param payer
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */
    public String sendAddAttributes(String ontid, String password, Map<String, Object> attrsMap,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeAddAttributes(ontid,password,attrsMap,payer,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpassword);
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
     * @param attrsMap
     * @param payer
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeAddAttributes(String ontid, String password, Map<String, Object> attrsMap,String payer,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),serializeAttributes(attrsMap),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addAttributes",parabytes, VmType.Native.value(), payer,gas);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param path
     * @param payer
     * @param payerpassword
     * @param gas
     * @return
     * @throws Exception
     */
    public String sendRemoveAttribute(String ontid,String password,String path,String payer,String payerpassword,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        Transaction tx = makeRemoveAttribute(ontid,password,path,payer,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payer,payerpassword);
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
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAttribute(String ontid,String password,String path,String payer,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),path.getBytes(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"removeAttribute",parabytes, VmType.Native.value(), addr,gas);
        return tx;
    }

    /**
     *
     * @param txhash
     * @return
     * @throws Exception
     */
    public Object getMerkleProof(String txhash) throws Exception {
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
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getDDO", parabytes, VmType.Native.value(), null,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (res.equals("")) {
            throw new SDKException(ErrorCode.ResultIsNull);
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

    private int parse4bytes(byte[] bs, int offset) {
        return (bs[offset] & 0xFF) * 256 * 256 * 256 + (bs[offset + 1] & 0xFF) * 256 * 256 + (bs[offset + 2] & 0xFF) * 256 + (bs[offset + 3] & 0xFF);
    }

    private int bytes2int(byte[] b) {
        int i = 0;
        int ret = 0;
        for (; i < b.length; i++) {
            ret = ret * 256;
            ret = ret + b[i];
        }
        return ret;
    }

    public byte[] buildParams(Object ...params) throws SDKException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        try {
            for (Object param : params) {
                if(param instanceof Integer){
                    binaryWriter.writeInt(((Integer) param).intValue());
                }else if(param instanceof byte[]){
                    binaryWriter.writeVarBytes((byte[])param);
                }else if(param instanceof String){
                    binaryWriter.writeVarString((String) param);
                }
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
