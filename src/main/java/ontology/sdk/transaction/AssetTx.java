package ontology.sdk.transaction;

import ontology.account.Acct;
import ontology.common.*;
import ontology.core.*;
import ontology.core.contract.Contract;
import ontology.network.connect.ConnectorException;
import ontology.network.connect.ConnectorRuntimeException;
import ontology.network.rest.RestException;
import ontology.network.rest.RestClient;
import ontology.OntSdk;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.CoinException;
import ontology.sdk.exception.SDKException;
import ontology.sdk.exception.ParamCheck;
import ontology.account.Coin;
import ontology.sdk.info.account.AccountAsset;
import ontology.sdk.info.account.Asset;
import ontology.sdk.info.asset.AssetInfo;
import ontology.sdk.info.mutil.TxJoiner;
import ontology.sdk.info.transaction.TransactionInfo;
import ontology.sdk.info.transaction.TxInputInfo;
import ontology.sdk.info.transaction.TxOutputInfo;
import com.alibaba.fastjson.JSON;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static ontology.common.Common.currentTime;
import static ontology.sdk.exception.ParamCheck.*;


public class AssetTx {
    public OntSdk sdk;
    private RestClient txServer;

    public AssetTx (OntSdk sdk) {
        this.sdk = sdk;
    }

    public String registerTransaction(String password,String issuer, String name, long amount, String desc, String controller) throws Exception {
        checkRegisterParameter(issuer, name, amount, desc, controller);
        RegisterTransaction tx = makeRegisterTx(sdk.getWalletMgr().getAccount(password,issuer), name, amount, desc, AssetType.Token, controller, Fixed8.DefaultPrecision);
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String issueTransaction(String password,String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
        checkIssueAndTransferParameter(sendAddr, assetid, amount, recvAddr, desc);
        IssueTransaction tx = makeIssueTx(assetid, amount, recvAddr, desc);
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String transferTransaction(String password,String sendAddr, String assetid, long amount, String recvAddr, String desc) throws Exception {
        checkIssueAndTransferParameter(sendAddr, assetid, amount, recvAddr, desc);
        TransferTransaction tx = makeTranferTx(assetid, amount, recvAddr, desc);
        sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),tx, Common.toScriptHash(sendAddr));
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    //构造注册资产交易
    public RegisterTransaction makeRegisterTransaction(String password,String issuer, String name, long amount, String desc, String controller) throws SDKException {
        checkRegisterParameter(issuer, name, amount, desc, controller);
        RegisterTransaction tx = makeRegisterTx(sdk.getWalletMgr().getAccount(password,issuer), name, amount, desc, AssetType.Token, controller, Fixed8.DefaultPrecision);
        return tx;
    }
    //构造注册资产交易
    public RegisterTransaction makeRegisterTransaction(String password,String issuer, String name, long amount, String desc, String controller, int precision) throws SDKException {
        checkRegisterParameter(issuer, name, amount, desc, controller);
        RegisterTransaction tx = makeRegisterTx(sdk.getWalletMgr().getAccount(password,issuer), name, amount, desc, AssetType.Token, controller, precision);
        return tx;
    }

    // 构造分发资产交易
    public IssueTransaction makeIssueTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws SDKException {
        checkIssueAndTransferParameter(sendAddr, assetid, amount, recvAddr, desc);
        return makeIssueTx(assetid, amount, recvAddr, desc);
    }

    //构造分发资产交易(多个接收者)
    public IssueTransaction makeIssueTransaction(String sendAddr, List<TxJoiner> list, String desc) throws SDKException {
        checkIssueAndTransferParameterList(sendAddr, list, desc);
        return makeIssueTx(list, desc);
    }

    //构造转移资产交易
    public TransferTransaction makeTransferTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws SDKException {
        checkIssueAndTransferParameter(sendAddr, assetid, amount, recvAddr, desc);
        TransferTransaction tx = makeTranferTx(assetid, amount, recvAddr, desc);
        return sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),tx, Common.toScriptHash(sendAddr));
    }

    // 构造转移资产交易,多个接收者
    public TransferTransaction makeTransferTransaction(String sendAddr, List<TxJoiner> list, String desc) throws SDKException {
        checkIssueAndTransferParameterList(sendAddr, list, desc);
        TransferTransaction tx = makeTranferTx(list, desc);
        return sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),tx, Common.toScriptHash(sendAddr));
    }

    //构造资产注销交易
    public DestroyTransaction makeDestroyTransaction(String issuer, String assetId, String txDesc) throws SDKException {
        checkDestroyParameter(issuer, assetId, txDesc);
        DestroyTransaction tx = new DestroyTransaction();
        tx.inputs = sdk.getCoinManager().queryAccountAsset(sdk.getConnectMgr(),issuer, assetId).stream().filter(p -> p.stateStr.equals("Unspent")).map(p -> p.input).toArray(TransactionInput[]::new);
        tx.outputs = new TransactionOutput[0];
        if (txDesc != null && txDesc.length() > 0) {
            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.Description;
            tx.attributes[0].data = Common.toAttr(txDesc);
        } else {
            tx.attributes = new TransactionAttribute[0];
        }
        return tx;
    }


    private RegisterTransaction makeRegisterTx(Acct acc, String assetName, long assetAmount, String txDesc, AssetType assetType, String controller, int precision) {
        RegisterTransaction tx = new RegisterTransaction();
        tx.precision = (byte) precision;
        tx.assetType = AssetType.Token;
        tx.recordType = RecordType.UTXO;
        tx.assetType = assetType;
        tx.name = assetName;
        tx.description = txDesc;
        tx.amount = Fixed8.parse(String.valueOf(assetAmount));
        tx.issuer = acc.publicKey;
        tx.admin = Common.toScriptHash(controller);
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        if (txDesc != null && txDesc.length() > 0) {
            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.Description;
            tx.attributes[0].data = Common.toAttr(txDesc);
        } else {
            tx.attributes = new TransactionAttribute[0];
        }
        return tx;
    }


    private IssueTransaction makeIssueTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
        IssueTransaction tx = new IssueTransaction();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[1];
        tx.outputs[0] = new TransactionOutput();
        tx.outputs[0].assetId = UInt256.parse(assetId);
        tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
        tx.outputs[0].scriptHash = Common.toScriptHash(recvAddr);
        int len = txDesc != null && txDesc.length() > 0 ? 2 : 1;
        tx.attributes = new TransactionAttribute[len];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Description;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();//Common.generateKey64Bit();
        for (int i = 1; i < len; ++i) {
            tx.attributes[i] = new TransactionAttribute();
            tx.attributes[i].usage = TransactionAttributeUsage.Description;
            tx.attributes[i].data = Common.toAttr(txDesc);
        }

        return tx;
    }

    private IssueTransaction makeIssueTx(List<TxJoiner> recvlist, String txDesc) {
        int size = recvlist.size();
        IssueTransaction tx = new IssueTransaction();
        tx.nonce = (long) Math.random() * 1000000;
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[1];
        tx.outputs = new TransactionOutput[size];
        for (int i = 0; i < size; ++i) {
            TxJoiner recv = recvlist.get(i);
            tx.outputs[i] = new TransactionOutput();
            tx.outputs[i].assetId = UInt256.parse(recv.assetid);
            tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
            tx.outputs[i].scriptHash = Common.toScriptHash(recv.address);
        }
        if (txDesc != null && txDesc.length() > 0) {
            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.Description;
            tx.attributes[0].data = Common.toAttr(txDesc);
        } else {
            tx.attributes = new TransactionAttribute[0];
        }
        return tx;
    }

    private TransferTransaction makeTranferTx(String assetId, long assetAmount, String recvAddr, String txDesc) {
        TransferTransaction tx = new TransferTransaction();
        tx.outputs = new TransactionOutput[1];
        tx.outputs[0] = new TransactionOutput();
        tx.outputs[0].assetId = UInt256.parse(assetId);
        tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
        tx.outputs[0].scriptHash = Common.toScriptHash(recvAddr);
        if (txDesc != null && txDesc.length() > 0) {
            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.Description;
            tx.attributes[0].data = Common.toAttr(txDesc);
        } else {
            tx.attributes = new TransactionAttribute[0];
        }
        return tx;
    }

    private TransferTransaction makeTranferTx(List<TxJoiner> recvList, String txDesc) {
        int size = recvList.size();
        TransferTransaction tx = new TransferTransaction();
        tx.outputs = new TransactionOutput[size];
        for (int i = 0; i < size; ++i) {
            TxJoiner recv = recvList.get(i);
            tx.outputs[i] = new TransactionOutput();
            tx.outputs[i].assetId = UInt256.parse(recv.assetid);
            tx.outputs[i].value = Fixed8.parse(String.valueOf(recv.value));
            tx.outputs[i].scriptHash = Common.toScriptHash(recv.address);
        }
        if (txDesc != null && txDesc.length() > 0) {
            tx.attributes = new TransactionAttribute[1];
            tx.attributes[0] = new TransactionAttribute();
            tx.attributes[0].usage = TransactionAttributeUsage.Description;
            tx.attributes[0].data = Common.toAttr(txDesc);
        } else {
            tx.attributes = new TransactionAttribute[0];
        }
        return tx;
    }


    public void setIssServiceUrl(String url) {
        txServer = new RestClient(url);
    }

    public boolean notifyTxServer(Transaction tx, String data) throws RestException {
        if (tx instanceof IssueTransaction) {
            return txServer.sendToIssService(data);
        } else if (tx instanceof TransferTransaction) {
            return txServer.sendToTrfService(data);
        }
        return false;
    }

    public boolean sendToIssueService(String sendAddr, String assetid, long amount, String recvAddr, String txid) throws RestException {
        String data = makeIssueMessage(sendAddr, recvAddr, txid);
        System.out.println("sendToIssService:" + data);
        return txServer.sendToIssService(data);
    }

    public boolean sendToTransferService(String sendAddr, String assetid, long amount, String recvAddr, String txid) throws RestException {
        String data = makeTransferMessage(sendAddr, recvAddr, txid);
        System.out.println("sendToTrfService:" + data);
        return txServer.sendToTrfService(data);
    }

    public IssueTransaction makeComposeIssueTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws RestException, CoinException {
        IssueTransaction newTx = makeIssueTx(assetid, amount, sendAddr, desc);
        IssueTransaction tx = sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),newTx, Common.toScriptHash(sendAddr));    // 分发给自己-> IssService -> 转战给recver
        String data = makeIssueMessage(sendAddr, recvAddr, tx.hash().toString());
        if (notifyTxServer(tx, data)) {
            return tx;
        }
        throw new RestException(Error.getDescComposeIssueTx("Not finished sendToIssService"));
    }

    public TransferTransaction makeComposeTransferTransaction(String sendAddr, String assetid, long amount, String recvAddr, String desc) throws RestException, CoinException {
        TransferTransaction newTx = makeTranferTx(assetid, amount, recvAddr, desc);
        TransferTransaction tx = sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),newTx, Common.toScriptHash(sendAddr));
        String data = makeTransferMessage(sendAddr, recvAddr, tx.hash().toString());
        if (notifyTxServer(tx, data)) {
            return tx;
        }
        throw new RestException(Error.getDescComposeIssueTx("Not finished sendToTrfService"));
    }

    public String makeIssueMessage(String sendAddr, String recvAddr, String issTxid) {
        String data = String.format("{\"issuetxid\":\"%s\",\"bankAddr\":\"%s\",\"companyAddr\":\"%s\",\"sendtime\":\"%s\"}",
                issTxid, sendAddr, recvAddr, currentTime());
        return data;
    }

    public String makeTransferMessage(String sendAddr, String recvAddr, String issTxid) {
        String data = String.format("{\"transtxid\":\"%s\",\"bankAddr\":\"%s\",\"companyAddr\":\"%s\",\"sendtime\":\"%s\"}",
                issTxid, sendAddr, recvAddr, currentTime());
        return data;
    }



    //获取账户资产
//    private AccountAsset getAccountAsset(String password, String address) throws SDKException {
//        if (!ParamCheck.isValidAddress(address)) {
//            throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "address", address)));
//        }
//        AccountAsset asset = new AccountAsset();
//        Contract con = sdk.getWalletMgr().getContract(password,address);
//        asset.address = con.address();
//        asset.canUseAssets = new ArrayList<Asset>();
//        asset.freezeAssets = new ArrayList<Asset>();
//        List<Coin> list = sdk.getCoinManager().queryAccountAsset(sdk.getConnectMgr(),address);
//        list.stream().filter(p -> p.stateStr.endsWith("Unspent")).forEach(p -> {
//            Asset as = new Asset();
//            as.assetid = p.assetId.toString();
//            as.amount = p.value.toLong();
//            asset.canUseAssets.add(as);
//        });
//        list.stream().filter(p -> p.stateStr.endsWith("Spending")).forEach(p -> {
//            Asset as = new Asset();
//            as.assetid = p.assetId.toString();
//            as.amount = p.value.toLong();
//            asset.freezeAssets.add(as);
//        });
//        return asset;
//    }


    // 获取资产信息
    public AssetInfo getAssetInfo(String assetid) throws IOException, SDKException {
        if (!ParamCheck.isValidAssetId(assetid)) {
            throw new SDKException(Error.getDescAssetIdError(String.format("%s=%s", "assetid", assetid)));
        }
        String ss = sdk.getConnectMgr().getAsset(assetid);
        return JSON.parseObject(ss, AssetInfo.class);
    }

    //获取交易信息
    private TransactionInfo getTransactionInfo(String txid) throws IOException, SDKException {
        if (!ParamCheck.isValidTxid(txid)) {
            throw new SDKException(Error.getDescTxidError(String.format("%s=%s", "txid", txid)));
        }
        TransactionInfo info = new TransactionInfo();
        info.txid = txid;
        Transaction tx = sdk.getConnectMgr().getRawTransaction(txid);
        if (tx instanceof RegisterTransaction) {
            info.type = RegisterTransaction.class.getSimpleName();
        } else if (tx instanceof IssueTransaction) {
            info.type = IssueTransaction.class.getSimpleName();
        } else if (tx instanceof TransferTransaction) {
            info.type = TransferTransaction.class.getSimpleName();
        } else if (tx instanceof RecordTransaction) {
            info.type = RecordTransaction.class.getSimpleName();
        }
        try {
            info.inputs = new ArrayList<TxInputInfo>();
            Arrays.stream(tx.inputs).map(p -> getTxByNextTxInput(p)).forEach(p -> {
                TxInputInfo in = new TxInputInfo();
                in.address = Common.toAddress(p.scriptHash);
                in.assetid = p.assetId.toString();
                in.amount = p.value.toLong();
                info.inputs.add(in);
            });
            info.outputs = new ArrayList<TxOutputInfo>();
            Arrays.stream(tx.outputs).forEach(p -> {
                TxOutputInfo out = new TxOutputInfo();
                out.address = Common.toAddress(p.scriptHash);
                out.assetid = p.assetId.toString();
                out.amount = p.value.toLong();
                info.outputs.add(out);
            });
        } catch (ConnectorRuntimeException ex) {
            new SDKException(Error.getDescNetworkError(ex.getMessage()), ex);
        }
        StringBuilder sb = new StringBuilder();
        for (TransactionAttribute attr : tx.attributes) {
            sb.append(Helper.toHexString(attr.data));
        }
        if (sb.toString().length() > 0) {
            info.attrs = new String(Helper.hexToBytes(sb.toString()));
        }
        return info;
    }

    private TransactionOutput getTxByNextTxInput(TransactionInput input) {
        Transaction tx;
        try {
            tx = sdk.getConnectMgr().getRawTransaction(input.prevHash.toString());
        } catch (ConnectorException | IOException e) {
            throw new ConnectorRuntimeException("Not find tx by next txInput:" + input.prevHash.toString(), e);
        }
        return tx.outputs[input.prevIndex];
    }
    public String claimTx(String password,String addr,String assetId) throws Exception {
        Claim tx = makeClaimTx(addr,assetId,10);
        String hex = sdk.getWalletMgr().signatureData(password,tx);
        System.out.println(hex);
        boolean b = sdk.getConnectMgr().sendRawTransaction(hex);
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    private Claim makeClaimTx(String addr,String assetId,long assetAmount) {
        Claim tx = new Claim();
        tx.claims = sdk.getCoinManager().queryAccountAsset(sdk.getConnectMgr(),addr, assetId).stream().filter(p -> p.stateStr.equals("Unspent")).map(p -> p.input).toArray(TransactionInput[]::new);

        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[1];
        tx.outputs[0] = new TransactionOutput();
        tx.outputs[0].assetId = UInt256.parse(assetId);
        tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
        tx.outputs[0].scriptHash = Common.toScriptHash(addr);
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Description;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        return tx;
    }
    public String voteTx(String password,String addr,ECPoint... pubKeys) throws Exception {
        Vote tx = makeVoteTx(sdk.getWalletMgr().getAccount(password,addr).scriptHash,pubKeys);
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

        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Description;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        return tx;
    }
}
