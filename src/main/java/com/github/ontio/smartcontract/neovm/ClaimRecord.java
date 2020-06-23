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

package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.github.ontio.smartcontract.neovm.abi.Parameter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ClaimRecord {
    private OntSdk sdk;
    private String contractAddress = "36bb5c053b6b839c8f6b923fe852f91239b9fccc";

    private String abi = "{\"hash\":\"0x36bb5c053b6b839c8f6b923fe852f91239b9fccc\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Commit\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"},{\"name\":\"commiterId\",\"type\":\"ByteArray\"},{\"name\":\"ownerId\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"Revoke\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"},{\"name\":\"ontId\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"GetStatus\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"ByteArray\"}],\"returntype\":\"ByteArray\"}],\"events\":[{\"name\":\"ErrorMsg\",\"parameters\":[{\"name\":\"id\",\"type\":\"ByteArray\"},{\"name\":\"error\",\"type\":\"String\"}],\"returntype\":\"Void\"},{\"name\":\"Push\",\"parameters\":[{\"name\":\"id\",\"type\":\"ByteArray\"},{\"name\":\"msg\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"ByteArray\"}],\"returntype\":\"Void\"}]}";

    private String abi2 = "{\"hash\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Commit\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"\"},{\"name\":\"commiterId\",\"type\":\"\"},{\"name\":\"index\",\"type\":\"\"},{\"name\":\"ownerId\",\"type\":\"\"}]},{\"name\":\"Revoke\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"\"},{\"name\":\"ontId\",\"type\":\"\"},{\"name\":\"index\",\"type\":\"\"}]},{\"name\":\"Remove\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"\"},{\"name\":\"ownerId\",\"type\":\"\"},{\"name\":\"index\",\"type\":\"\"}]},{\"name\":\"GetStatus\",\"parameters\":[{\"name\":\"claimId\",\"type\":\"\"}]},{\"name\":\"Upgrade\",\"parameters\":[{\"name\":\"code\",\"type\":\"\"},{\"name\":\"needStorage\",\"type\":\"\"},{\"name\":\"name\",\"type\":\"\"},{\"name\":\"version\",\"type\":\"\"},{\"name\":\"author\",\"type\":\"\"},{\"name\":\"email\",\"type\":\"\"},{\"name\":\"description\",\"type\":\"\"}]}]}";

    public ClaimRecord(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    /**
     * @param issuerOntid
     * @param password
     * @param subjectOntid
     * @param claimId
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendCommit(String issuerOntid, String password, byte[] salt, String subjectOntid, String claimId, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (issuerOntid == null || issuerOntid.equals("") || password == null || password.equals("") || subjectOntid == null || subjectOntid.equals("")
                || claimId == null || claimId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeCommit(issuerOntid, subjectOntid, claimId, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, issuerOntid, password, salt);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    // new version claim contract
    public String sendCommit2(String issuerOntid, String password, byte[] salt, String subjectOntid, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (issuerOntid == null || issuerOntid.equals("") || password == null || password.equals("") || subjectOntid == null || subjectOntid.equals("")
                || claimId == null || claimId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeCommit2(issuerOntid, subjectOntid, claimId, pubkeyIndex, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, issuerOntid, password, salt);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param issuerOntid
     * @param subjectOntid
     * @param claimId
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeCommit(String issuerOntid, String subjectOntid, String claimId, String payer, long gaslimit, long gasprice) throws Exception {
        if (issuerOntid == null || issuerOntid.equals("") || subjectOntid == null || subjectOntid.equals("") || payer == null || payer.equals("")
                || claimId == null || claimId.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }

        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Commit";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes(), issuerOntid.getBytes(), subjectOntid.getBytes());
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer, gaslimit, gasprice);
        return tx;
    }

    public Transaction makeCommit2(String issuerOntid, String subjectOntid, String claimId, int pubkeyIndex, String payer, long gaslimit, long gasprice) throws Exception {
        if (issuerOntid == null || issuerOntid.equals("") || subjectOntid == null || subjectOntid.equals("") || payer == null || payer.equals("")
                || claimId == null || claimId.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }

        String name = "Commit";
        Parameter claimIdParam = new Parameter("claimId", Parameter.Type.String, claimId);
        Parameter commiterIdParam = new Parameter("commiterId", Parameter.Type.String, issuerOntid);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, pubkeyIndex);
        Parameter ownerIdParam = new Parameter("ownerId", Parameter.Type.String, subjectOntid);
        AbiFunction func = new AbiFunction(name, claimIdParam, commiterIdParam, indexParam, ownerIdParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer, gaslimit, gasprice);
        return tx;
    }

    /**
     * @param issuerOntid
     * @param password
     * @param claimId
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendRevoke(String issuerOntid, String password, byte[] salt, String claimId, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (issuerOntid == null || issuerOntid.equals("") || password == null || password.equals("")
                || claimId == null || claimId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = issuerOntid.replace(Common.didont, "");
        Transaction tx = makeRevoke(issuerOntid, claimId, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, addr, password, salt);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendRevoke2(String ownerId, String password, byte[] salt, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || ownerId.equals("") || password == null || password.equals("")
                || claimId == null || claimId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRevoke2(ownerId, claimId, pubkeyIndex, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, ownerId, password, salt);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeRevoke(String issuerOntid, String claimId, String payer, long gaslimit, long gasprice) throws Exception {
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Revoke";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes(), issuerOntid.getBytes());
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer, gaslimit, gasprice);
        return tx;
    }

    public Transaction makeRevoke2(String ownerId, String claimId, int pubkeyIndex, String payer, long gaslimit, long gasprice) throws Exception {
        String name = "Revoke";
        Parameter claimIdParam = new Parameter("claimId", Parameter.Type.String, claimId);
        Parameter ownerIdParam = new Parameter("ontId", Parameter.Type.String, ownerId);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, pubkeyIndex);
        AbiFunction func = new AbiFunction(name, claimIdParam, ownerIdParam, indexParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendRemove2(String ownerId, String password, byte[] salt, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if (ownerId == null || ownerId.equals("") || password == null || password.equals("")
                || claimId == null || claimId.equals("") || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        Transaction tx = makeRemove2(ownerId, claimId, pubkeyIndex, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);
        sdk.signTx(tx, ownerId, password, salt);
        sdk.addSign(tx, payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public Transaction makeRemove2(String ownerOntId, String claimId, int pubkeyIndex, String payer, long gaslimit, long gasprice) throws Exception {
        String name = "Remove";
        Parameter claimIdParam = new Parameter("claimId", Parameter.Type.String, claimId);
        Parameter ownerIdParam = new Parameter("ownerId", Parameter.Type.String, ownerOntId);
        Parameter indexParam = new Parameter("index", Parameter.Type.Integer, pubkeyIndex);
        AbiFunction func = new AbiFunction(name, claimIdParam, ownerIdParam, indexParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer, gaslimit, gasprice);
        return tx;
    }

    public String sendGetStatus(String claimId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if ("".equals(claimId)) {
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "GetStatus";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes());
        Object obj = sdk.neovm().sendTransaction(Helper.reverse(contractAddress), null, null, 0, 0, func, true);
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return "";
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader br = new BinaryReader(bais);
        ClaimTx claimTx = new ClaimTx();
        claimTx.deserialize(br);
        if (claimTx.status.length == 0) {
            return new String(claimTx.claimId) + "." + "00" + "." + new String(claimTx.issuerOntId) + "." + new String(claimTx.subjectOntId);
        }
        return new String(claimTx.claimId) + "." + Helper.toHexString(claimTx.status) + "." + new String(claimTx.issuerOntId) + "." + new String(claimTx.subjectOntId);
    }

    public String sendGetStatus2(String claimId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if ("".equals(claimId)) {
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "GetStatus";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(claimId.getBytes());
        Object obj = sdk.neovm().sendTransaction(Helper.reverse(contractAddress), null, null, 0, 0, func, true);
        return ((JSONObject) obj).getString("Result");
    }
}

class ClaimTx implements Serializable {
    public byte[] claimId;
    public byte[] issuerOntId;
    public byte[] subjectOntId;
    public byte[] status;

    ClaimTx() {
    }

    ClaimTx(byte[] claimId, byte[] issuerOntId, byte[] subjectOntId, byte[] status) {
        this.claimId = claimId;
        this.issuerOntId = issuerOntId;
        this.subjectOntId = subjectOntId;
        this.status = status;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        byte dataType = reader.readByte();
        long length = reader.readVarInt();
        byte dataType2 = reader.readByte();
        this.claimId = reader.readVarBytes();
        byte dataType3 = reader.readByte();
        this.issuerOntId = reader.readVarBytes();
        byte dataType4 = reader.readByte();
        this.subjectOntId = reader.readVarBytes();
        byte dataType5 = reader.readByte();
        this.status = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
