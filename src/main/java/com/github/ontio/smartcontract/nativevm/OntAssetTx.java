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
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.*;
import com.github.ontio.core.payload.Vote;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;


/**
 *
 */
public class OntAssetTx {
    private OntSdk sdk;
    private final String ontContract = "ff00000000000000000000000000000000000001";
    private final String ongContract = "ff00000000000000000000000000000000000002";
    private int precision = 1;
    public OntAssetTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    /**
     * @param assetName
     * @param sendAddr
     * @param password
     * @param amount
     * @param recvAddr
     * @return
     * @throws Exception
     */
    public String sendTransfer(String assetName, String sendAddr, String password, String recvAddr, long amount,long gas) throws Exception {
        if (amount <= 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        Transaction tx = makeTransfer(assetName, sendAddr, password, recvAddr, amount,gas);
        sdk.signTx(tx, sendAddr, password);
        System.out.println(tx.toHexString());
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public Transaction makeTransfer(String assetName, String sendAddr, String password, String recvAddr, long amount,long gas) throws Exception {
        if (amount <= 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        String contractAddr = null;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        amount = amount * precision;
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount);
        Transfers transfers = new Transfers(new State[]{state});
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"transfer",transfers.toArray(), VmType.Native.value(), sendAddr,gas);
        return tx;
    }

    /**
     *
     * @param assetName
     * @param address
     * @return
     * @throws Exception
     */
    public long sendBalanceOf(String assetName, String address) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        byte[] parabytes = buildParams(Address.decodeBase58(address).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"balanceOf", parabytes, VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = (String)obj;
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @param assetName
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @return
     * @throws Exception
     */
    public String sendApprove(String assetName ,String sendAddr, String password, String recvAddr, long amount,long gas) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        state.serialize(binaryWriter);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"approve", byteArrayOutputStream.toByteArray(), VmType.Native.value(), sendAddr,gas);
        sdk.signTx(tx,sendAddr,password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param assetName
     * @param sendAddr
     * @param password
     * @param fromAddr
     * @param toAddr
     * @param amount
     * @return
     * @throws Exception
     */
    public String sendTransferFrom(String assetName ,String sendAddr, String password, String fromAddr, String toAddr, long amount,long gas) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        TransferFrom transferFrom = new TransferFrom(Address.addressFromPubKey(sender.pubkey),Address.decodeBase58(fromAddr), Address.decodeBase58(toAddr), amount);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        transferFrom.serialize(binaryWriter);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"transferFrom", byteArrayOutputStream.toByteArray(), VmType.Native.value(), sendAddr,gas);
        sdk.signTx(tx,sendAddr,password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param assetName
     * @return
     * @throws Exception
     */
    public String queryName(String assetName) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"name", "".getBytes(), VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return new String(Helper.hexToBytes((String) obj));
    }

    /**
     *
     * @param assetName
     * @return
     * @throws Exception
     */
    public String querySymbol(String assetName) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"symbol", "".getBytes(), VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return new String(Helper.hexToBytes((String) obj));
    }

    /**
     *
     * @param assetName
     * @return
     * @throws Exception
     */
    public long queryDecimals(String assetName) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"decimals", "".getBytes(), VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = (String)obj;
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @param assetName
     * @return
     * @throws Exception
     */
    public long queryTotalSupply(String assetName) throws Exception {
        String contractAddr;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"totalSupply", "".getBytes(), VmType.Native.value(), null,0);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        String res = (String)obj;
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    public byte[] buildParams(byte[] ...params) throws SDKException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        try {
            for (byte[] param : params) {
                binaryWriter.writeVarBytes(param);
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * @param assetName
     * @param sendAddr
     * @param password
     * @param amount
     * @param recvAddr
     * @return
     * @throws Exception
     */
    public String sendTransferToMany(String assetName, String sendAddr, String password, String[] recvAddr, long[] amount,long gas) throws Exception {
        for (long amou : amount) {
            if (amou <= 0) {
                throw new SDKException(ErrorCode.AmountError);
            }
        }
        String contractAddr = null;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State[] states = new State[recvAddr.length];
        if (amount.length != recvAddr.length) {
            throw new Exception(ErrorCode.ParamError);
        }
        for (int i = 0; i < recvAddr.length; i++) {
            amount[i] = amount[i] * precision;
            states[i] = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr[i]), amount[i]);
        }
        Transfers transfers = new Transfers(states);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"transfer",transfers.toArray(), VmType.Native.value(), sender.addressBase58,gas);
        sdk.signTx(tx, new Account[][]{{sdk.getWalletMgr().getAccount(sendAddr, password)}});
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     * @param assetName
     * @param sendAddr
     * @param password
     * @param amount
     * @param recvAddr
     * @return
     * @throws Exception
     */
    public String sendTransferFromMany(String assetName, String[] sendAddr, String[] password, String recvAddr, long[] amount,long gas) throws Exception {
        for (long amou : amount) {
            if (amou <= 0) {
                throw new SDKException(ErrorCode.AmountError);
            }
        }
        String contractAddr = null;
        if (assetName.toUpperCase().equals("ONG")) {
            contractAddr = ongContract;
        } else if (assetName.toUpperCase().equals("ONT")) {
            contractAddr = ontContract;
        } else {
            throw new SDKException(ErrorCode.AssetNameError);
        }
        if (sendAddr == null || sendAddr.length != password.length) {
            throw new Exception(ErrorCode.SenderAmtNotEqPasswordAmt);
        }
        State[] states = new State[sendAddr.length];
        for (int i = 0; i < sendAddr.length; i++) {
            AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr[i], password[i]);
            amount[i] = amount[i] * precision;
            states[i] = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount[i]);
        }

        Transfers transfers = new Transfers(states);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr,"transfer", transfers.toArray(), VmType.Native.value(), sendAddr[0],gas);
        Account[][] acct = Arrays.stream(sendAddr).map(p -> {
            for (int i = 0; i < sendAddr.length; i++) {
                if (sendAddr[i].equals(p)) {
                    try {
                        return new Account[]{sdk.getWalletMgr().getAccount(p, password[i])};
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }).toArray(Account[][]::new);
        sdk.signTx(tx, acct);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendOngTransferFrom(String sendAddr, String password, String to, long amount,long gas) throws Exception {
        if (amount <= 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        TransferFrom transferFrom = new TransferFrom(Address.addressFromPubKey(sender.pubkey),Address.parse(ontContract),Address.decodeBase58(to),amount);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"transferFrom", transferFrom.toArray(), VmType.Native.value(), sendAddr,gas);
        sdk.signTx(tx, sendAddr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}
