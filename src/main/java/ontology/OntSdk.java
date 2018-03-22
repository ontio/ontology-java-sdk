package ontology;

import ontology.sdk.manager.ConnectMgr;
import ontology.sdk.manager.WalletMgr;


public class OntSdk {
    //oep管理器
    private WalletMgr walletMgr;
    //连接管理器
    private ConnectMgr connManager;

    //OntId 交易
    private ontology.sdk.transaction.OntIdTx ontIdTx = null;

    //智能合约交易
    private ontology.sdk.transaction.SmartcodeTx smartcodeTx = null;
    //ont资产
    private ontology.sdk.transaction.OntAssetTx ontAssetTx = null;

    public static OntSdk instance = null;
    public static OntSdk getInstance(){
        if(instance == null){
            instance = new OntSdk();
        }
        return instance;
    }


    public ontology.sdk.transaction.OntIdTx getOntIdTx() {
        if(ontIdTx == null){
            getSmartcodeTx();
            ontIdTx = new ontology.sdk.transaction.OntIdTx(getInstance());
        }
        return ontIdTx;
    }


    public ontology.sdk.transaction.SmartcodeTx getSmartcodeTx() {
        if(smartcodeTx == null){
            smartcodeTx = new ontology.sdk.transaction.SmartcodeTx(getInstance());
        }
        return smartcodeTx;
    }
    public ontology.sdk.transaction.OntAssetTx getOntAssetTx() {
        if(ontAssetTx == null){
            ontAssetTx = new ontology.sdk.transaction.OntAssetTx(getInstance());
        }
        return ontAssetTx;
    }
    public void setWsSessionId(String sessionId){
        getOntIdTx().setWsSessionId(sessionId);
        getSmartcodeTx().setWsSessionId(sessionId);
    }
    public void setCodeHash(String codeHash){
        getOntIdTx().setCodeHash(codeHash);
        getSmartcodeTx().setCodeHash(codeHash);
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
        this.walletMgr = new WalletMgr(path);
    }

    public void openWalletFile(String path,String password) {
        this.walletMgr = new WalletMgr(path,password);
    }
    public void setAlgrithem(String alg) {
        walletMgr.setAlgrithem(alg);
    }


    public void setRestToken(String accessToken) {
        connManager.updateToken(accessToken);
    }

}
