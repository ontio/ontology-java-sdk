package ontology.sdk.transaction;

import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.core.*;
import ontology.core.code.FunctionCode;
import ontology.core.contract.Contract;
import ontology.core.contract.ContractParameterType;
import ontology.core.scripts.Program;
import ontology.core.scripts.ScriptBuilder;
import ontology.OntSdk;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.SDKException;
import ontology.sdk.info.abi.AbiFunction;
import ontology.sdk.info.abi.Parameter;
import ontology.sdk.info.account.AccountInfo;
import com.alibaba.fastjson.JSON;
import org.bouncycastle.math.ec.ECPoint;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zx on 2018/1/9.
 */
public class SmartcodeTx {
    public OntSdk sdk;
    private String codeHash = null;
    private String wsSessionId = "";
    public void setCodeHash(String codeHash){
        this.codeHash = codeHash.replace("0x","");
    }
    public SmartcodeTx(OntSdk sdk) {
        this.sdk =sdk;
    }
    public void setWsSessionId(String sessionId){
        if(!this.wsSessionId.equals(sessionId)) {
            this.wsSessionId = sessionId;
        }
    }
    public String getWsSessionId(){
        return wsSessionId;
    }
    public String invokeTransaction(String ontid,String password,AbiFunction abiFunction) throws Exception {
        return (String)invokeTransaction(false,ontid,password,abiFunction);
    }
    public Object invokeTransactionPreExec(String ontid,String password,AbiFunction abiFunction) throws Exception {
        return invokeTransaction(true,ontid,password,abiFunction);
    }
    public Object invokeTransaction(boolean preExec,String ontid,String password,AbiFunction abiFunction) throws Exception {
        if(codeHash == null){
            throw new SDKException(Error.getDescArgError("null codeHash"));
        }
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid,password);
        List list = new ArrayList<Object>();
        list.add(abiFunction.getName().getBytes());
        List tmp = new ArrayList<Object>();
        for(Parameter obj:abiFunction.getParameters()){
            if("ByteArray".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(),byte[].class));
            }else if("String".equals(obj.getType())){
                tmp.add(obj.getValue());
            }else if ("Boolean".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(),boolean.class));
            } else if ("Integer".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(),int.class));
            } else if ("Array".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(),Array.class));
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(),Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else{
                throw new SDKException(Error.getDescArgError("type error"));
            }
        }
        list.add(tmp);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(sdk.getSmartcodeTx().createCodeParamsScript(list),codeHash,info.address,info.pubkey);
        String txHex = sdk.getWalletMgr().signatureData(password,tx);
        boolean b = false;
        if(preExec){
            return sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
        }else {
            b = sdk.getConnectMgr().sendRawTransaction(wsSessionId, txHex);
        }
        if(!b){
            throw new SDKException(Error.getDescArgError("sendRawTransaction error"));
        }
        return tx.hash().toString();
    }
    public String DeployCodeTransaction(String codeHexStr,boolean needStorage, String name, String codeVersion, String author, String email, String desp, String returnType) throws Exception {
        //AccountInfo info = sdk.getOepMgr().getAccountInfo(ontid,password);
        //ContractParameterType.valueOf("Boolean")
        Transaction tx = makeDeployCodeTransaction(codeHexStr, needStorage, name ,codeVersion, author, email, desp, ContractParameterType.valueOf(returnType));

        String txHex = sdk.getWalletMgr().signatureData(tx);
        boolean b = sdk.getConnectMgr().sendRawTransaction(wsSessionId,txHex);
        if(!b){
            throw new SDKException(Error.getDescArgError("sendRawTransaction error"));
        }
        return tx.hash().toString();
    }
    public byte[] createCodeParamsScript(ScriptBuilder sb, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof BigInteger) {
                    sb.push((BigInteger) val);
                } else if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.push(new BigInteger(String.valueOf(val)));
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(sb, tmp);
                    sb.push(new BigInteger(String.valueOf(tmp.size())));
                    sb.pushPack();

                }else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }

    public byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof BigInteger) {
                    sb.push((BigInteger) val);
                } else if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.push(new BigInteger(String.valueOf(val)));
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(sb, tmp);
                    sb.push(new BigInteger(String.valueOf(tmp.size())));
                    sb.pushPack();
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }
    //smartcode
    public DeployCodeTransaction makeDeployCodeTransaction(String codeStr,  boolean needStorage, String name, String codeVersion, String author, String email, String desp, ContractParameterType returnType) throws SDKException {
        DeployCodeTransaction tx = new DeployCodeTransaction();
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();//Common.generateKey64Bit();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.code = new FunctionCode(Helper.hexToBytes(codeStr), new ContractParameterType[]{ContractParameterType.ByteArray, ContractParameterType.ByteArray}, returnType);
        tx.codeVersion = codeVersion;
        tx.vmType = (byte)0;
        tx.needStorage = needStorage;
        tx.name = name;
        tx.author = author;
        tx.email = email;
        tx.description = desp;
        //tx.pubkey = sdk.getOntto().getAccount(addr).publicKey;
        return tx;
    }

    public InvokeCodeTransaction makeInvokeCodeTransaction(String addr,String password,String codeHash,byte[] paramsHexStr) throws SDKException {
        InvokeCodeTransaction tx = new InvokeCodeTransaction(sdk.getWalletMgr().getAccount(addr,password).publicKey);
        tx.attributes = new TransactionAttribute[2];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();//Common.generateKey64Bit();
        tx.attributes[1] = new TransactionAttribute();
        tx.attributes[1].usage = TransactionAttributeUsage.Script;
        tx.attributes[1].data = Program.toScriptHash(Contract.createSignatureRedeemScript(sdk.getWalletMgr().getAccount(addr,password).publicKey)).toArray();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.code = paramsHexStr;
        tx.codeHash = new UInt160(Helper.hexToBytes(codeHash));
        return tx;
    }
    public InvokeCodeTransaction makeInvokeCodeTransaction(byte[] paramsHexStr, String codeHash, String addr,String pubkey) throws SDKException {
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(pubkey);
        InvokeCodeTransaction tx = new InvokeCodeTransaction(publicKey);
        tx.attributes = new TransactionAttribute[2];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();//Common.generateKey64Bit();
        tx.attributes[1] = new TransactionAttribute();
        tx.attributes[1].usage = TransactionAttributeUsage.Script;
        tx.attributes[1].data = Program.toScriptHash(Contract.createSignatureRedeemScript(publicKey)).toArray();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.code = paramsHexStr;
        tx.codeHash = new UInt160(Helper.hexToBytes(codeHash));
        return tx;
    }
}
