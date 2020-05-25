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
import com.github.ontio.core.block.Block;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.ontid.Group;
import com.github.ontio.core.ontid.Signer;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.merkle.MerkleVerifier;
import com.github.ontio.sdk.claim.Claim;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class OntId {
    private OntSdk sdk;
    private String contractAddress = "0000000000000000000000000000000000000003";


    public OntId(OntSdk sdk) {
        this.sdk = sdk;
    }


    public String getContractAddress() {
        return contractAddress;
    }

    /**
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @param isPreExec
     * @return
     * @throws Exception
     */
    private String sendRegister(String ontId, Account pk, Account payerAcct, long gaslimit, long gasprice, boolean isPreExec) throws Exception {
        if (ontId == null || ontId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        Transaction tx = makeRegister(ontId, pk.serializePublicKey(), payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        if (isPreExec) {
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            String result = ((JSONObject) obj).getString("Result");
            if (Integer.parseInt(result) == 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error: " + obj));
            }
        } else {
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
        }
        return tx.hash().toHexString();
    }

    public String sendRegisterPreExec(String ontId, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        return sendRegister(ontId, pk, payerAcct, gaslimit, gasprice, true);
    }

    public String sendRegister(String ontId, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        return sendRegister(ontId, pk, payerAcct, gaslimit, gasprice, false);
    }


    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRegister(String ontid, byte[] pubKey, String payer, long gaslimit, long gasprice) throws Exception {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, pubKey));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "regIDWithPublicKey", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRegisterIdWithController(String ontId, Account[] controllerSigners, Group controller, Signer[] signers, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontId == null || controllerSigners == null || ontId.equals("") || controller == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        Transaction tx = makeRegisterIdWithController(ontId, controller, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        for (Account con : controllerSigners) {
            sdk.addSign(tx, con);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public Transaction makeRegisterIdWithController(String ontId, Group controller, Signer[] signers, String payer, long gaslimit, long gasprice) throws SDKException, IOException {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter writer = new BinaryWriter(ms);
        controller.serialize(writer);
        list.add(new Struct().add(ontId, ms.toByteArray(), serializeSigners(signers)));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "regIDWithController", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRevokeId(String ontId, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontId == null || ontId.equals("") || pk == null || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        Transaction tx = makeRevokeId(ontId, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        sdk.addSign(tx, pk);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public Transaction makeRevokeId(String ontId, int index, String payer, long gaslimit, long gasprice) throws SDKException, IOException {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontId, index));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "revokeID", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendRevokeIdByController(String ontId, Signer[] signers, Account[] controllerSigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontId == null || ontId.equals("") || controllerSigners == null || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        Transaction tx = makeRevokeIdByController(ontId, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        for (Account con : controllerSigners) {
            sdk.addSign(tx, con);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public Transaction makeRevokeIdByController(String ontId, Signer[] signers, String payer, long gaslimit, long gasprice) throws SDKException, IOException {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontId, serializeSigners(signers)));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "revokeIDByController", args, payer, gaslimit, gasprice);
        return tx;
    }

    byte[] serializeSigners(Signer[] signers) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        BinaryWriter writer = new BinaryWriter(ms);
        BigInteger l = BigInteger.valueOf(signers.length);
        writer.writeVarBytes(Helper.BigIntToNeoBytes(l));
        for (Signer s : signers) {
            s.serialize(writer);
        }
        return ms.toByteArray();
    }


    public String sendRemoveController(String ontId, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontId == null || ontId.equals("") || pk == null || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        Transaction tx = makeRemoveController(ontId, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        sdk.addSign(tx, pk);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public Transaction makeRemoveController(String ontId, int index, String payer, long gaslimit, long gasprice) throws SDKException, IOException {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontId, index));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeController", args, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param attributes
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */

    public String sendRegisterWithAttrs(String ontId, Attribute[] attributes, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontId == null || ontId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRegisterWithAttrs(ontId, pk.serializePublicKey(), attributes, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    /**
     * @param ontid
     * @param attributes
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRegisterWithAttrs(String ontid, byte[] publickey, Attribute[] attributes, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        Struct struct = new Struct().add(ontid.getBytes(), publickey);
        struct.add(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            struct.add(attributes[i].key, attributes[i].valueType, attributes[i].value);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "regIDWithAttributes", args, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param ontid
     * @return
     * @throws Exception
     */
    public String sendGetPublicKeys(String ontid) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(ontid.getBytes());
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getPublicKeysJson", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return new String(Helper.hexToBytes(res));
    }

    /**
     * @param ontid
     * @return
     * @throws Exception
     */
    public String sendGetKeyState(String ontid, int index) throws Exception {
        if (ontid == null || ontid.equals("") || index < 0) {
            throw new SDKException(ErrorCode.ParamErr("parameter is wrong"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid.getBytes(), index));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getKeyState", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return new String(Helper.hexToBytes(res));
    }

    public String sendGetAttributes(String ontid) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }


        List list = new ArrayList();
        list.add(ontid.getBytes());
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getAttributes", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader br = new BinaryReader(bais);
        List attrsList = new ArrayList();
        while (true) {
            try {
                Map attributeMap = new HashMap();
                attributeMap.put("Key", new String(br.readVarBytes()));
                attributeMap.put("Type", new String(br.readVarBytes()));
                attributeMap.put("Value", new String(br.readVarBytes()));
                attrsList.add(attributeMap);
            } catch (Exception e) {
                break;
            }
        }

        return JSON.toJSONString(attrsList);
    }

    /**
     * @param ontid
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String ontid, Account pk, byte[] newPubKey, String controller, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddPubKey(ontid, newPubKey, pk.serializePublicKey(), controller, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddPubKey(String ontid, byte[] newPubKey, byte[] pubKey, String controller, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || newPubKey == null || newPubKey.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        if (controller != "") {
            list.add(new Struct().add(ontid.getBytes(), newPubKey, pubKey, controller.getBytes()));
        } else {
            list.add(new Struct().add(ontid.getBytes(), newPubKey, pubKey));
        }
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addKey", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddKeyByIndex(String ontid, Account pk, byte[] newPubKey, int index, String controller, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddKeyByIndex(ontid, newPubKey, index, controller, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddKeyByIndex(String ontid, byte[] newPubKey, int index, String controller, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || newPubKey == null || newPubKey.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid.getBytes(), newPubKey, index, controller));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addKeyByIndex", arg, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveKeyByIndex(String ontid, Account pk, byte[] removePubkey, int index, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveKeyByIndex(ontid, removePubkey, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveKeyByIndex(String ontid, byte[] removePubKey, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || removePubKey == null || removePubKey.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid.getBytes(), removePubKey, index));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeKeyByIndex", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddKeyByController(String ontid, byte[] pubkey, Signer[] signers, String controller, Account[] controllerSigner, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddKeyByController(ontid, pubkey, signers, controller, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account acc : controllerSigner) {
            sdk.addSign(tx, acc);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddKeyByController(String ontid, byte[] pubKey, Signer[] signers, String controller, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, pubKey, serializeSigners(signers), controller.getBytes()));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addKeyByController", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendRemoveKeyByController(String ontid, int pubkeyIndex, Signer[] signers, Account[] controllerSigner, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveKeyByController(ontid, pubkeyIndex, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account acc : controllerSigner) {
            sdk.addSign(tx, acc);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveKeyByController(String ontid, int pubKeyIndex, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, pubKeyIndex, serializeSigners(signers)));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeKeyByController", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddKeyByRecovery(String ontid, byte[] pubkey, Signer[] signers, String controller, Account[] recoverySigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddKeyByRecovery(ontid, pubkey, signers, controller, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account acc : recoverySigners) {
            sdk.addSign(tx, acc);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddKeyByRecovery(String ontid, byte[] pubKey, Signer[] signers, String controller, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, pubKey, serializeSigners(signers), controller.getBytes()));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addKeyByRecovery", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendRemoveKeyByRecovery(String ontid, int pubkeyIndex, Signer[] signers, Account[] recoverySigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveKeyByRecovery(ontid, pubkeyIndex, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account acc : recoverySigners) {
            sdk.addSign(tx, acc);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveKeyByRecovery(String ontid, int pubKeyIndex, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, pubKeyIndex, serializeSigners(signers)));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeKeyByRecovery", arg, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param ontid
     * @param removePubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, byte[] removePubkey, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemovePubKey(ontid, removePubkey, pk.serializePublicKey(), payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param removePubkey
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemovePubKey(String ontid, byte[] removePubkey, byte[] pubKey, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || removePubkey == null || removePubkey.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid.getBytes(), removePubkey, pubKey));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeKey", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendSetRecovery(String ontid, Group recovery, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeSetRecovery(ontid, recovery, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeSetRecovery(String ontid, Group recovery, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || recovery == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, recovery.toArray(), index));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "setRecovery", arg, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendUpdateRecovery(String ontid, Group newRecovery, Signer[] signers, Account[] recoverySigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeUpdateRecovery(ontid, newRecovery, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account r : recoverySigners) {
            sdk.addSign(tx, r);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeUpdateRecovery(String ontid, Group newRecovery, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || signers == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, newRecovery.toArray(), serializeSigners(signers)));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "updateRecovery", arg, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveRecovery(String ontid, int index, Account pkAcc, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveRecovery(ontid, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        sdk.addSign(tx, pkAcc);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveRecovery(String ontid, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, index));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeRecovery", arg, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param ontid
     * @param attributes
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddAttributes(String ontid, Attribute[] attributes, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddAttributes(ontid, attributes, pk.serializePublicKey(), payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param attributes
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddAttributes(String ontid, Attribute[] attributes, byte[] pk, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid);
        struct.add(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            struct.add(attributes[i].key, attributes[i].valueType, attributes[i].value);
        }
        struct.add(pk);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addAttributes", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddAttributesByIndex(String ontid, Attribute[] attributes, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddAttributesByIndex(ontid, attributes, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param attributes
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddAttributesByIndex(String ontid, Attribute[] attributes, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid);
        struct.add(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            struct.add(attributes[i].key, attributes[i].valueType, attributes[i].value);
        }
        struct.add(index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addAttributesByIndex", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveAttributeByIndex(String ontid, byte[] removeKey, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAttributeByIndex(ontid, removeKey, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAttributeByIndex(String ontid, byte[] removeKey, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, removeKey, index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAttributeByIndex", args, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param ontid
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRemoveAttribute(String ontid, byte[] removeKey, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAttribute(ontid, removeKey, pk.serializePublicKey(), payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAttribute(String ontid, byte[] removeKey, byte[] pk, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(new Struct().add(ontid.getBytes(), removeKey, pk));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAttribute", arg, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendAddAttributesByController(String ontid, Attribute[] attributes, Signer[] signers, Account[] controllerSigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddAttributesByController(ontid, attributes, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        for (Account acc : controllerSigners) {
            sdk.addSign(tx, acc);
        }
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param attributes
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddAttributesByController(String ontid, Attribute[] attributes, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || attributes == null || attributes.length == 0 || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid);
        struct.add(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            struct.add(attributes[i].key, attributes[i].valueType, attributes[i].value);
        }
        struct.add(serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addAttributesByController", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveAttributesByController(String ontid, byte[] key, Signer[] signers, Account[] controllerSigners, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAttributesByController(ontid, key, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, payerAcct);
        for (Account acc : controllerSigners) {
            sdk.addSign(tx, acc);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAttributesByController(String ontid, byte[] key, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, key, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAttributeByController", args, payer, gaslimit, gasprice);
        return tx;
    }


    /**
     * @param ontid
     * @param pubKey
     * @param controller
     * @param signIndex
     * @param pk
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendAddNewAuthKey(String ontid, byte[] pubKey, String controller, int signIndex, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddNewAuthKey(ontid, pubKey, controller, signIndex, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddNewAuthKey(String ontid, byte[] pubKey, String controller, int signIndex, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct1 = new Struct().add(pubKey, controller.getBytes());
        Struct struct = new Struct().add(ontid, struct1, signIndex);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addNewAuthKey", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendRemoveAuthKey(String ontid, int index, int signedIndex, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAuthKey(ontid, index, signedIndex, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAuthKey(String ontid, int index, int signIndex, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, index, signIndex);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAuthKey", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendRemoveAuthKeyByRecovery(String ontid, int index, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAuthKeyByRecovery(ontid, index, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAuthKeyByRecovery(String ontid, int index, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, index, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAuthKeyByRecovery", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveAuthKeyByController(String ontid, int index, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveAuthKeyByController(ontid, index, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveAuthKeyByController(String ontid, int index, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, index, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeAuthKeyByController", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendAddNewAuthKeyByRecovery(String ontid, byte[] pubKey, String controller, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddNewAuthKeyByRecovery(ontid, pubKey, controller, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddNewAuthKeyByRecovery(String ontid, byte[] pubKey, String controller, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct1 = new Struct().add(pubKey, controller.getBytes());
        Struct struct = new Struct().add(ontid, struct1, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addNewAuthKeyByRecovery", args, payer, gaslimit, gasprice);
        return tx;
    }


    /**
     * @param ontid
     * @param index
     * @param signers
     * @param pk        recovery account
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendSetAuthKeyByRecovery(String ontid, int index, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeSetAuthKeyByRecovery(ontid, index, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeSetAuthKeyByRecovery(String ontid, int index, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, index, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "setAuthKeyByRecovery", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddNewAuthKeyByController(String ontid, byte[] pubKey, String controller, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddNewAuthKeyByController(ontid, pubKey, controller, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddNewAuthKeyByController(String ontid, byte[] pubKey, String controller, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct1 = new Struct().add(pubKey, controller.getBytes());
        Struct struct = new Struct().add(ontid, struct1, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addNewAuthKeyByController", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendSetAuthKeyByController(String ontid, int index, Signer[] signers, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeSetAuthKeyByController(ontid, index, signers, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeSetAuthKeyByController(String ontid, int index, Signer[] signers, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, index, serializeSigners(signers));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "setAuthKeyByController", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendAddService(String ontid, byte[] serviceId, byte[] type, byte[] serviceEndpint, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddService(ontid, serviceId, type, serviceEndpint, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddService(String ontid, byte[] serviceId, byte[] type, byte[] serviceEndpint, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, serviceId, type, serviceEndpint, index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addService", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendUpdateService(String ontid, byte[] serviceId, byte[] type, byte[] serviceEndpint, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeUpdateService(ontid, serviceId, type, serviceEndpint, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeUpdateService(String ontid, byte[] serviceId, byte[] type, byte[] serviceEndpint, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, serviceId, type, serviceEndpint, index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "updateService", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveService(String ontid, byte[] serviceId, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveService(ontid, serviceId, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveService(String ontid, byte[] serviceId, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid, serviceId, index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeService", args, payer, gaslimit, gasprice);
        return tx;
    }


    public String sendAddContext(String ontid, byte[][] contexts, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeAddContext(ontid, contexts, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeAddContext(String ontid, byte[][] contexts, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid.getBytes());
        struct.add(contexts.length);
        for (int i = 0; i < contexts.length; i++) {
            struct.add(contexts[i]);
        }
        struct.add(index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "addContext", args, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemoveContext(String ontid, byte[][] contexts, int index, Account pk, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemoveContext(ontid, contexts, index, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.addSign(tx, pk);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param ontid
     * @param payer
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeRemoveContext(String ontid, byte[][] contexts, int index, String payer, long gaslimit, long gasprice) throws Exception {
        if (ontid == null || ontid.equals("") || payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        Struct struct = new Struct().add(ontid.getBytes());
        struct.add(contexts.length);
        for (int i = 0; i < contexts.length; i++) {
            struct.add(contexts[i]);
        }
        struct.add(index);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "removeContext", args, payer, gaslimit, gasprice);
        return tx;
    }

    //TODO
    public String sendVerifySignature(String ontid, int keyIndex, Account acc) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        List list = new ArrayList();
        list.add(new Struct().add(ontid, keyIndex));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "verifySignature", arg, null, 0, 0);

        sdk.addSign(tx, acc);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return res;
    }

    //TODO
    public String sendVerifyController(String ontid, Signer[] signers, Account[] accounts) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(new Struct().add(ontid, serializeSigners(signers)));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "verifyController", arg, null, 0, 0);

        for (Account acc : accounts) {
            sdk.addSign(tx, acc);
        }

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return res;
    }


    //TODO parse result
    public String sendGetService(String ontid, byte[] serviceId) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(new Struct().add(ontid, serviceId));
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getServiceJson", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return res;
    }


    //TODO parse result
    public String sendGetController(String ontid) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(ontid);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getControllerJson", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return new String(Helper.hexToBytes(res));
    }


    //TODO parse result
    public String sendGetDocument(String ontid) throws Exception {
        if (ontid == null || ontid.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(ontid);
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getDocumentJson", arg, null, 0, 0);

        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        return new String(Helper.hexToBytes(res));
    }


    /**
     * @param txhash
     * @return
     * @throws Exception
     */
    public Object getMerkleProof(String txhash) throws Exception {
        if (txhash == null || txhash.equals("")) {
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
     * @param merkleProof
     * @return
     * @throws Exception
     */
    public boolean verifyMerkleProof(String merkleProof) throws Exception {
        if (merkleProof == null || merkleProof.equals("")) {
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
            if (!containTx) {
                throw new SDKException(ErrorCode.OtherError("not contain this tx"));
            }
            UInt256 txsroot = block.transactionsRoot;

            List nodes = (List) proof.get("Nodes");
            return MerkleVerifier.Verify(txsroot, nodes, merkleRoot);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDKException(e);
        }
    }

    /**
     * @param signerOntid
     * @param context
     * @param claimMap
     * @param metaData
     * @param clmRevMap
     * @param expire
     * @return
     * @throws Exception
     */
    public String createOntIdClaim(String signerOntid, Account pkAcc, String context, Map<String, Object> claimMap, Map metaData, Map clmRevMap, long expire) throws Exception {
        if (signerOntid == null || signerOntid.equals("") || pkAcc == null || context == null || context.equals("") || claimMap == null || metaData == null || clmRevMap == null || expire <= 0) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (expire < System.currentTimeMillis() / 1000) {
            throw new SDKException(ErrorCode.ExpireErr);
        }
        Claim claim = null;
        try {
            String sendDid = (String) metaData.get("Issuer");
            String receiverDid = (String) metaData.get("Subject");
            if (sendDid == null || receiverDid == null) {
                throw new SDKException(ErrorCode.DidNull);
            }
            String issuerDdo = sendGetDocument(sendDid);
            JSONArray owners = JSON.parseObject(issuerDdo).getJSONArray("publicKey");
            if (owners == null) {
                throw new SDKException(ErrorCode.NotExistCliamIssuer);
            }
            String pubkeyId = null;
            String pk = Helper.toHexString(pkAcc.serializePublicKey());
            for (int i = 0; i < owners.size(); i++) {
                JSONObject obj = owners.getJSONObject(i);
                if (obj.getString("publicKeyHex").equals(pk)) {
                    pubkeyId = obj.getString("id");
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
            claim = new Claim(sdk.getWalletMgr().getSignatureScheme(), pkAcc, context, claimMap, metaData, clmRevMap, pubkeyId, expire);
            return claim.getClaimStr();
        } catch (SDKException e) {
            throw new SDKException(ErrorCode.CreateOntIdClaimErr);
        }
    }

    /**
     * @param claim
     * @return
     * @throws Exception
     */
    public boolean verifyOntIdClaim(String claim) throws Exception {
        if (claim == null) {
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
            String issuerDdo = sendGetDocument(issuerDid);
            JSONArray owners = JSON.parseObject(issuerDdo).getJSONArray("publicKey");
            if (owners == null) {
                throw new SDKException(ErrorCode.NotExistCliamIssuer);
            }
            byte[] signatureBytes = Base64.getDecoder().decode(obj[2]);
            byte[] headerBytes = Base64.getDecoder().decode(obj[0].getBytes());
            JSONObject header = JSON.parseObject(new String(headerBytes));
            String kid = header.getString("kid");
            String id = kid.split("#keys-")[1];
            String pubkeyStr = owners.getJSONObject(Integer.parseInt(id) - 1).getString("publicKeyHex");
            sign = new DataSignature();
            byte[] data = (obj[0] + "." + obj[1]).getBytes();
            return sign.verifySignature(new Account(false, Helper.hexToBytes(pubkeyStr)), data, signatureBytes);
        } catch (Exception e) {
            throw new SDKException(ErrorCode.VerifyOntIdClaimErr);
        }
    }


    /**
     * @param ontid
     * @return
     * @throws Exception
     * @deprecated 已废弃使用
     */
    //TODO should remove this method
    public String sendGetDDO(String ontid) throws Exception {
        if (ontid == null) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(ontid.getBytes());
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getDDO", arg, null, 0, 0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return res;
        }
        Map map = parseDdoData(ontid, res);
        if (map.size() == 0) {
            return "";
        }
        return JSON.toJSONString(map);
    }

    private Map parseDdoData(String ontid, String obj) throws Exception {
        byte[] bys = Helper.hexToBytes(obj);

        ByteArrayInputStream bais = new ByteArrayInputStream(bys);
        BinaryReader br = new BinaryReader(bais);
        byte[] publickeyBytes;
        byte[] attributeBytes;
        byte[] recoveryBytes;
        try {
            publickeyBytes = br.readVarBytes();
        } catch (Exception e) {
            publickeyBytes = new byte[]{};
        }
        try {
            attributeBytes = br.readVarBytes();
        } catch (Exception e) {
            e.printStackTrace();
            attributeBytes = new byte[]{};
        }
        try {
            recoveryBytes = br.readVarBytes();
        } catch (Exception e) {
            recoveryBytes = new byte[]{};
        }
        List pubKeyList = new ArrayList();
        if (publickeyBytes.length != 0) {
            ByteArrayInputStream bais1 = new ByteArrayInputStream(publickeyBytes);
            BinaryReader br1 = new BinaryReader(bais1);
            while (true) {
                try {
                    Map publicKeyMap = new HashMap();
                    publicKeyMap.put("PubKeyId", ontid + "#keys-" + String.valueOf(br1.readInt()));
                    byte[] pubKey = br1.readVarBytes();
                    if (pubKey.length == 33) {
                        publicKeyMap.put("Type", KeyType.ECDSA.name());
                        publicKeyMap.put("Curve", Curve.P256);
                        publicKeyMap.put("Value", Helper.toHexString(pubKey));
                    } else {
                        publicKeyMap.put("Type", KeyType.fromLabel(pubKey[0]));
                        publicKeyMap.put("Curve", Curve.fromLabel(pubKey[1]));
                        publicKeyMap.put("Value", Helper.toHexString(pubKey));
                    }

                    pubKeyList.add(publicKeyMap);
                } catch (Exception e) {
                    break;
                }
            }
        }
        List attrsList = new ArrayList();
        if (attributeBytes.length != 0) {
            ByteArrayInputStream bais2 = new ByteArrayInputStream(attributeBytes);
            BinaryReader br2 = new BinaryReader(bais2);
            while (true) {
                try {
                    Map<String, Object> attributeMap = new HashMap();
                    attributeMap.put("Key", new String(br2.readVarBytes()));
                    attributeMap.put("Type", new String(br2.readVarBytes()));
                    attributeMap.put("Value", new String(br2.readVarBytes()));
                    attrsList.add(attributeMap);
                } catch (Exception e) {
                    break;
                }
            }
        }

        Map map = new HashMap();
        map.put("Owners", pubKeyList);
        map.put("Attributes", attrsList);
        if (recoveryBytes.length != 0) {
            map.put("Recovery", Address.parse(Helper.toHexString(recoveryBytes)).toBase58());
        }
        map.put("OntId", ontid);
        return map;
    }
}
