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

import com.github.ontio.account.Acct;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.transaction.OntAssetTx;
import com.github.ontio.sdk.transaction.OntIdTx;
import com.github.ontio.sdk.transaction.SmartcodeTx;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 */
public class OntSdk {
    private WalletMgr walletMgr;
    private ConnectMgr connManager;
    private OntIdTx ontIdTx = null;
    private SmartcodeTx smartcodeTx = null;
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

    public String signTx(Transaction tx, Acct[][] accounts) {
        Sig[] sigs = new Sig[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            sigs[i] = new Sig();
            sigs[i].pubKeys = new ECPoint[accounts[i].length];
            sigs[i].sigData = new byte[accounts[i].length][];
            for (int j = 0; j < accounts[i].length; j++) {
                sigs[i].M++;
                System.out.println(accounts[i].length);
                byte[] signature = tx.sign(accounts[i][j], getWalletMgr().getAlgrithem());
                sigs[i].pubKeys[j] = accounts[i][j].publicKey;
                sigs[i].sigData[j] = signature;
            }
        }
        tx.sigs = sigs;
        return tx.toHexString();
    }

    public String signTx(Transaction tx, Acct[][] accounts, int[] M) throws SDKException {
        if (M.length != accounts.length) {
            throw new SDKException("M Error");
        }
        Sig[] sigs = new Sig[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            sigs[i] = new Sig();
            sigs[i].pubKeys = new ECPoint[accounts[i].length];
            sigs[i].sigData = new byte[accounts[i].length][];
            if (M[i] > accounts[i].length || M[i] < 0) {
                throw new SDKException("M Error");
            }
            sigs[i].M = M[i];
            for (int j = 0; j < accounts[i].length; j++) {
                byte[] signature = tx.sign(accounts[i][j], getWalletMgr().getAlgrithem());
                sigs[i].pubKeys[j] = accounts[i][j].publicKey;
                sigs[i].sigData[j] = signature;
            }
        }
        tx.sigs = sigs;
        return tx.toHexString();
    }
}
