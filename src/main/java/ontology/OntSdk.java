package ontology;

import ontology.sdk.manager.CoinMgr;
import ontology.sdk.manager.ConnectMgr;
import ontology.sdk.manager.WalletMgr;


public class OntSdk {
    //oep管理器
    private WalletMgr walletMgr;
    //coin管理器
    private CoinMgr coinManager;
    //连接管理器
    private ConnectMgr connManager;

    //OntId 交易
    private ontology.sdk.transaction.OntIdTx ontIdTx = null;
    //数据交易
    private ontology.sdk.transaction.DataTx dataTx = null;
    //资产交易
    private ontology.sdk.transaction.AssetTx assetTx = null;
    //存证交易
    private ontology.sdk.transaction.RecordTx recordTx = null;

    //智能合约交易
    private ontology.sdk.transaction.SmartcodeTx smartcodeTx = null;

    public static OntSdk instance = null;
    public static OntSdk getInstance(){
        if(instance == null){
            instance = new OntSdk();
        }
        return instance;
    }
    public ontology.sdk.transaction.AssetTx getAssetTx() {
        if(assetTx == null){
            assetTx = new ontology.sdk.transaction.AssetTx(getInstance());
        }
        return assetTx;
    }

    public ontology.sdk.transaction.RecordTx getRecordTx() {
        if(recordTx == null){
            recordTx = new ontology.sdk.transaction.RecordTx(getInstance());
        }
        return recordTx;
    }

    public ontology.sdk.transaction.OntIdTx getOntIdTx() {
        if(ontIdTx == null){
            getSmartcodeTx();
            ontIdTx = new ontology.sdk.transaction.OntIdTx(getInstance());
        }
        return ontIdTx;
    }

    public ontology.sdk.transaction.DataTx getDataTx() {
        if(dataTx == null){
            getSmartcodeTx();
            dataTx = new ontology.sdk.transaction.DataTx(getInstance());
        }
        return dataTx;
    }

    public ontology.sdk.transaction.SmartcodeTx getSmartcodeTx() {
        if(smartcodeTx == null){
            smartcodeTx = new ontology.sdk.transaction.SmartcodeTx(getInstance());
        }
        return smartcodeTx;
    }
    public void setWsSessionId(String sessionId){
        getOntIdTx().setWsSessionId(sessionId);
        getSmartcodeTx().setWsSessionId(sessionId);
    }
    public void setCodeHash(String codeHash){
        getOntIdTx().setCodeHash(codeHash);
        getSmartcodeTx().setCodeHash(codeHash);
    }
    public CoinMgr getCoinManager() {
        return coinManager;
    }

    public WalletMgr getWalletMgr() {
        return walletMgr;
    }

    public ConnectMgr getConnectMgr() {
        return connManager;
    }

    public void setBlockChainConfig(String url, String token) {
        this.connManager = new ConnectMgr(url, token);
    }
    public void setBlockChainConfig(String url) {
        this.connManager = new ConnectMgr(url);
    }
    public void openWalletFile(String path) {
        if (coinManager == null) {
            this.coinManager = new CoinMgr();
        }
        this.walletMgr = new WalletMgr(path);
    }

    public void openWalletFile(String path,String password) {
        if (coinManager == null) {
            this.coinManager = new CoinMgr();
        }
        this.walletMgr = new WalletMgr(path,password);
    }
    public void setAlgrithem(String alg) {
        walletMgr.setAlgrithem(alg);
    }


    public void setRestToken(String accessToken) {
        connManager.updateToken(accessToken);
    }

}
