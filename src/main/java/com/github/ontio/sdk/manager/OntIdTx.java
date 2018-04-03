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
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.claim.Claim;
import com.github.ontio.core.DataSignature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;
import java.util.*;

/**
 *
 */
public class OntIdTx {
    private OntSdk sdk;
    private String codeAddress = null;


    public OntIdTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return codeAddress;
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
        AccountInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password,sdk.keyType,sdk.curveParaSpec);
        String ontid = Common.didont + info.addressBase58;

        Transaction tx = makeRegister(info);
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.addressBase58);
        sdk.getWalletMgr().writeWallet();
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException("sendRawTransaction error");
        }
        System.out.println("hash:" + tx.hash().toString());
        return identity;
    }

    /**
     *
     * @param info
     * @return
     * @throws Exception
     */
    public Transaction makeRegister(AccountInfo info) throws Exception {

        String ontid = Common.didont + info.addressBase58;
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("RegIdWithPublicKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(ontid.getBytes());
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info);
        return tx;
    }
    /**
     * register
     *
     * @param password
     * @return
     * @throws Exception
     */
    public Identity sendRegister(String password) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        AccountInfo info = sdk.getWalletMgr().createIdentityInfo(password,sdk.keyType,sdk.curveParaSpec);
        String ontid = Common.didont + info.addressBase58;
        Transaction tx = makeRegister(info);
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.addressBase58);
        sdk.getWalletMgr().writeWallet();
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException("sendRawTransaction error");
        }
        System.out.println("hash:" + tx.hash().toString());
        return identity;
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
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        AccountInfo info = sdk.getWalletMgr().createIdentityInfo(password,sdk.keyType,sdk.curveParaSpec);
        String ontid = Common.didont + info.addressBase58;
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("RegIdWithAttributes".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(ontid.getBytes());
        tmp.add(pk);
        byte attriNum = (byte) attrsMap.size();
        byte[] allAttrsBys = new byte[]{};

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            String tmpVal = "";
            byte[] bs = null;
            String type = "Object";
            byte[] attrsBys = new byte[]{};
            if (val instanceof BigInteger) {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "Integer";
                tmpVal = String.valueOf((int) val);
            } else if (val instanceof byte[]) {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "ByteArray";
                tmpVal = new String((byte[]) val);
            } else if (val instanceof Boolean) {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "Boolean";
                tmpVal = String.valueOf((boolean) val);
            } else if (val instanceof Integer) {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "Integer";
                tmpVal = String.valueOf((int) val);
            } else if (val instanceof String) {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "String";
                tmpVal = (String) val;
            } else {
                bs = Helper.addBytes(new byte[]{(byte) e.getKey().getBytes().length}, e.getKey().getBytes());
                type = "Object";
                tmpVal = JSON.toJSONString(val);
            }

            bs = Helper.addBytes(bs, Helper.addBytes(new byte[]{(byte) type.length()}, type.getBytes()));
            byte[] valBys = JSON.toJSONString(tmpVal).getBytes();
            bs = Helper.addBytes(bs, Helper.addBytes(new byte[]{(byte) (valBys.length / 256), (byte) (valBys.length % 256)}, valBys));
            attrsBys = Helper.addBytes(attrsBys, bs);
            if (attrsBys.length / (256 * 256) > 0) {
                return null;
            }
            attrsBys = Helper.addBytes(new byte[]{(byte) (attrsBys.length / 256), (byte) (attrsBys.length % 256)}, attrsBys);
            allAttrsBys = Helper.addBytes(allAttrsBys, attrsBys);
        }

        tmp.add(Helper.addBytes(new byte[]{attriNum}, allAttrsBys));
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info);
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.addressBase58);
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
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        AccountInfo info = sdk.getWalletMgr().createIdentityInfo(password,sdk.keyType,sdk.curveParaSpec);
        String ontid = Common.didont + info.addressBase58;
        byte[] guardianDid = (Common.didont + guardianAddr).getBytes();
        List li = new ArrayList<Object>();
        li.add("CreateIdentityByGuardian".getBytes());
        li.add(ontid.getBytes());
        li.add(guardianDid);
        Transaction tx = makeInvokeTransaction(li,ontid,password);
        sdk.signTx(tx, ontid, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * add PubKey
     *
     * @param addr
     * @param password
     * @param newpubkey
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String addr, String password, String newpubkey) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("AddKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newpubkey));
        tmp.add(pk);
//        tmp.add(new byte[]{0});
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
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
     * @param addr
     * @param newpubkey
     * @param recoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String password, String addr, String newpubkey, String recoveryScriptHash) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec);
        byte[] did = (Common.didont + addr).getBytes();
        List list = new ArrayList<Object>();
        list.add("AddKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newpubkey));
        tmp.add(Helper.hexToBytes(recoveryScriptHash));
        tmp.add(new byte[]{1});
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
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
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        byte[] guardianDid = (Common.didont + guardianAddr).getBytes();
        List list = new ArrayList<Object>();
        list.add("AddKeyByGuardian".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newpubkey));
        tmp.add(guardianDid);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        //String txHex = sdk.getWalletMgr().signatureData(password, tx);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * remove PubKey
     *
     * @param addr
     * @param password
     * @param removepk
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String addr, String password, String removepk) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("RemoveKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(removepk));
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param addr
     * @param password
     * @param key
     * @param recoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String addr, String password, byte[] key, String recoveryScriptHash) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        List list = new ArrayList<Object>();
        list.add("RemoveKey".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(key);
        tmp.add(Helper.hexToBytes(recoveryScriptHash));
        tmp.add(1);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param addr
     * @param password
     * @param recoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendAddRecovery(String addr, String password, String recoveryScriptHash) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddRecovery".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(recoveryScriptHash));
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * change Recovery
     *
     * @param addr
     * @param password
     * @param newRecoveryScriptHash
     * @param oldRecoveryScriptHash
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String addr, String password, String newRecoveryScriptHash, String oldRecoveryScriptHash) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("ChangeRecovery".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(Helper.hexToBytes(newRecoveryScriptHash));
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        sdk.signTx(tx, addr, password);;
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
        Transaction tx = makeUpdateAttribute(ontid,password,path,type,value);
        sdk.signTx(tx,ontid,password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeUpdateAttribute(String ontid, String password, byte[] path, byte[] type, byte[] value) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        if (type.length >= 255 || path.length >= 255) {
            throw new SDKException("param error");
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddAttribute".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(path);
        tmp.add(type);
        tmp.add(value);
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        return tx;
    }
    /**
     * @param addr
     * @param password
     * @param attriList
     * @return
     * @throws Exception
     */
    public String sendUpdateAttributeArray(String addr, String password, List<Object> attriList) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        byte[] did = (Common.didont + addr).getBytes();
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddAttributeArray".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(pk);
        tmp.add(attriList);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    private int bytes2int(byte[] b)
    {
        int i = 0;
        int ret = 0;
        for (; i < b.length; i++) {
            ret = ret * 256;
            ret = ret + b[i];
        }
        return ret;
    }
    /**
     * @param ontid
     * @param obj
     * @return
     */
    private Map parseDdoData(String ontid, String obj) {
        byte[] bys = Helper.hexToBytes(obj);
        int elen = parse4bytes(bys,0);
        int offset = 4;
        if (elen == 0) {
            return new HashMap();
        }
        byte[] pubkeysData = new byte[elen];
        System.arraycopy(bys, offset, pubkeysData, 0, elen);
//        int pubkeysNum = pubkeysData[0];

        byte[] tmpb = new byte[4];
        System.arraycopy(bys,offset, tmpb, 0, 4);
        int pubkeysNum = bytes2int(tmpb);

        offset = 4;
        Map map = new HashMap();
        Map attriMap = new HashMap();
        List ownersList = new ArrayList();
        for (int i = 0; i < pubkeysNum; i++) {
            int len = parse4bytes(pubkeysData,offset);
            offset = offset + 4;
            byte[] data = new byte[len];
            System.arraycopy(pubkeysData, offset, data, 0, len);
            Map owner = new HashMap();
            owner.put("Type",sdk.keyType);
            owner.put("Value",Helper.toHexString(data));
            ownersList.add(owner);
            offset = offset + len;
        }
        map.put("Owners", ownersList);
        map.put("OntId", ontid);
        offset = 4 + elen;

        elen = parse4bytes(bys,offset);
        offset = offset + 4;
        int totalOffset = offset + elen;
        if (elen != 0) {
            byte[] attrisData = new byte[elen];
            System.arraycopy(bys, offset, attrisData, 0, elen);

//        int attrisNum = attrisData[0];
            System.arraycopy(bys,offset, tmpb, 0, 4);
            int attrisNum = bytes2int(tmpb);

            offset = 4;
            for (int i = 0; i < attrisNum; i++) {

                int dataLen = parse4bytes(attrisData,offset);
                offset = offset + 4;
                byte[] data = new byte[dataLen];
                System.arraycopy(attrisData, offset, data, 0, dataLen);
                offset = offset + dataLen;


                int index = 0;
                int len = parse4bytes(data,index);
                index = index + 4;
                byte[] key = new byte[len];
                System.arraycopy(data, index, key, 0, len);
                index = index + len;

                len = parse4bytes(data,index);
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
        //System.out.println(JSON.toJSONString(map));
        if(totalOffset < bys.length){
            elen = parse4bytes(bys,totalOffset);
            if(elen == 0){
                return map;
            }
            byte[] recoveryData = new byte[elen];
            offset = 4;
            System.arraycopy(bys, totalOffset + 4, recoveryData, 0, elen);
            System.out.println("recoveryData:"+Helper.toHexString(recoveryData));
            map.put("Recovery",Helper.toHexString(recoveryData));
        }
        return map;
    }
    private int parse4bytes(byte[] bs,int offset){
        return (bs[offset] & 0xFF) * 256 * 256 * 256 + (bs[offset + 1] & 0xFF) * 256 * 256 + (bs[offset + 2] & 0xFF) * 256 + (bs[offset + 3] & 0xFF);
    }

    /**
     * get DDO
     *
     * @param ontid
     * @return
     * @throws Exception
     */
    public String sendGetDDO(String ontid) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        List list = new ArrayList<Object>();
        list.add("GetDDO".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(ontid.getBytes());
        tmp.add(UUID.randomUUID().toString().getBytes());
        list.add(tmp);
        Fee[] fees = new Fee[0];
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeAddress));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, VmType.NEOVM.value(), fees);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        //System.out.println(obj);
        List listResult = (List) obj;
        //System.out.println(listResult);
        Map map = new HashMap();
        try {
            for (int i = 0; i < listResult.size(); i++) {
                map = parseDdoData(ontid, (String) listResult.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        if (key.length >= 255) {
            throw new SDKException("param error");
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("AddRecord".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(key);
        tmp.add(value);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
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
    public String createOntIdClaim(String password, String context, Map<String, Object> claimMap, Map metaData) throws Exception {
        Claim claim = null;
        Map map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> e : claimMap.entrySet()) {
            map.put(e.getKey(), e.getValue());
        }
        try {
            String sendDid = (String) metaData.get("Issuer");
            String receiverDid = (String) metaData.get("Subject");
            if (sendDid == null || receiverDid == null) {
                throw new SDKException("SendDid or receiverDid is null in metaData");
            }
            String[] sendDidStr = sendDid.split(":");
            String[] receiverDidStr = receiverDid.split(":");
            if (sendDidStr.length != 3 || receiverDidStr.length != 3) {
                throw new SDKException("Did error");
            }
            claim = new Claim(sdk.getWalletMgr().getSignatureScheme(), sdk.getWalletMgr().getAccount(sendDidStr[2], password,sdk.keyType,sdk.curveParaSpec), context, map, sendDid, receiverDid, metaData);
            return claim.getClaim();
        } catch (SDKException e) {
            throw new SDKException(e);
        }
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
            JSONObject obj = JSON.parseObject(claim);
            String issuerDid = obj.getJSONObject("Metadata").getString("Issuer");
            String[] str = issuerDid.split(":");
            if (str.length != 3) {
                throw new SDKException("Did error");
            }
            String issuerDdo = sendGetDDO(issuerDid);
            JSONArray owners = JSON.parseObject(issuerDdo).getJSONArray("Owners");
            if(owners == null){
                throw new SDKException("Not exist cliam issuer");
            }
            String pubkeyStr = owners.getJSONObject(0).getString("Value");
            String signature = obj.getJSONObject("Signature").getString("Value");
            obj.remove("Signature");
            sign = new DataSignature();
            byte[] data = JSON.toJSONString(obj).getBytes();
            return sign.verifySignature(new Account(false, Helper.hexToBytes(pubkeyStr)), data, Base64.getDecoder().decode(signature));
        } catch (Exception e) {
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
     * @param ontid
     * @param password
     * @param path
     * @return
     * @throws Exception
     */
    public String sendRemoveAttribute(String ontid, String password, byte[] path) throws Exception {
        Transaction tx = makeRemoveAttribute(ontid,password,path);
        sdk.signTx(tx,ontid,password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeRemoveAttribute(String ontid, String password, byte[] path) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        if (path.length >= 255) {
            throw new SDKException("param error");
        }
        byte[] did = (ontid).getBytes();
        String addr = ontid.replace(Common.didont, "");
        byte[] pk = Helper.hexToBytes(sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec).pubkey);
        List list = new ArrayList<Object>();
        list.add("RemoveAttribute".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(did);
        tmp.add(path);
        tmp.add(pk);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,addr,password);
        return tx;
    }

    public Transaction makeInvokeTransaction(List<Object> list,AccountInfo acctinfo) throws Exception {
        Fee[] fees = new Fee[1];
        fees[0] = new Fee(0, Address.addressFromPubKey(acctinfo.pubkey));
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeAddress));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, VmType.NEOVM.value(), fees);
        return tx;
    }
    public Transaction makeInvokeTransaction(List<Object> list,String addr, String password) throws Exception {
        addr = addr.replace(Common.didont, "");
        AccountInfo acctinfo = sdk.getWalletMgr().getAccountInfo(addr,password,sdk.keyType,sdk.curveParaSpec);
        Fee[] fees = new Fee[1];
        fees[0] = new Fee(0, Address.addressFromPubKey(acctinfo.pubkey));
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeAddress));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, VmType.NEOVM.value(), fees);
        return tx;
    }


}
