package com.github.ontio.sdk.transaction;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.core.Transaction;
import com.github.ontio.core.TransactionAttribute;
import com.github.ontio.core.TransactionAttributeUsage;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.asset.TokenTransfer;
import com.github.ontio.core.asset.Transfers;
import com.github.ontio.core.payload.Vote;
import com.github.ontio.network.rest.RestClient;
import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.exception.ParamCheck;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.account.AccountInfo;
import com.github.ontio.sdk.info.transaction.TransactionInfo;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;


public class OntAssetTx {
    public OntSdk sdk;
    private RestClient txServer;
    private final String ontContract = "ff00000000000000000000000000000000000001";

    public OntAssetTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String transfer(String sendAddr, String password, long amount, String recvAddr) throws Exception {
        amount = amount * 100000000;
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr), new BigInteger(String.valueOf(amount)));
        TokenTransfer tokenTransfer = new TokenTransfer(Address.parse(ontContract), new State[]{state});
        Transfers transfers = new Transfers(new TokenTransfer[]{tokenTransfer});
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
        fees[0] = new Fee(0, Address.addressFromPubKey(publicKey));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), sender.pubkey, VmType.NativeVM.value(), fees);
        String hex = sdk.getWalletMgr().signatureData(password, tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String transferToMany(String sendAddr, String password, long[] amount, String[] recvAddr) throws Exception {

        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        TokenTransfer[] tokenTransfers = new TokenTransfer[recvAddr.length];
        if (amount.length != recvAddr.length) {
            throw new Exception("");
        }
        for (int i = 0; i < recvAddr.length; i++) {
            amount[i] = amount[i] * 100000000;
            State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr[i]), new BigInteger(String.valueOf(amount[i])));
            tokenTransfers[i] = new TokenTransfer(Address.parse(ontContract), new State[]{state});
        }
        Transfers transfers = new Transfers(tokenTransfers);
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
        fees[0] = new Fee(0, Address.addressFromPubKey(publicKey));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), sender.pubkey, VmType.NativeVM.value(), fees);
        String hex = sdk.getWalletMgr().signatureData(password, tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String transferFromMany(String[] sendAddr, String[] password, long[] amount, String recvAddr) throws Exception {
        if (sendAddr == null || sendAddr.length != password.length) {
            throw new Exception("");
        }
        TokenTransfer[] tokenTransfers = new TokenTransfer[sendAddr.length];
        Fee[] fees = new Fee[sendAddr.length];
        for (int i = 0; i < sendAddr.length; i++) {
            AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr[i], password[i]);
            amount[i] = amount[i] * 100000000;
            State state = new State(Address.addressFromPubKey(sdk.getWalletMgr().getPubkey(sender.pubkey)), Address.decodeBase58(recvAddr), new BigInteger(String.valueOf(amount[i])));
            tokenTransfers[i] = new TokenTransfer(Address.parse(ontContract), new State[]{state});
            ECPoint publicKey = sdk.getWalletMgr().getPubkey(sender.pubkey);
            fees[i] = new Fee(0, Address.addressFromPubKey(publicKey));
        }

        Transfers transfers = new Transfers(tokenTransfers);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), null, VmType.NativeVM.value(), fees);
        String hex = sdk.getWalletMgr().signatureData(password, tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    //获取交易信息
    private TransactionInfo getTransactionInfo(String txid) throws IOException, SDKException {
        if (!ParamCheck.isValidTxid(txid)) {
            throw new SDKException(Error.getDescTxidError(String.format("%s=%s", "txid", txid)));
        }
        TransactionInfo info = new TransactionInfo();
        info.txid = txid;
        Transaction tx = sdk.getConnectMgr().getRawTransaction(txid);
        StringBuilder sb = new StringBuilder();
        for (TransactionAttribute attr : tx.attributes) {
            sb.append(Helper.toHexString(attr.data));
        }
        if (sb.toString().length() > 0) {
            info.attrs = new String(Helper.hexToBytes(sb.toString()));
        }
        return info;
    }

    public String voteTx(String addr, String password, ECPoint... pubKeys) throws Exception {
        Vote tx = makeVoteTx(sdk.getWalletMgr().getAccount(addr, password).scriptHash, pubKeys);
        String hex = sdk.getWalletMgr().signatureData(password, tx);
        System.out.println(hex);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
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
