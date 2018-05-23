package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.Parameter;
import com.github.ontio.sdk.exception.SDKException;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @date 2018/5/23
 */
public class BuildParams {


//    /**
//     * @param abiFunction
//     * @param vmtype
//     * @return
//     * @throws Exception
//     */
//    public String sendInvokeSmartCodeWithNoSign(AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
//        Transaction tx = invokeTransaction(null, null, abiFunction, vmtype,gas);
//        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
//        if (!b) {
//            throw new SDKException(ErrorCode.SendRawTxError);
//        }
//        return tx.hash().toString();
//    }

//    public String sendInvokeSmartCodeWithNoSignPreExec(AbiFunction abiFunction, byte vmtype) throws Exception {
//        Transaction tx = makeInvokecodeTransaction(null, null, abiFunction, vmtype,0);
//        Object obj = (String)sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
//        return ((JSONObject)obj).getString("Result");
//    }
//    /**
//     *
//     * @param ontid
//     * @param password
//     * @param abiFunction
//     * @param vmtype
//     * @return
//     * @throws Exception
//     */
//    public String sendInvokeSmartCodeWithSign(String ontid, String password, AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
//        Transaction tx = makeInvokecodeTransaction( ontid, password, abiFunction, vmtype,gas);
//        sdk.signTx(tx,ontid,password);
//        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
//        if (!b) {
//            throw new SDKException(ErrorCode.SendRawTxError);
//        }
//        return tx.hash().toString();
//    }
//
//    public String sendInvokeSmartCodeWithSignPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
//        Transaction tx = makeInvokecodeTransaction( ontid, password, abiFunction, vmtype,0);
//        sdk.signTx(tx, new Account[][]{{sdk.getWalletMgr().getAccount(ontid, password)}});
//        Object obj = (String)sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
//        return ((JSONObject)obj).getString("Result");
//    }
//    /**
//     * @param ontid
//     * @param password
//     * @param abiFunction
//     * @param vmtype
//     * @return
//     * @throws Exception
//     */
//    public Object sendInvokeTransactionPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
//        Transaction tx = makeInvokecodeTransaction( ontid, password, abiFunction, vmtype,gas);
//        return sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
//    }
//    public Transaction invokeTransactionNoSign(AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
//        byte[] params = buildParams("",null,null,abiFunction,vmtype,gas);
//        Transaction tx = makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);
//        return makeInvokecodeTransaction(null,null,abiFunction,vmtype,gas);
//    }

    public static byte[] Int2Bytes_LittleEndian(int iValue){
        byte[] rst = new byte[4];
        rst[0] = (byte)(iValue & 0xFF);
        rst[1] = (byte)((iValue & 0xFF00) >> 8 );
        rst[2] = (byte)((iValue & 0xFF0000) >> 16 );
        rst[3] = (byte)((iValue & 0xFF000000) >> 24 );
        return rst;
    }
    /**
     * @param builder
     * @param list
     * @return
     */
    private static byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.push((Boolean) val);
                } else if (val instanceof Integer) {
                    builder.push(new BigInteger(Int2Bytes_LittleEndian((int)val)));
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(builder, tmp);
                    builder.push(new BigInteger(String.valueOf(tmp.size())));
                    builder.pushPack();

                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toArray();
    }
    /**
     * @param list
     * @return
     */
    public static byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.push(new BigInteger(Int2Bytes_LittleEndian((int)val)));
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

    /**
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public static byte[] serializeAbiFunction( AbiFunction abiFunction) throws Exception {
        List list = new ArrayList<Object>();
        list.add(abiFunction.getName().getBytes());
        List tmp = new ArrayList<Object>();
        for (Parameter obj : abiFunction.getParameters()) {
            if ("ByteArray".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), byte[].class));
            } else if ("String".equals(obj.getType())) {
                tmp.add(obj.getValue());
            } else if ("Boolean".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), boolean.class));
            } else if ("Integer".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), int.class));
            } else if ("Array".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Array.class));
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
        }
        if(list.size()>0) {
            list.add(tmp);
        }
        byte[] params = createCodeParamsScript(list);
        return params;
//        Transaction tx = null;
//        if (ontid == null && password == null) {
//            tx = makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);
//        } else {
//            tx = makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);
//        }
//        return tx;
    }
}
//Transaction tx = makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);