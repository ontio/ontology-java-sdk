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

package com.github.ontio.sdk.transaction;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Acct;
import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionAttribute;
import com.github.ontio.core.transaction.TransactionAttributeUsage;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.*;
import com.github.ontio.core.payload.Vote;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.account.AccountInfo;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;


/**
 *
 */
public class OntAssetTx {
    public OntSdk sdk;
    private final String ontContract = "ff00000000000000000000000000000000000001";
    private final String ongContract = "ff00000000000000000000000000000000000002";

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
    public String transfer(String assetName, String sendAddr, String password, String recvAddr, long amount) throws Exception {
        amount = amount * 100000000;
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr), new BigInteger(String.valueOf(amount)));
        String contract = null;
        if (assetName.equals("ong")) {
            contract = ongContract;
        } else if (assetName.equals("ont")) {
            contract = ontContract;
        } else {
            throw new SDKException("asset name error");
        }
        TokenTransfer tokenTransfer = new TokenTransfer(Address.parse(contract), new State[]{state});
        Transfers transfers = new Transfers(new TokenTransfer[]{tokenTransfer});
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
        fees[0] = new Fee(0, Address.addressFromPubKey(publicKey));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), sender.pubkey, VmType.NativeVM.value(), fees);
//        sdk.getWalletMgr().signatureData(password, tx);
        sdk.signTx(tx,new Acct[][]{{sdk.getWalletMgr().getAccount(sendAddr, password)}});
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
    public String transferToMany(String assetName, String sendAddr, String password, String[] recvAddr, long[] amount) throws Exception {
        String contract = null;
        if (assetName.equals("ong")) {
            contract = ongContract;
        } else if (assetName.equals("ont")) {
            contract = ontContract;
        } else {
            throw new SDKException("asset name error");
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        TokenTransfer[] tokenTransfers = new TokenTransfer[recvAddr.length];
        if (amount.length != recvAddr.length) {
            throw new Exception("");
        }
        for (int i = 0; i < recvAddr.length; i++) {
            amount[i] = amount[i] * 100000000;
            State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr[i]), new BigInteger(String.valueOf(amount[i])));
            tokenTransfers[i] = new TokenTransfer(Address.parse(contract), new State[]{state});
        }
        Transfers transfers = new Transfers(tokenTransfers);
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
        fees[0] = new Fee(0, Address.addressFromPubKey(publicKey));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), sender.pubkey, VmType.NativeVM.value(), fees);
        //String hex = sdk.getWalletMgr().signatureData(password, tx);
        sdk.signTx(tx,new Acct[][]{{sdk.getWalletMgr().getAccount(sendAddr, password)}});
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
    public String transferFromMany(String assetName, String[] sendAddr, String[] password, String recvAddr, long[] amount) throws Exception {
        String contract = null;
        if (assetName.equals("ong")) {
            contract = ongContract;
        } else if (assetName.equals("ont")) {
            contract = ontContract;
        } else {
            throw new SDKException("asset name error");
        }
        if (sendAddr == null || sendAddr.length != password.length) {
            throw new Exception("");
        }
        TokenTransfer[] tokenTransfers = new TokenTransfer[sendAddr.length];
        Fee[] fees = new Fee[sendAddr.length];
        for (int i = 0; i < sendAddr.length; i++) {
            AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr[i], password[i]);
            amount[i] = amount[i] * 100000000;
            State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr), new BigInteger(String.valueOf(amount[i])));
            tokenTransfers[i] = new TokenTransfer(Address.parse(contract), new State[]{state});
            ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
            fees[i] = new Fee(0, Address.addressFromPubKey(publicKey));
        }

        Transfers transfers = new Transfers(tokenTransfers);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), null, VmType.NativeVM.value(), fees);
//        String hex = sdk.getWalletMgr().signatureData(password, tx);
        Acct[][] acct =  Arrays.stream(sendAddr).map(p -> {
            for(int i=0;i<sendAddr.length;i++){
                if(sendAddr[i].equals(p)){
                    try {
                        return new Acct[]{sdk.getWalletMgr().getAccount(p, password[i])};
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }).toArray(Acct[][]::new);

        sdk.signTx(tx,acct);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }



    public String voteTx(String addr, String password, ECPoint... pubKeys) throws Exception {
        Vote tx = makeVoteTx(sdk.getWalletMgr().getAccount(addr, password).addressU160, pubKeys);
        sdk.signTx(tx,new Acct[][]{{sdk.getWalletMgr().getAccount(addr, password)}});
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    private Vote makeVoteTx(Address account, ECPoint... pubKeys) {
        Vote tx = new Vote();
        tx.pubKeys = pubKeys;
        tx.account = account;

        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Description;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        return tx;
    }
}
