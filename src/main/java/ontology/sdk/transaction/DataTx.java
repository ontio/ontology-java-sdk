package ontology.sdk.transaction;

import ontology.common.*;
import ontology.core.contract.Contract;
import ontology.core.scripts.Program;
import ontology.core.Transaction;
import ontology.core.TransactionOutput;
import ontology.OntSdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 2018/1/9.
 */
public class DataTx {

    public OntSdk sdk;
    public DataTx(OntSdk sdk) {
        this.sdk =sdk;
    }
    //初始化资金池合约
    public String InvokeInitFund(String password,String codeHash,String addr,byte[] assetid,byte[] admin,byte[] caller) throws Exception {
        List list = new ArrayList<Object>();
        list.add("init".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(assetid);
        tmp.add(admin);
        tmp.add(caller);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,addr);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //充值
    public String InvokeDepositFund(String password,String codeHash,String addr,String assetId,int assetAmount) throws Exception {
        List list = new ArrayList<Object>();
        list.add("deposit".getBytes());
        List tmp = new ArrayList<Object>();
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,addr);
        tx.outputs = new TransactionOutput[1];
        tx.outputs[0] = new TransactionOutput();
        tx.outputs[0].assetId = UInt256.parse(assetId);
        tx.outputs[0].value = Fixed8.parse(String.valueOf(assetAmount));
        tx.outputs[0].scriptHash =  new UInt160(Helper.hexToBytes(codeHash));

        sdk.getCoinManager().makeTransaction(sdk.getConnectMgr(),tx, Common.toScriptHash(addr));
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //初始化协议合约
    public String InvokeInitProto(String password,String codeHash,String addr,byte[] admin,byte[] caller) throws Exception {
        List list = new ArrayList<Object>();
        list.add("init".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(admin);
        tmp.add(caller);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,addr);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //创建订单
    public String InvokeMakeBuyOrder(String password,String codeHash, byte[] orderSig,byte[] orderId,String  buyerPk,String sellerAddr,int amount) throws Exception { //String buyerPk,
        List list = new ArrayList<Object>();
        list.add("makebuyorder".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(orderSig);
        tmp.add(orderId);
        tmp.add(Program.toScriptHash(Contract.createSignatureRedeemScript(buyerPk)).toArray());
        tmp.add(Program.toScriptHash(Contract.createSignatureRedeemScript(sdk.getWalletMgr().getAccountInfo(password,sellerAddr).pubkey)).toArray());
        tmp.add(Helper.hexToBytes(buyerPk));
        tmp.add(amount);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,sellerAddr);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //确认订单
    public String InvokeBuyerComfirmOrder(String password,String codeHash, String orderId,String buyer) throws Exception {
        List list = new ArrayList<Object>();
        list.add("buyercomfirmorder".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(orderId);
        tmp.add(buyer);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,buyer);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //取消订单
    public String InvokeBuyerCancelOrder(String password,String codeHash, String orderId,String buyer) throws Exception {
        List list = new ArrayList<Object>();
        list.add("buyerconcelorder".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(orderId);
        tmp.add(buyer);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,buyer);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
    //尝试关闭订单
    public String InvokeSellerTryCloseOrder(String password,String codeHash, String orderId,String seller) throws Exception {
        List list = new ArrayList<Object>();
        list.add("sellertrycloseorder".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(orderId);
        tmp.add(seller);
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(password,sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,seller);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(txHex);
        if(b){
            return tx.hash().toString();
        }
        return null;
    }
}
