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

package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSONArray;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.block.Block;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.merkle.MerkleVerifier;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.claim.Claim;
import com.github.ontio.core.DataSignature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 *
 */
public class OntIdTx {
    private OntSdk sdk;
    private String contractAddress = null;


    public OntIdTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return contractAddress;
    }


    /**
     * register
     *
     * @param ident
     * @param password
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password) throws Exception {
        return sendRegister(ident, password, false);
    }


    public Identity sendRegisterPreExec(Identity ident, String password) throws Exception {
        return sendRegister(ident, password, true);
    }

    public Identity sendRegister(Identity ident, String password, boolean preExec) throws Exception {
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;

        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithPublicKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), ident.ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        if (preExec) {
            String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
            b = Integer.parseInt(result) > 0 ? true : false;
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTransactionPreExec);
            }
        } else {
            b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        }
        return identity;
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

    /**
     * register
     *
     * @param password
     * @return
     * @throws Exception
     */
    public Identity sendRegister(String password) throws Exception {
        return sendRegister("", password);
    }

    public Identity sendRegisterPreExec(String password) throws Exception {
        return sendRegister("", password, true);
    }

    public Identity sendRegister(String label, String password) throws Exception {
        return sendRegister(label, password, false);
    }

    public Identity sendRegisterPreExec(String label, String password) throws Exception {
        return sendRegister(label, password, true);
    }

    private Identity sendRegister(String label, String password, boolean preExec) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().createIdentityInfo(password);
        String ontid = info.ontid;

        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithPublicKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        identity.label = label;
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        if (preExec) {
            String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
            System.out.println(result);
            b = Integer.parseInt(result) > 0 ? true : false;
            if (!b) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
        } else {
            b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        }
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
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
    public Identity sendRegister(Identity ident, String password,Map<String, Object> attrsMap) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);

        byte attriNum = (byte) attrsMap.size();
        byte[] allAttrsBys = new byte[]{};

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            String tmpVal = "";
            byte[] bs = null;
            String type = "Object";
            byte[] attrsBys = new byte[]{};
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

        byte[] parabytes = buildParams(ontid.getBytes(), pk, byteArrayOutputStream.toByteArray());

        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithAttributes", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), ident.ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        return identity;
    }

    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetAttributes(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getAttributes", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
        BinaryReader br = new BinaryReader(bais);
        Map attributeMap = new HashMap();
        while (true){
            try{
                attributeMap.put("key", new String(br.readVarBytes()));
                attributeMap.put("type",new String(br.readVarBytes()));
                attributeMap.put("value",new String(br.readVarBytes()));
            }catch (Exception e){
                break;
            }
        }
        return JSON.toJSONString(attributeMap);
    }

    /**
     * register ontid
     *
     * @param password
     * @param attrsMap
     * @return
     * @throws Exception
     */
    public Identity sendRegister(String password, Map<String, Object> attrsMap) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().createIdentityInfo(password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);

        byte attriNum = (byte) attrsMap.size();
        byte[] allAttrsBys = new byte[]{};

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            String tmpVal = "";
            byte[] bs = null;
            String type = "Object";
            byte[] attrsBys = new byte[]{};
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

        byte[] parabytes = buildParams(ontid.getBytes(), pk, byteArrayOutputStream.toByteArray());

        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithAttributes", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), info.ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);

        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (!b) {
            return null;
        }
        return identity;
    }

    /**
     * @param guardianAddr
     * @param password
     * @return
     * @throws Exception
     */
    public String sendRegisterByGuardian(String guardianAddr, String password) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().createIdentityInfo(password);
        String ontid = info.ontid;
        byte[] guardianDid = (Common.didont + guardianAddr).getBytes();
        List li = new ArrayList<Object>();
        li.add("CreateIdentityByGuardian".getBytes());
        li.add(ontid.getBytes());
        li.add(guardianDid);
        Transaction tx = makeInvokeTransaction(li, ontid, password);
        sdk.signTx(tx, ontid, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendAddPubKey(String ontid, String password, String newpubkey) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(newpubkey),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * add PubKey
     *
     * @param password
     * @param ontid
     * @param newpubkey
     * @param recoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String password, String ontid, String newpubkey, String recoveryScriptHash) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] did = (Common.didont + addr).getBytes();
        List list = new ArrayList<Object>();
        list.add("AddKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newpubkey));
        tmp.add(Helper.hexToBytes(recoveryScriptHash));
        tmp.add(new byte[]{1});
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * Add Key By Guardian
     *
     * @param addr
     * @param password
     * @param newpubkey
     * @param guardianAddr
     * @return
     * @throws Exception
     */
    public String sendAddPubKeyByGuardian(String addr, String password, String newpubkey, String guardianAddr) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        addr = addr.replace(Common.didont, "");
        byte[] did = (Common.didont + addr).getBytes();
        byte[] guardianDid = (Common.didont + guardianAddr).getBytes();
        List list = new ArrayList<Object>();
        list.add("AddKeyByGuardian".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newpubkey));
        tmp.add(guardianDid);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        //String txHex = sdk.getWalletMgr().signatureData(password, tx);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendRemovePubKey(String ontid, String password, String removePubkey) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(removePubkey),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "removeKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
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
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getPublicKeys", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
        BinaryReader br = new BinaryReader(bais);
        Map publicKeyMap = new HashMap();
        while (true){
            try{
                publicKeyMap.put("index", br.readInt());
                publicKeyMap.put("publicKey",Helper.toHexString(br.readVarBytes()));
            }catch (Exception e){
                break;
            }
        }
        return JSON.toJSONString(publicKeyMap);
    }

    /**
     * @param ontid
     * @param password
     * @param key
     * @param recoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, String password, byte[] key, String recoveryScriptHash) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] did = (Common.didont + addr).getBytes();
        List list = new ArrayList<Object>();
        list.add("RemoveKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(key);
        tmp.add(Helper.hexToBytes(recoveryScriptHash));
        tmp.add(1);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
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
    public String sendAddRecovery(String ontid, String password, String recovery) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(recovery).toArray(),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
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
     * @param newRecovery
     * @param oldRecovery
     * @param password
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "changeRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), oldRecovery);
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
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery,String[] addresses, String[] password) throws Exception {
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
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "changeRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), oldRecovery);
        sdk.signTx(tx, new com.github.ontio.account.Account[][]{accounts});
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }


    /**
     * @param ontid
     * @param password
     * @param path
     * @param type
     * @param value
     * @return
     * @throws Exception
     */
    public String sendUpdateAttribute(String ontid, String password, byte[] path, byte[] type, byte[] value) throws Exception {
        return sendUpdateAttribute(ontid, password, path, type, value, false);
    }


    public String sendUpdateAttributePreExec(String ontid, String password, byte[] path, byte[] type, byte[] value) throws Exception {
        return sendUpdateAttribute(ontid, password, path, type, value, true);
    }

    private String sendUpdateAttribute(String ontid, String password, byte[] path, byte[] type, byte[] value, boolean preExec) throws Exception {
        Transaction tx = makeUpdateAttribute(ontid, password, path, type, value);
        sdk.signTx(tx, ontid, password);
        boolean b = false;
        if (preExec) {
            String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
            b = Integer.parseInt(result) > 0 ? true : false;
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTransactionPreExec);
            }
        } else {
            b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        }
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
     * @param type
     * @param value
     * @return
     * @throws Exception
     */
    public String sendAddAttribute(String ontid, String password, byte[] path,byte[] type, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),path,type,value,pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addAttribute", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
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
     * @param path
     * @param type
     * @param value
     * @return
     * @throws Exception
     */
    public String sendAddAttributeArray(String ontid, String password, byte[] path,byte[] type, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);

        byte[] parabytes = buildParams(ontid.getBytes(),path,type,value,pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addAttribute", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeUpdateAttribute(String ontid, String password, byte[] path, byte[] type, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (type.length >= 255 || path.length >= 255) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddAttribute".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(path);
        tmp.add(type);
        tmp.add(value);
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        return tx;
    }

    /**
     * @param ontid
     * @param password
     * @param attriList
     * @return
     * @throws Exception
     */
    public String sendUpdateAttributeArray(String ontid, String password, List<Object> attriList) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] did = (Common.didont + addr).getBytes();
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddAttributeArray".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(pk);
        tmp.add(attriList);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
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
        Map publicKeyMap = new HashMap();
        if(publickeyBytes.length != 0){
            ByteArrayInputStream bais1 = new ByteArrayInputStream(publickeyBytes);
            BinaryReader br1 = new BinaryReader(bais1);
            while (true) {
                try {
                    publicKeyMap.put("index",br1.readInt());
                    publicKeyMap.put("publicKey",Helper.toHexString(br1.readVarBytes()));
                } catch (Exception e) {
                    break;
                }
            }
        }
        Map<String, Object> attributeMap = new HashMap();
        if(attributeBytes.length != 0){
            ByteArrayInputStream bais2 = new ByteArrayInputStream(attributeBytes);
            BinaryReader br2 = new BinaryReader(bais2);
            while (true) {
                try {
                    attributeMap.put("key",new String(br2.readVarBytes()));
                    attributeMap.put("type",new String(br2.readVarBytes()));
                    attributeMap.put("value",new String(br2.readVarBytes()));
                } catch (Exception e) {
                    break;
                }
            }
        }

        Map map = new HashMap();
        map.put("publicKey",publicKeyMap);
        map.put("attributes",attributeMap);
        map.put("recovery", Helper.toHexString(recoveryBytes));
        return map;
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
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getDDO", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
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

    public String[] getPubKeys(String did) throws Exception {
        return new String[]{};
    }


    /**
     * @param ontid
     * @param password
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    private String sendAddRecord(String ontid, String password, byte[] key, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (key.length >= 255) {
            throw new SDKException("param error");
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddRecord".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(key);
        tmp.add(value);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * create OntId Claim
     *
     * @param password
     * @param context
     * @param claimMap
     * @param metaData
     * @return
     * @throws SDKException
     */
    public String createOntIdClaim(String signerOntid, String password, String context, Map<String, Object> claimMap, Map metaData) throws Exception {
        Claim claim = null;
        Map contentMap = sortMap(claimMap);

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
                    pubkeyId = obj.getString("PublicKeyId");
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
            metaData = sortMap(metaData);
            claim = new Claim(sdk.getWalletMgr().getSignatureScheme(), acct, context, contentMap, metaData, pubkeyId);
            return claim.getClaim();
        } catch (SDKException e) {
            throw new SDKException(ErrorCode.CreateOntIdClaimErr);
        }
    }

    /**
     *
     * @param signerOntid
     * @param password
     * @param context
     * @param claimMap
     * @param metaData
     * @return
     * @throws Exception
     */
    public String createOntIdClaim(String signerOntid, String password, String context, Map<String, Object> claimMap, Map metaData,Map clmRevMap,long expire) throws Exception {
        Claim claim = null;
        Map contentMap = sortMap(claimMap);

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
                    pubkeyId = obj.getString("PublicKeyId");
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
            metaData = sortMap(metaData);
            claim = new Claim(sdk.getWalletMgr().getSignatureScheme(), acct, context, claimMap, metaData,clmRevMap,pubkeyId,expire);
            return claim.getClaimStr();
        } catch (SDKException e) {
            throw new SDKException(ErrorCode.CreateOntIdClaimErr);
        }
    }

    public Map sortMap(Map<String, Object> claimMap) {
        Map<String, Object> contentMap = new HashMap();
        for (Map.Entry<String, Object> e : claimMap.entrySet()) {
            contentMap.put(e.getKey(), e.getValue());
        }
        for (Map.Entry<String, Object> e : contentMap.entrySet()) {
            if (e.getValue() instanceof Map) {
                Map m = (Map) e.getValue();
                e.setValue(sort(m));
            } else if (e.getValue() instanceof List) {
                List list = (List) e.getValue();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Map) {
                        list.set(i, sort((Map) list.get(i)));
                    }
                }
                e.setValue(list);
            }
        }
        return contentMap;
    }

    public Map sort(Map m) {
        Map mNew = new HashMap(16);
        Object[] key = m.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < key.length; i++) {
            mNew.put((String) key[i], (Object) m.get(key[i]));
        }
        return mNew;
    }

    /**
     * verify OntId Claim
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
            String issuerDid = payloadObj.getString("Iss");
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
            JSONObject signatureObj = JSON.parseObject(new String(signatureBytes));

            String signatureValue = signatureObj.getString("Value");
            String publicKeyId = signatureObj.getString("PublicKeyId");
            boolean verify = false;
            for (int i = 0; i < owners.size(); i++) {
                JSONObject o = owners.getJSONObject(i);
                if (o.getString("PublicKeyId").equals(publicKeyId)) {
                    verify = true;
                    break;
                }
            }
            if (!verify) {
                throw new SDKException(ErrorCode.PublicKeyIdErr);
            }
            String id = publicKeyId.split("#keys-")[1];
            String pubkeyStr = owners.getJSONObject(Integer.parseInt(id) - 1).getString("Value");
            sign = new DataSignature();
            byte[] data = (obj[0] + "." + obj[1]).getBytes();
            return sign.verifySignature(new Account(false, Helper.hexToBytes(pubkeyStr)), data, Base64.getDecoder().decode(signatureValue));
        } catch (Exception e) {
            throw new SDKException(ErrorCode.VerifyOntIdClaimErr);
        }
    }

    public Object getProof(String txhash) throws Exception {
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
     * verify Signature
     *
     * @param ontid
     * @param data
     * @param signature
     * @return
     * @throws Exception
     */
    public boolean verifySign(String ontid, byte[] data, byte[] signature) throws Exception {
        DataSignature sign = null;
        try {
            String issuerDdo = sendGetDDO(ontid);
            String pubkeyStr = JSON.parseObject(issuerDdo).getJSONArray("Owners").getJSONObject(0).getString("Value");
            sign = new DataSignature();
            return sign.verifySignature(new Account(false, Helper.hexToBytes(pubkeyStr)), data, signature);
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }


    /**
     * sendRemoveAttribute
     *
     * @param ontid
     * @param password
     * @param path
     * @return
     * @throws Exception
     */
    public String sendRemoveAttribute(String ontid, String password, byte[] path) throws Exception {
        Transaction tx = makeRemoveAttribute(ontid, password, path);
        sdk.signTx(tx, ontid, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeRemoveAttribute(String ontid, String password, byte[] path) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (path.length >= 255) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password).pubkey);
        List list = new ArrayList<Object>();
        list.add("RemoveAttribute".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(path);
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        return tx;
    }

    public Transaction makeInvokeTransaction(List<Object> list, IdentityInfo acctinfo) throws Exception {

        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);

        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, params, VmType.NEOVM.value(), Address.addressFromPubKey(acctinfo.pubkey).toBase58());
        return tx;
    }

    public Transaction makeInvokeTransaction(List<Object> list, String addr, String password) throws Exception {
        addr = addr.replace(Common.didont, "");
        AccountInfo acctinfo = sdk.getWalletMgr().getAccountInfo(addr, password);
        Fee[] fees = new Fee[1];
        fees[0] = new Fee(0, Address.addressFromPubKey(acctinfo.pubkey));
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, params, VmType.NEOVM.value(), fees);
        return tx;
    }


    public String sendGetPublicKeyId(String ontid, String password) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password).pubkey);
        List list = new ArrayList<Object>();
        list.add("GetPublicKeyId".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list, addr, password);
        sdk.signTx(tx, addr, password);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return (String) obj;
    }

    public String sendGetPublicKeyStatus(String ontid, int index) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes(),index);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getKeyState", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        System.out.println(tx.toHexString());
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        return new String(Helper.hexToBytes((String) obj));
    }

}
