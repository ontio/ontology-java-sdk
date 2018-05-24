package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Description:
 * @date 2018/5/24
 */
public class Auth {
    private OntSdk sdk;
    private final String contractAddress = "ff00000000000000000000000000000000000006";
    public Auth(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendTransfer(String ontid,String password,String contractAddr, String newAdminOntID,int key,String payer,String payerpassword,long gaslimit,long gas) throws Exception {
        if(ontid ==null || ontid.equals("") || contractAddr == null || contractAddr.equals("") || newAdminOntID==null || newAdminOntID.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        byte[] parabytes = buildParams(contractAddr.getBytes(),newAdminOntID.getBytes(),key);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"transfer",parabytes, VmType.Native.value(), payer,gaslimit,gas);
        sdk.signTx(tx,ontid,password);
        sdk.addSign(tx,payer,payerpassword);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    public String assignFuncsToRole(String adminOntID,String password,String contractAddr,String role,String[] funcName,int key,String payer,String payerpassword,long gaslimit,long gas){

        return null;
    }

    public byte[] buildParams(Object ...params) throws SDKException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);
        try {
            for (Object param : params) {
                if(param instanceof Integer){
                    binaryWriter.writeInt(((Integer) param).intValue());
                }else if(param instanceof byte[]){
                    binaryWriter.writeVarBytes((byte[])param);
                }else if(param instanceof String){
                    binaryWriter.writeVarString((String) param);
                }
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        }
        return byteArrayOutputStream.toByteArray();
    }


}


