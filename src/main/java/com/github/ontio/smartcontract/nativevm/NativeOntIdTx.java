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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class NativeOntIdTx {
    private OntSdk sdk;
    private String contractAddress = "ff00000000000000000000000000000000000003";


    public NativeOntIdTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return contractAddress;
    }

    /**
     *
     * @param ident
     * @param password
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithPublicKey",parabytes, VmType.Native.value(), ident.ontid.replace(Common.didont,""),gas);
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        return identity;
    }

    /**
     *
     * @param ident
     * @param password
     * @param attrsMap
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password,Map<String, Object> attrsMap,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(), pk, serializeAttributes(attrsMap));
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"regIDWithAttributes",parabytes, VmType.Native.value(), ident.ontid.replace(Common.didont,""),gas);
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        return identity;
    }


    private byte[] serializeAttributes(Map<String, Object> attrsMap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            if (val instanceof BigInteger) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Integer".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof byte[]) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("ByteArray".getBytes());
                binaryWriter.writeVarBytes(new String((byte[]) val).getBytes());
            } else if (val instanceof Boolean) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Boolean".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((boolean) val).getBytes());
            } else if (val instanceof Integer) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Integer".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof String) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("String".getBytes());
                binaryWriter.writeVarBytes(((String) val).getBytes());
            } else {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Object".getBytes());
                binaryWriter.writeVarBytes(JSON.toJSONString(val).getBytes());
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
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
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
        System.out.println(tx.toHexString());
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        return new String(Helper.hexToBytes((String) obj));
    }

    public String sendGetAttributes(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress, "getAttributes", parabytes, VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
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
    public String sendAddPubKey(String ontid, String password, String newpubkey,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(newpubkey),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addKey",parabytes, VmType.Native.value(), addr,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
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
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, String password, String removePubkey,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(removePubkey),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"removeKey",parabytes, VmType.Native.value(), addr,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
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
     * @return
     * @throws Exception
     */
    public String sendAddRecovery(String ontid, String password, String recovery,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(recovery).toArray(),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addRecovery",parabytes, VmType.Native.value(), addr,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
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
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"changeRecovery",parabytes, VmType.Native.value(), oldRecovery,gas);
        sdk.signTx(tx, oldRecovery, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
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
     * @param addresses
     * @param password
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery,String[] addresses, String[] password,long gas) throws Exception {
        if(addresses.length != password.length) {
            throw new SDKException(ErrorCode.ParamError);
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
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendAddAttributes(String ontid, String password, Map<String, Object> attrsMap,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),serializeAttributes(attrsMap),pk);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"addAttributes",parabytes, VmType.Native.value(), addr,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
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
        int height = sdk.getConnectMgr().getBlockHeightByTxHash(txhash);
        map.put("Type", "MerkleProof");
        map.put("TxnHash", txhash);
        map.put("BlockHeight", height);

        Map tmpProof = (Map) sdk.getConnectMgr().getMerkleProof(txhash);
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
     * @param claim
     * @return
     * @throws Exception
     */
    public boolean verifyMerkleProof(String claim) throws Exception {
        try {
            JSONObject obj = JSON.parseObject(claim);
            Map proof = (Map) obj.getJSONObject("Proof");
            String txhash = (String) proof.get("TxnHash");
            int blockHeight = (int) proof.get("BlockHeight");
            UInt256 merkleRoot = UInt256.parse((String) proof.get("MerkleRoot"));
            Block block = sdk.getConnectMgr().getBlock(blockHeight);
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
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        Map map = parseDdoData2(ontid, (String) obj);
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
        map.put("Recovery", Helper.toHexString(recoveryBytes));
        map.put("OntId",ontid);
        return map;
    }
    private Map parseDdoData(String ontid, String obj) {
        byte[] bys = Helper.hexToBytes(obj);
        int elen = parse4bytes(bys, 0);
        int offset = 4;
        if (elen == 0) {
            return new HashMap();
        }
        byte[] pubkeysData = new byte[elen];
        System.arraycopy(bys, offset, pubkeysData, 0, elen);
//        int pubkeysNum = pubkeysData[0];

        byte[] tmpb = new byte[4];
        System.arraycopy(bys, offset, tmpb, 0, 4);
        int pubkeysNum = bytes2int(tmpb);

        offset = 4;
        Map map = new HashMap();
        Map attriMap = new HashMap();
        List ownersList = new ArrayList();
        for (int i = 0; i < pubkeysNum; i++) {
            int pubkeyIdLen = parse4bytes(pubkeysData, offset);
            offset = offset + 4;
            int pubkeyId = (int) pubkeysData[offset];
            offset = offset + pubkeyIdLen;
            int len = parse4bytes(pubkeysData, offset);
            offset = offset + 4;
            byte[] data = new byte[len];
            System.arraycopy(pubkeysData, offset, data, 0, len);
            Map owner = new HashMap();
            owner.put("PublicKeyId", ontid + "#keys-" + String.valueOf(pubkeyId));
            if(sdk.signatureScheme == SignatureScheme.SHA256WITHECDSA) {
                owner.put("Type", KeyType.ECDSA);
                owner.put("Curve", new Object[]{"P-256"}[0]);
            }
            owner.put("Value", Helper.toHexString(data));
            ownersList.add(owner);
            offset = offset + len;
        }
        map.put("Owners", ownersList);
        map.put("OntId", ontid);
        offset = 4 + elen;

        elen = parse4bytes(bys, offset);
        offset = offset + 4;
        int totalOffset = offset + elen;
        if (elen == 0) {
            map.put("Attributes", attriMap);
        }
        if (elen != 0) {
            byte[] attrisData = new byte[elen];
            System.arraycopy(bys, offset, attrisData, 0, elen);

//        int attrisNum = attrisData[0];
            System.arraycopy(bys, offset, tmpb, 0, 4);
            int attrisNum = bytes2int(tmpb);

            offset = 4;
            for (int i = 0; i < attrisNum; i++) {

                int dataLen = parse4bytes(attrisData, offset);
                offset = offset + 4;
                byte[] data = new byte[dataLen];
                System.arraycopy(attrisData, offset, data, 0, dataLen);
                offset = offset + dataLen;


                int index = 0;
                int len = parse4bytes(data, index);
                index = index + 4;
                byte[] key = new byte[len];
                System.arraycopy(data, index, key, 0, len);
                index = index + len;

                len = parse4bytes(data, index);
                index = index + 4;
                len = data[index];
                index++;
                byte[] type = new byte[len];
                System.arraycopy(data, index, type, 0, len);
                index = index + len;

                byte[] value = new byte[dataLen - index];
                System.arraycopy(data, index, value, 0, dataLen - index);

                Map tmp = new HashMap();
                tmp.put("Type", new String(type));
                tmp.put("Value", new String(value));
                attriMap.put(new String(key), tmp);
            }
            map.put("Attributes", attriMap);
        }
        if (totalOffset < bys.length) {
            elen = parse4bytes(bys, totalOffset);
            if (elen == 0) {
                return map;
            }
            byte[] recoveryData = new byte[elen];
            offset = 4;
            System.arraycopy(bys, totalOffset + 4, recoveryData, 0, elen);
            map.put("Recovery", Helper.toHexString(recoveryData));
        }
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
