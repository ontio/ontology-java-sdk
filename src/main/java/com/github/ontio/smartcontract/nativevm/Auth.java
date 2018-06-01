package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
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

    /**
     *
     * @param adminOntId
     * @param password
     * @param contractAddr
     * @param newAdminOntID
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransfer(String adminOntId,String password,String contractAddr, String newAdminOntID,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(adminOntId ==null || adminOntId.equals("") || contractAddr == null || contractAddr.equals("") || newAdminOntID==null || newAdminOntID.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit <0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeTransfer(adminOntId,contractAddr,newAdminOntID,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,adminOntId,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    /**
     *
     * @param adminOntID
     * @param contractAddr
     * @param newAdminOntID
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeTransfer(String adminOntID,String contractAddr, String newAdminOntID,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(adminOntID ==null || adminOntID.equals("") || contractAddr == null || contractAddr.equals("") || newAdminOntID==null || newAdminOntID.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit <0 || gasprice <0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        byte[] parabytes = new TransferParam(Helper.hexToBytes(contractAddr),newAdminOntID.getBytes(),keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"transfer",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param contractAddr
     * @param funcName
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String verifyToken(String ontid,String password,String contractAddr,String funcName,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid ==null || ontid.equals("") || password ==null || password.equals("")|| contractAddr == null || contractAddr.equals("") || funcName==null || funcName.equals("")
                ||payer ==null||payer.equals("")||payerpwd==null||payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("key or gaslimit or gas price should not be less than 0"));
        }
        Transaction tx = makeVerifyToken(ontid,contractAddr,funcName,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,ontid,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    /**
     *
     * @param ontid
     * @param contractAddr
     * @param funcName
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeVerifyToken(String ontid,String contractAddr,String funcName,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(ontid ==null || ontid.equals("")|| contractAddr == null || contractAddr.equals("") || funcName==null || funcName.equals("")||payer==null ||payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("key or gaslimit or gas price should not be less than 0"));
        }
        byte[] parabytes = new VerifyTokenParam(Helper.hexToBytes(contractAddr),ontid.getBytes(),funcName.getBytes(),keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"verifyToken",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param adminOntID
     * @param password
     * @param contractAddr
     * @param role
     * @param funcName
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String assignFuncsToRole(String adminOntID,String password,String contractAddr,String role,String[] funcName,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(adminOntID ==null || adminOntID.equals("") || contractAddr == null || contractAddr.equals("") || role==null || role.equals("") || funcName == null || funcName.length == 0
                || payer==null || payer.equals("") ||payerpwd==null || payerpwd.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gas price should not be less than 0"));
        }
        Transaction tx = makeAssignFuncsToRole(adminOntID,contractAddr,role,funcName,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,adminOntID,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param adminOntID
     * @param contractAddr
     * @param role
     * @param funcName
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeAssignFuncsToRole(String adminOntID,String contractAddr,String role,String[] funcName,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(adminOntID ==null || adminOntID.equals("") || contractAddr == null || contractAddr.equals("") || role==null || role.equals("") || funcName == null || funcName.length == 0
                || payer==null || payer.equals("")){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo < 0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gas price should not be less than 0"));
        }
        byte[] parabytes = new FuncsToRoleParam(Helper.hexToBytes(contractAddr),adminOntID.getBytes(),role.getBytes(),funcName,keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"assignFuncsToRole",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param adminOntId
     * @param password
     * @param contractAddr
     * @param role
     * @param ontIDs
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String assignOntIDsToRole(String adminOntId,String password,String contractAddr,String role,String[] ontIDs,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(adminOntId == null || adminOntId.equals("") || password==null || password.equals("") || contractAddr== null || contractAddr.equals("") ||
                role == null || role.equals("") || ontIDs==null || ontIDs.length == 0){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo<0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeAssignOntIDsToRole(adminOntId,contractAddr,role,ontIDs,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,adminOntId,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param adminOntId
     * @param contractAddr
     * @param role
     * @param ontIDs
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeAssignOntIDsToRole(String adminOntId,String contractAddr,String role,String[] ontIDs,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(adminOntId == null || adminOntId.equals("") || contractAddr== null || contractAddr.equals("") ||
                role == null || role.equals("") || ontIDs==null || ontIDs.length == 0){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        byte[][] ontId = new byte[ontIDs.length][];
        for(int i=0; i< ontIDs.length ; i++){
            ontId[i] = ontIDs[i].getBytes();
        }
        byte[] parabytes = new OntIDsToRoleParam(Helper.hexToBytes(contractAddr),adminOntId.getBytes(),role.getBytes(),ontId,keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"assignOntIDsToRole",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param contractAddr
     * @param toOntId
     * @param role
     * @param period
     * @param level
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String delegate(String ontid,String password,String contractAddr,String toOntId,String role,long period,long level,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(ontid == null || ontid.equals("") ||password == null || password.equals("") || contractAddr == null || contractAddr.equals("") ||toOntId==null || toOntId.equals("")||
                role== null || role.equals("") || payer ==null || payer.equals("")||payerpwd == null || payerpwd.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(period<0 || level <0 || keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("period level key gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeDelegate(ontid,contractAddr,toOntId,role,period,level,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,ontid,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param contractAddr
     * @param toAddr
     * @param role
     * @param period
     * @param level
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeDelegate(String ontid,String contractAddr,String toAddr,String role,long period,long level,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(ontid == null || ontid.equals("")|| contractAddr == null || contractAddr.equals("") ||toAddr==null || toAddr.equals("")||
                role== null || role.equals("") || payer ==null || payer.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(period<0 || level <0 || keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("period level keyNo gaslimit or gasprice should not be less than 0"));
        }
        byte[] parabytes = new DelegateParam(Helper.hexToBytes(contractAddr),ontid.getBytes(),toAddr.getBytes(),role.getBytes(),period,level,keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"delegate",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param initiatorOntid
     * @param password
     * @param contractAddr
     * @param delegate
     * @param role
     * @param keyNo
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String withdraw(String initiatorOntid,String password,String contractAddr,String delegate, String role,long keyNo,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if(initiatorOntid == null || initiatorOntid.equals("")|| password ==null|| password.equals("") || contractAddr == null || contractAddr.equals("") ||
                role== null || role.equals("") || payer ==null || payer.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("keyNo or gaslimit or gasprice should not be less than 0"));
        }
        Transaction tx = makeWithDraw(initiatorOntid,contractAddr,delegate,role,keyNo,payer,gaslimit,gasprice);
        sdk.signTx(tx,initiatorOntid,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param contractAddr
     * @param delegate
     * @param role
     * @param keyNo
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws SDKException
     */
    public Transaction makeWithDraw(String ontid,String contractAddr,String delegate, String role,long keyNo,String payer,long gaslimit,long gasprice) throws SDKException {
        if(ontid == null || ontid.equals("")|| contractAddr == null || contractAddr.equals("") ||
                role== null || role.equals("") || payer ==null || payer.equals("")){
            throw  new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(keyNo <0 || gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("key gaslimit or gasprice should not be less than 0"));
        }
        byte[] parabytes = new AuthWithdrawParam(Helper.hexToBytes(contractAddr),ontid.getBytes(),delegate.getBytes(),role.getBytes(),keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"withdraw",parabytes, VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }
}
class TransferParam implements Serializable {
    byte[] contractAddr;
    byte[] newAdminOntID;
    long KeyNo;
    TransferParam(byte[] contractAddr,byte[] newAdminOntID,long keyNo){
        this.contractAddr = contractAddr;
        this.newAdminOntID = newAdminOntID;
        KeyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.newAdminOntID = reader.readVarBytes();
        KeyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.newAdminOntID);
        writer.writeVarInt(KeyNo);
    }
}
class VerifyTokenParam implements Serializable{
    byte[] contractAddr;
    byte[] caller;
    byte[] fn;
    long keyNo;
    VerifyTokenParam(byte[] contractAddr,byte[] caller,byte[] fn,long keyNo){
        this.contractAddr = contractAddr;
        this.caller = caller;
        this.fn = fn;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.caller);
        writer.writeVarBytes(this.fn);
        writer.writeVarInt(keyNo);
    }
}

class FuncsToRoleParam implements Serializable{
    byte[] contractAddr;
    byte[] adminOntID;
    byte[] role;
    String[] funcNames;
    long keyNo;

    FuncsToRoleParam(byte[] contractAddr,byte[] adminOntID,byte[] role,String[] funcNames,long keyNo){
        this.contractAddr =contractAddr;
        this.adminOntID = adminOntID;
        this.role =role;
        this.funcNames = funcNames;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.adminOntID = reader.readVarBytes();
        this.role = reader.readVarBytes();
        int length = (int)reader.readVarInt();
        this.funcNames = new String[length];
        for(int i = 0;i< length;i++){
            this.funcNames[i] = reader.readVarString();
        }
        this.keyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.adminOntID);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.funcNames.length);
        for(String name:this.funcNames){
            writer.writeVarString(name);
        }
        writer.writeVarInt(this.keyNo);
    }
}
class OntIDsToRoleParam implements Serializable{
    byte[] contractAddr;
    byte[] adminOntID;
    byte[] role;
    byte[][] persons;
    long keyNo;
    OntIDsToRoleParam( byte[] contractAddr,byte[] adminOntID,byte[] role,byte[][] persons,long keyNo){
        this.contractAddr = contractAddr;
        this.adminOntID = adminOntID;
        this.role = role;
        this.persons = persons;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.contractAddr = reader.readVarBytes();
        this.adminOntID = reader.readVarBytes();
        this.role = reader.readVarBytes();
        int length = (int)reader.readVarInt();
        this.persons = new byte[length][];
        for(int i = 0; i< length;i++){
            this.persons[i] = reader.readVarBytes();
        }
        this.keyNo = reader.readVarInt();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.adminOntID);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.persons.length);
        for(byte[] p: this.persons){
            writer.writeVarBytes(p);
        }
        writer.writeVarInt(this.keyNo);
    }
}

class DelegateParam implements  Serializable{
    byte[] contractAddr;
    byte[] from;
    byte[] to;
    byte[] role;
    long period;
    long level;
    long keyNo;
    DelegateParam(byte[] contractAddr,byte[] from,byte[] to,byte[] role, long period, long level,long keyNo){
        this.contractAddr = contractAddr;
        this.from = from;
        this.to = to;
        this.role = role;
        this.period = period;
        this.level = level;
        this.keyNo = keyNo;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.from);
        writer.writeVarBytes(this.to);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.period);
        writer.writeVarInt(this.level);
        writer.writeVarInt(this.keyNo);
    }
}

class AuthWithdrawParam implements Serializable{
    byte[] contractAddr;
    byte[] initiator;
    byte[] delegate;
    byte[] role;
    long keyNo;
    public AuthWithdrawParam(byte[] contractAddr,byte[] initiator, byte[] delegate,byte[] role,long keyNo){
        this.contractAddr = contractAddr;
        this.initiator = initiator;
        this.delegate = delegate;
        this.role = role;
        this.keyNo = keyNo;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(this.contractAddr);
        writer.writeVarBytes(this.initiator);
        writer.writeVarBytes(this.delegate);
        writer.writeVarBytes(this.role);
        writer.writeVarInt(this.keyNo);
    }
}


