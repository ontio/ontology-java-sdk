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

import com.alibaba.fastjson.JSON;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.program.Program;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.*;
import com.github.ontio.sidechain.SidechainVm;
import com.github.ontio.smartcontract.NativeVm;
import com.github.ontio.smartcontract.NeoVm;
import com.github.ontio.smartcontract.Vm;
import com.github.ontio.smartcontract.WasmVm;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Ont Sdk
 */
public class OntSdk {
    private WalletMgr walletMgr;
    private ConnectMgr connRpc;
    private ConnectMgr connRestful;
    private ConnectMgr connWebSocket;
    private ConnectMgr connDefault;
    private  ConnectMgr sideChainConnectMgr;
    private SidechainVm sidechainVm;

    private Vm vm = null;
    private NativeVm nativevm = null;
    private NeoVm neovm = null;
    private WasmVm wasmvm = null;
    private SignServer signServer = null;


    private static OntSdk instance = null;
    public SignatureScheme defaultSignScheme = SignatureScheme.SHA256WITHECDSA;
    public long DEFAULT_GAS_LIMIT = 20000;
    public long DEFAULT_DEPLOY_GAS_LIMIT = 30000000;
    public long sideChainId;
    public static synchronized OntSdk getInstance(){
        if(instance == null){
            instance = new OntSdk();
        }
        return instance;
    }
    private OntSdk(){
    }
    public SignServer getSignServer() throws SDKException{
        if(signServer == null){
            throw new SDKException(ErrorCode.OtherError("signServer null"));
        }
        return signServer;
    }
    public NativeVm nativevm() throws SDKException{
        if(nativevm == null){
            vm();
            nativevm = new NativeVm(getInstance());
        }
        return nativevm;
    }
    public NeoVm neovm() {
        if(neovm == null){
            vm();
            neovm = new NeoVm(getInstance());
        }
        return neovm;
    }
    public WasmVm wasmvm() {
        if(wasmvm == null){
            vm();
            wasmvm = new WasmVm(getInstance());
        }
        return wasmvm;
    }

    public SidechainVm sidechainVm() {
        if (sidechainVm == null) {
            vm();
            sidechainVm = new SidechainVm(getInstance());
        }
        return sidechainVm;
    }

    public Vm vm() {
        if(vm == null){
            vm = new Vm(getInstance());
        }
        return vm;
    }
    public ConnectMgr getRpc() throws SDKException{
        if(connRpc == null){
            throw new SDKException(ErrorCode.ConnRestfulNotInit);
        }
        return connRpc;
    }

    public ConnectMgr getRestful() throws SDKException{
        if(connRestful == null){
            throw new SDKException(ErrorCode.ConnRestfulNotInit);
        }
        return connRestful;
    }
    public ConnectMgr getConnect(){
        if(connDefault != null){
            return connDefault;
        }
        if(connRpc != null){
            return  connRpc;
        }
        if(connRestful != null){
            return  connRestful;
        }
        if(connWebSocket != null){
            return  connWebSocket;
        }
        return null;
    }

    public ConnectMgr getSideChainConnectMgr() {
        return sideChainConnectMgr;
    }

    public void setSideChainRpc(String url) {
        this.sideChainConnectMgr = new ConnectMgr(url, "rpc");
    }
    public void setSideChainId(long sideChainId) {
        this.sideChainId = sideChainId;
    }
    public void setSideChainRest(String url) {
        this.sideChainConnectMgr = new ConnectMgr(url, "restful");
    }
    public void setSideChainWebsocket(String url,Object lock) {
        this.sideChainConnectMgr = new ConnectMgr(url, "websocket", lock);
    }
    public void setDefaultConnect(ConnectMgr conn){
        connDefault = conn;
    }
    public void setConnectTestNet(){
        try {
            String rpcUrl = "http://polaris1.ont.io:20336";
            getInstance().setRpc(rpcUrl);
            connDefault = getInstance().getRpc();
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
    public void setConnectMainNet(){
        try {
            String rpcUrl = "http://dappnode1.ont.io:20336";
            getInstance().setRpc(rpcUrl);
            connDefault = getInstance().getRpc();
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
    public ConnectMgr getWebSocket() throws SDKException{
        if(connWebSocket == null){
            throw new SDKException(ErrorCode.WebsocketNotInit);
        }
        return connWebSocket;
    }


    /**
     * get Wallet Mgr
     * @return
     */
    public WalletMgr getWalletMgr() {
        return walletMgr;
    }


    /**
     *
     * @param scheme
     */
    public void setSignatureScheme(SignatureScheme scheme) {
        defaultSignScheme = scheme;
        walletMgr.setSignatureScheme(scheme);
    }

    public void setSignServer(String url) throws Exception{
        this.signServer = new SignServer(url);
    }
    public void setRpc(String url) {
        this.connRpc = new ConnectMgr(url, "rpc");
    }

    public void setRestful(String url) {
        this.connRestful = new ConnectMgr(url,"restful");
    }

    public void setWesocket(String url,Object lock) {
        connWebSocket = new ConnectMgr(url,"websocket",lock);
    }
    /**
     *
     * @param path
     */
    public void openWalletFile(String path) {

        try {
            this.walletMgr = new WalletMgr(path,defaultSignScheme);
            setSignatureScheme(defaultSignScheme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param tx
     * @param addr
     * @param password
     * @return
     * @throws Exception
     */
    public Transaction addSign(Transaction tx,String addr,String password,byte[] salt) throws Exception {
        return addSign(tx,getWalletMgr().getAccount(addr,password,salt));
    }
    public Transaction addSign(Transaction tx,Account acct) throws Exception {
        if(tx.sigs == null){
            tx.sigs = new Sig[0];
        } else {
            if (tx.sigs.length >= Common.TX_MAX_SIG_SIZE) {
                throw new SDKException(ErrorCode.ParamErr("the number of transaction signatures should not be over 16"));
            }
        }
        Sig[] sigs = new Sig[tx.sigs.length + 1];
        for(int i= 0; i< tx.sigs.length; i++){
            sigs[i] = tx.sigs[i];
        }
        sigs[tx.sigs.length] = new Sig();
        sigs[tx.sigs.length].M = 1;
        sigs[tx.sigs.length].pubKeys = new byte[1][];
        sigs[tx.sigs.length].sigData = new byte[1][];
        sigs[tx.sigs.length].pubKeys[0] = acct.serializePublicKey();
        sigs[tx.sigs.length].sigData[0] = tx.sign(acct,acct.getSignatureScheme());
        tx.sigs = sigs;
        return tx;
    }

    /**
     *
     * @param tx
     * @param M
     * @param pubKeys
     * @param acct
     * @return
     * @throws Exception
     */
    public Transaction addMultiSign(Transaction tx,int M,byte[][] pubKeys, Account acct) throws Exception {
        addMultiSign(tx,M,pubKeys,tx.sign(acct, acct.getSignatureScheme()));
        return tx;
    }
    public Transaction addMultiSign(Transaction tx,int M,byte[][] pubKeys, byte[] signatureData) throws Exception {
        pubKeys = Program.sortPublicKeys(pubKeys);
        if (tx.sigs == null) {
            tx.sigs = new Sig[0];
        } else {
            if (tx.sigs.length  > Common.TX_MAX_SIG_SIZE || M > pubKeys.length || M <= 0 || signatureData == null || pubKeys == null) {
                throw new SDKException(ErrorCode.ParamError);
            }
            for (int i = 0; i < tx.sigs.length; i++) {
                if(Arrays.deepEquals(tx.sigs[i].pubKeys,pubKeys)){
                    if (tx.sigs[i].sigData.length + 1 > pubKeys.length) {
                        throw new SDKException(ErrorCode.ParamErr("too more sigData"));
                    }
                    if(tx.sigs[i].M != M){
                        throw new SDKException(ErrorCode.ParamErr("M error"));
                    }
                    int len = tx.sigs[i].sigData.length;
                    byte[][] sigData = new byte[len+1][];
                    for (int j = 0; j < tx.sigs[i].sigData.length; j++) {
                        sigData[j] = tx.sigs[i].sigData[j];
                    }
                    sigData[len] = signatureData;
                    tx.sigs[i].sigData = sigData;
                    return tx;
                }
            }
        }
        Sig[] sigs = new Sig[tx.sigs.length + 1];
        for (int i = 0; i < tx.sigs.length; i++) {
            sigs[i] = tx.sigs[i];
        }
        sigs[tx.sigs.length] = new Sig();
        sigs[tx.sigs.length].M = M;
        sigs[tx.sigs.length].pubKeys = pubKeys;
        sigs[tx.sigs.length].sigData = new byte[1][];
        sigs[tx.sigs.length].sigData[0] = signatureData;

        tx.sigs = sigs;
        return tx;
    }
    public Transaction signTx(Transaction tx, String addressOrOntId, String password,byte[] salt) throws Exception{
        signTx(tx, new Account[][]{{getWalletMgr().getAccount(addressOrOntId, password,salt)}});
        return tx;
    }
    /**
     * sign tx
     * @param tx
     * @param accounts
     * @return
     */
    public Transaction signTx(Transaction tx, Account[][] accounts) throws Exception{
        if (accounts.length > Common.TX_MAX_SIG_SIZE) {
            throw new SDKException(ErrorCode.ParamErr("the number of transaction signatures should not be over 16"));
        }
        Sig[] sigs = new Sig[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            sigs[i] = new Sig();
            sigs[i].pubKeys = new byte[accounts[i].length][];
            sigs[i].sigData = new byte[accounts[i].length][];
            for (int j = 0; j < accounts[i].length; j++) {
                sigs[i].M++;
                byte[] signature = tx.sign(accounts[i][j], accounts[i][j].getSignatureScheme());
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
    public Transaction signTx(Transaction tx, Account[][] accounts, int[] M) throws Exception {
        if (accounts.length > Common.TX_MAX_SIG_SIZE) {
            throw new SDKException(ErrorCode.ParamErr("the number of transaction signatures should not be over 16"));
        }
        if (M.length != accounts.length) {
            throw new SDKException(ErrorCode.ParamError);
        }
        tx = signTx(tx,accounts);
        for (int i = 0; i < tx.sigs.length; i++) {
            if (M[i] > tx.sigs[i].pubKeys.length || M[i] < 0) {
                throw new SDKException(ErrorCode.ParamError);
            }
            tx.sigs[i].M = M[i];
        }
        return tx;
    }

    public byte[] signatureData(com.github.ontio.account.Account acct, byte[] data) throws SDKException {
        DataSignature sign = null;
        try {
            data = Digest.sha256(Digest.sha256(data));
            sign = new DataSignature(defaultSignScheme, acct, data);
            return sign.signature();
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }

    public boolean verifySignature(byte[] pubkey, byte[] data, byte[] signature) throws SDKException {
        DataSignature sign = null;
        try {
            sign = new DataSignature();
            data = Digest.sha256(Digest.sha256(data));
            return sign.verifySignature(new com.github.ontio.account.Account(false, pubkey), data, signature);
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }
    public boolean verifyTransaction(Transaction tx) {
        try {
            boolean result = true;
            for (int i = 0; i < tx.sigs.length; i++) {
                if (tx.sigs[i].M == 1) {
                    if (tx.sigs[i].pubKeys.length != 1 || tx.sigs[i].sigData.length != 1) {
                        throw new SDKException(ErrorCode.OtherError("index" + i + "pubKeys or sigData number != 1"));
                    }
                    Account account = new Account(false, tx.sigs[i].pubKeys[0]);
                    boolean verify = account.verifySignature(Digest.hash256(tx.getHashData()), tx.sigs[i].sigData[0]);
                    if (!verify) {
                        return false;
                    }
                } else if (tx.sigs[i].M > 1) {
                    int m = 0;
                    for (int j = 0; j < tx.sigs[i].pubKeys.length; j++) {
                        Account account = new Account(false, tx.sigs[i].pubKeys[j]);
                        for (int k = 0; k < tx.sigs[i].sigData.length; k++) {
                            boolean verify = account.verifySignature(Digest.hash256(tx.getHashData()), tx.sigs[i].sigData[k]);
                            if (verify) {
                                m++;
                                break;
                            }
                        }
                    }
                    if (m < tx.sigs[i].M) {
                        return false;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private void buildMap(Map map,Object ele){
        try {
            for(Map.Entry<String, Object> e:((Map<String,Object>) ele).entrySet()) {
                Object tmp = e.getValue();
                if(tmp instanceof String){
                    String pre = ((String) tmp).substring(0,10);
                    if(pre.contains("String")) {
                        String data = ((String) tmp).replace("String:","");
                        e.setValue(data.getBytes());
                    }else if(pre.contains("ByteArray")) {
                        String data = ((String) tmp).replace("ByteArray:","");
                        e.setValue(Helper.hexToBytes(data));
                    }else if(pre.contains("Long")) {
                        String data = ((String) tmp).replace("Long:","");
                        e.setValue(data);
                    }else if(pre.contains("Address")) {
                        String data = ((String) tmp).replace("Address:","");
                        e.setValue(Address.decodeBase58(data).toArray());
                    } else {
                        throw new Exception(ErrorCode.OtherError("String type data error: "+ e));
                    }
                }else if(tmp instanceof Map){
                    Map data = new HashMap();
                    buildMap(data, tmp);
                    e.setValue(data);
                }
                map.put(e.getKey(),e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void buildArgs(List args,Object ele){
        try {
            if(ele instanceof Boolean){
                args.add(ele);
            }else if(ele instanceof Long){
                args.add(ele);
            } else if(ele instanceof Integer){
                args.add(ele);
            } else if(ele instanceof Map){
                Map map = new HashMap();
                buildMap(map,ele);
                args.add(map);
            } else if(ele instanceof String){
                String pre = ((String) ele).substring(0,10);
                if(pre.contains("String")) {
                    String data = ((String) ele).replace("String:","");
                    args.add(data.getBytes());
                }else if(pre.contains("ByteArray")) {
                    String data = ((String) ele).replace("ByteArray:","");
                    args.add(Helper.hexToBytes(data));
                }else if(pre.contains("Long")) {
                    String data = ((String) ele).replace("Long:","");
                    args.add(new BigInteger(data).longValue());
                }else if(pre.contains("Address")) {
                    String data = ((String) ele).replace("Address:","");
                    args.add(Address.decodeBase58(data).toArray());
                }else {
                    throw new Exception(ErrorCode.OtherError("String type data error: "+ele));
                }
            } else if(ele instanceof List){
                List tmp = new ArrayList();
                for (int i = 0; i < ((List)ele).size(); i++) {
                    buildArgs(tmp, ((List) ele).get(i));
                }
                args.add(tmp);
            } else{
                throw new Exception(ErrorCode.OtherError("type not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List[] buildInvokeFunctionByJson(String configStr) {
        try {
            Map config = (Map) JSON.parseObject(configStr);
            List functions = ((List)config.get("functions"));
            List[] paramLists = new List[functions.size()];
            for(int i =0;i < functions.size();i++) {
                Map func = (Map)functions.get(i);
                String operation = (String) func.get("operation");
                List args = (List) func.get("args");
                List paramList = new ArrayList<>();
                paramList.add(operation.getBytes());
                List args2 = new ArrayList();
                for (int j = 0; j < args.size(); j++) {
                    Object ele = ((Map) args.get(j)).get("value");
                    buildArgs(args2, ele);
                }
                paramList.add(args2);
                paramLists[i] = paramList;
            }
            return paramLists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Transaction makeNativeTransaction(String contractHash, List paramList, String payer, long gasLimit, long gasPrice) {
        try {
            String ONT = "0100000000000000000000000000000000000000";
            String ONG = "0200000000000000000000000000000000000000";
            if (contractHash.equals(ONT) || contractHash.equals(ONG)) {
                List list = new ArrayList();
                Struct struct = new Struct();
                String method = new String((byte[]) paramList.get(0));
                struct.list = (List) paramList.get(1);
                list.add(new Struct().list);
                byte[] args = NativeBuildParams.createCodeParamsScript(list);
                return vm().buildNativeParams(new Address(Helper.hexToBytes(Helper.reverse(contractHash))), method, args, payer, gasLimit, gasPrice);
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transaction[] makeTransactionByJson(String str) {
        Map map = JSON.parseObject(str);
        Map config = null;
        try {
            String action = ((String) map.get("action"));
            if (!action.equals("invoke") && !action.equals("invokeRead") && !action.equals("invokePasswordFree")) {
                throw new Exception(ErrorCode.OtherError("not found action is invoke or invokeRead"));
            }
            config = (Map) ((Map) map.get("params")).get("invokeConfig");
            String payer = (String) config.get("payer");
            long gasLimit = (int) config.get("gasLimit");
            long gasPrice = (int) config.get("gasPrice");
            String contractHash = (String) config.get("contractHash");

            List[] paramList = buildInvokeFunctionByJson(JSON.toJSONString(config));
            Transaction[] txs = new Transaction[paramList.length];
            for(int i=0;i< paramList.length;i++) {
                if(contractHash.contains("00000000000000000000000000000000000000")){
                    txs[i] = makeNativeTransaction(contractHash,paramList[i],payer, gasLimit, gasPrice);
                } else {
                    byte[] params = BuildParams.createCodeParamsScript(paramList[i]);
                    txs[i] = vm().makeInvokeCodeTransaction(Helper.reverse(contractHash), null, params, payer, gasLimit, gasPrice);
                }
            }
            return txs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map parseTransaction(String txhexstr) throws SDKException {
        Map map = new HashMap();
        try {
            Transaction tx = Transaction.deserializeFrom(Helper.hexToBytes(txhexstr));
            if (tx instanceof DeployCode) {
                map.put("txType", "deploy");
                map.put("author", ((DeployCode) tx).author);
                map.put("email", ((DeployCode) tx).email);
                map.put("version", ((DeployCode) tx).version);
                map.put("description", ((DeployCode) tx).description);
                map.put("name", ((DeployCode) tx).name);
            } else if (tx instanceof InvokeCode) {
                map.put("txType", "invoke");
                byte[] code = ((InvokeCode) tx).code;
                String codeHexStr = Helper.toHexString(code);
                if (codeHexStr.length() > 44 && codeHexStr.substring(codeHexStr.length() - 44, codeHexStr.length()).equals(Helper.toHexString("Ontology.Native.Invoke".getBytes()))) {
                    if (codeHexStr.substring(codeHexStr.length() - 92 - 16, codeHexStr.length() - 92).equals(Helper.toHexString("transfer".getBytes()))) {
                        map.put("method", "transfer");
                        map.put("from", Address.parse(codeHexStr.substring(8, 48)).toBase58());
                        map.put("to", Address.parse(codeHexStr.substring(56, 96)).toBase58());
                        map.put("amount", new BigInteger("00"));
                        if (codeHexStr.substring(102, 103).equals("5")) {
                            map.put("amount", code[51] - 0x50);
                        } else {
                            map.put("amount", Helper.BigIntFromNeoBytes(Helper.hexToBytes(codeHexStr.substring(104, 104 + code[51] * 2))));
                        }
                        if (codeHexStr.substring(codeHexStr.length() - 50 - 40, codeHexStr.length() - 50).equals(this.nativevm().ont().getContractAddress())) {
                            map.put("asset", "ont");
                        } else if (codeHexStr.substring(codeHexStr.length() - 50 - 40, codeHexStr.length() - 50).equals(this.nativevm().ong().getContractAddress())) {
                            map.put("asset", "ong");
                            map.put("amount", ((BigInteger) map.get("amount")).doubleValue() / 1000000000L);
                        }
                    } else if (codeHexStr.substring(codeHexStr.length() - 92 - 24, codeHexStr.length() - 92).equals(Helper.toHexString("transferFrom".getBytes()))) {
                        map.put("method", "transferFrom");
                        map.put("from", Address.parse(codeHexStr.substring(8, 48)).toBase58());
                        map.put("to", Address.parse(codeHexStr.substring(56, 96)).toBase58());
                        map.put("amount", new BigInteger("00"));
                        if (codeHexStr.substring(102, 103).equals("5")) {
                            map.put("amount", code[51] - 0x50);
                        } else {
                            map.put("amount", Helper.BigIntFromNeoBytes(Helper.hexToBytes(codeHexStr.substring(104, 104 + code[51] * 2))));
                        }
                        if (codeHexStr.substring(codeHexStr.length() - 50 - 40, codeHexStr.length() - 50).equals(this.nativevm().ont().getContractAddress())) {
                            map.put("asset", "ont");
                        } else if (codeHexStr.substring(codeHexStr.length() - 50 - 40, codeHexStr.length() - 50).equals(this.nativevm().ong().getContractAddress())) {
                            map.put("asset", "ong");
                            map.put("amount", ((BigInteger) map.get("amount")).doubleValue() / 1000000000L);
                        }
                    }
                }
            } else {
                throw new SDKException(ErrorCode.OtherError("tx type error"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
