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

import com.github.ontio.account.Account;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.manager.OntAssetTx;
import com.github.ontio.sdk.manager.OntIdTx;
import com.github.ontio.sdk.manager.SmartcodeTx;
import org.bouncycastle.math.ec.ECPoint;

/**
 * Ont Sdk
 */
public class OntSdk {
    private WalletMgr walletMgr;
    private ConnectMgr connManager;
    private OntIdTx ontIdTx = null;
    private SmartcodeTx smartcodeTx = null;
    private OntAssetTx ontAssetTx = null;
    private static OntSdk instance = null;
    public KeyType keyType = KeyType.ECDSA;
    public Object[] curveParameterSpec = new Object[]{"P-256"};
    public SignatureScheme signatureScheme = SignatureScheme.SHA256WITHECDSA;

    public static synchronized OntSdk getInstance(){
        if(instance == null){
            instance = new OntSdk();
        }
        return instance;
    }
    private OntSdk(){
    }

    /**
     * OntId
     * @return instance
     */
    public OntIdTx getOntIdTx() {
        if(ontIdTx == null){
            getSmartcodeTx();
            ontIdTx = new OntIdTx(getInstance());
        }
        return ontIdTx;
    }

    /**
     *  Smartcode Tx
     * @return instance
     */
    public SmartcodeTx getSmartcodeTx() {
        if(smartcodeTx == null){
            smartcodeTx = new SmartcodeTx(getInstance());
        }
        return smartcodeTx;
    }

    /**
     *  get OntAsset Tx
     * @return instance
     */
    public OntAssetTx getOntAssetTx() {
        if(ontAssetTx == null){
            ontAssetTx = new OntAssetTx(getInstance());
        }
        return ontAssetTx;
    }

    /**
     * get Wallet Mgr
     * @return
     */
    public WalletMgr getWalletMgr() {
        return walletMgr;
    }

    /**
     * get Connect Mgr
     * @return
     */
    public ConnectMgr getConnectMgr() {
        return connManager;
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
     * @param codeAddress
     */
    public void setCodeAddress(String codeAddress){
        getOntIdTx().setCodeAddress(codeAddress);
        getSmartcodeTx().setCodeAddress(codeAddress);
    }

    /**
     *
     * @param scheme
     */
    public void setSignatureScheme(SignatureScheme scheme) {
        walletMgr.setSignatureScheme(scheme);
    }

    /**
     *
     * @param url
     */
    public void setRpcConnection(String url) {
        this.connManager = new ConnectMgr(url, true);
    }

    /**
     *
     * @param url
     */
    public void setRestfulConnection(String url) {
        this.connManager = new ConnectMgr(url);
    }

    /**
     *
     * @param path
     */
    public void openWalletFile(String path) {

        this.walletMgr = new WalletMgr(path,keyType,curveParameterSpec);
        setSignatureScheme(signatureScheme);
    }



    /**
     * sign tx
     * @param tx
     * @param accounts
     * @return
     */
    public Transaction signTx(Transaction tx, Account[][] accounts) throws Exception{
        Sig[] sigs = new Sig[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            sigs[i] = new Sig();
            sigs[i].pubKeys = new byte[accounts[i].length][];
            sigs[i].sigData = new byte[accounts[i].length][];
            for (int j = 0; j < accounts[i].length; j++) {
                sigs[i].M++;
                byte[] signature = tx.sign(accounts[i][j], getWalletMgr().getSignatureScheme());
                sigs[i].pubKeys[j] = accounts[i][j].serializePublicKey();
                sigs[i].sigData[j] = signature;
            }
        }
        tx.sigs = sigs;
        return tx;
    }

    /**
     *  signTx
     * @param tx
     * @param accounts
     * @param M
     * @return
     * @throws SDKException
     */
    public Transaction signTx(Transaction tx, Account[][] accounts, int[] M) throws SDKException {
        if (M.length != accounts.length) {
            throw new SDKException("M Error");
        }
        for (int i = 0; i < tx.sigs.length; i++) {
            if (M[i] > tx.sigs[i].pubKeys.length || M[i] < 0) {
                throw new SDKException("M Error");
            }
            tx.sigs[i].M = M[i];
        }
        return tx;
    }
}
