package com.github.ontio.sidechain;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.program.Program;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sidechain.core.transaction.InvokeCode;
import com.github.ontio.sidechain.core.transaction.Transaction;
import com.github.ontio.sidechain.smartcontract.governance.Governance;
import com.github.ontio.sidechain.smartcontract.ongx.OngX;
import com.github.ontio.smartcontract.Vm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class SidechainVm {
    private Governance governance;
    private OngX ongX;
    private OntSdk sdk;
    public SidechainVm(OntSdk sdk){
        this.sdk = sdk;
    }

    public Governance governance() {
        if (governance == null){
            governance = new Governance(sdk);
        }
        return governance;
    }

    public OngX ongX() {
        if (ongX == null){
            ongX = new OngX(sdk);
        }
        return ongX;
    }

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

    public Transaction addSign(Transaction tx, String addr, String password, byte[] salt) throws Exception {
        return addSign(tx,sdk.getWalletMgr().getAccount(addr,password,salt));
    }
    public Transaction addSign(Transaction tx, Account acct) throws Exception {
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
    public InvokeCode makeInvokeCodeTransaction(byte[] params, String payer, long gaslimit, long gasprice) throws SDKException {

        InvokeCode tx = new InvokeCode();
        tx.sideChainId = sdk.sideChainId;
        tx.attributes = new Attribute[0];
        tx.nonce = new Random().nextInt();
        tx.code = params;
        tx.gasLimit = gaslimit;
        tx.gasPrice = gasprice;
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }
        return tx;
    }
    public Transaction buildNativeParams(Address codeAddr, String initMethod, byte[] args, String payer, long gaslimit, long gasprice) throws SDKException {
        ScriptBuilder sb = new ScriptBuilder();
        if(args.length >0) {
            sb.add(args);
        }
        sb.emitPushByteArray(initMethod.getBytes());
        sb.emitPushByteArray(codeAddr.toArray());
        sb.emitPushInteger(BigInteger.valueOf(0));
        sb.emit(ScriptOp.OP_SYSCALL);
        sb.emitPushByteArray(Vm.NATIVE_INVOKE_NAME.getBytes());
        Transaction tx = makeInvokeCodeTransaction(sb.toArray(),payer,gaslimit,gasprice);
        return tx;
    }
}
