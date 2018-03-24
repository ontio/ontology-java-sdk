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

package com.github.ontio;

import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.transaction.OntAssetTx;
import com.github.ontio.sdk.transaction.OntIdTx;
import com.github.ontio.sdk.transaction.SmartcodeTx;
import com.github.ontio.sdk.transaction.*;

/**
 *
 */
public class OntSdk {
    //wallet管理器
    private WalletMgr walletMgr;
    //连接管理器
    private ConnectMgr connManager;

    //OntId 交易
    private OntIdTx ontIdTx = null;

    //智能合约交易
    private SmartcodeTx smartcodeTx = null;
    //ont资产
    private OntAssetTx ontAssetTx = null;

    private static OntSdk instance = null;
    public static OntSdk getInstance(){
        if(instance == null){
            instance = new OntSdk();
        }
        return instance;
    }
    private OntSdk(){

    }

    public OntIdTx getOntIdTx() {
        if(ontIdTx == null){
            getSmartcodeTx();
            ontIdTx = new OntIdTx(getInstance());
        }
        return ontIdTx;
    }


    public SmartcodeTx getSmartcodeTx() {
        if(smartcodeTx == null){
            smartcodeTx = new SmartcodeTx(getInstance());
        }
        return smartcodeTx;
    }
    public OntAssetTx getOntAssetTx() {
        if(ontAssetTx == null){
            ontAssetTx = new OntAssetTx(getInstance());
        }
        return ontAssetTx;
    }

    /**
     *
     * @param sessionId
     */
    public void setWsSessionId(String sessionId){
        getOntIdTx().setWsSessionId(sessionId);
        getSmartcodeTx().setWsSessionId(sessionId);
    }

    /**
     *
     * @param codeHash
     */
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

    public void setRpcConnection(String url) {
        this.connManager = new ConnectMgr(url, true);
    }
    public void setRestfulConnection(String url) {
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


}
