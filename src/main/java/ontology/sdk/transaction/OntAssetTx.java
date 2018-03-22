package ontology.sdk.transaction;

import ontology.OntSdk;
import ontology.account.Acct;
import ontology.common.*;
import ontology.core.*;
import ontology.core.asset.Fee;
import ontology.core.contract.Contract;
import ontology.core.asset.State;
import ontology.core.asset.TokenTransfer;
import ontology.core.asset.Transfers;
import ontology.core.payload.*;
import ontology.network.rest.RestClient;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.ParamCheck;
import ontology.sdk.exception.SDKException;
import ontology.sdk.info.account.AccountInfo;
import ontology.sdk.info.transaction.TransactionInfo;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import static ontology.sdk.exception.ParamCheck.*;


public class OntAssetTx {
    public OntSdk sdk;
    private RestClient txServer;
    private final String ontContract = "ff00000000000000000000000000000000000001";

    public OntAssetTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String transfer(String sendAddr,String password, long amount, String recvAddr, String desc) throws Exception {
        AccountInfo info1 = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Contract.addressFromPubKey(sdk.getWalletMgr().getPubkey(info1.pubkey)),UInt160.decodeBase58(recvAddr),new BigInteger(String.valueOf(amount)));
        TokenTransfer tokenTransfer = new TokenTransfer(UInt160.parse(ontContract),new State[]{state});
        Transfers transfers = new Transfers(new TokenTransfer[]{tokenTransfer});

        System.out.println("####"+Helper.toHexString(transfers.toArray()));
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(info1.pubkey);
        fees[0] = new Fee(amount,Contract.addressFromPubKey(publicKey));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(transfers.toArray(), "","", info1.pubkey,VmType.NativeVM.value(),fees);
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
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

    public String voteTx(String addr,String password,ECPoint... pubKeys) throws Exception {
        Vote tx = makeVoteTx(sdk.getWalletMgr().getAccount(addr,password).scriptHash,pubKeys);
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        System.out.println(hex);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    private Vote makeVoteTx(UInt160 account, ECPoint... pubKeys) {
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
