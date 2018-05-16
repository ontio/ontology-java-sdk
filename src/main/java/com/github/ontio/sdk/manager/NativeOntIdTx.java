package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.*;

public class NativeOntIdTx {
    private OntSdk sdk;
    private String contractAddress = null;


    public NativeOntIdTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return contractAddress;
    }

    /**
     *
     * @param ident
     * @param password
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithPublicKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), ident.ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        return identity;
    }

    /**
     *
     * @param ident
     * @param password
     * @param attrsMap
     * @return
     * @throws Exception
     */
    public Identity sendRegister(Identity ident, String password,Map<String, Object> attrsMap) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        IdentityInfo info = sdk.getWalletMgr().getIdentityInfo(ident.ontid, password);
        String ontid = info.ontid;
        byte[] pk = Helper.hexToBytes(info.pubkey);

        byte attriNum = (byte) attrsMap.size();
        byte[] allAttrsBys = new byte[]{};

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter binaryWriter = new BinaryWriter(byteArrayOutputStream);

        for (Map.Entry<String, Object> e : attrsMap.entrySet()) {
            Object val = e.getValue();
            String tmpVal = "";
            byte[] bs = null;
            String type = "Object";
            byte[] attrsBys = new byte[]{};
            if (val instanceof BigInteger) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Integer".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof byte[]) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("ByteArray".getBytes());
                binaryWriter.writeVarBytes(new String((byte[]) val).getBytes());
            } else if (val instanceof Boolean) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Boolean".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((boolean) val).getBytes());
            } else if (val instanceof Integer) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Integer".getBytes());
                binaryWriter.writeVarBytes(String.valueOf((int) val).getBytes());
            } else if (val instanceof String) {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("String".getBytes());
                binaryWriter.writeVarBytes(((String) val).getBytes());
            } else {
                binaryWriter.writeVarBytes(e.getKey().getBytes());
                binaryWriter.writeVarBytes("Object".getBytes());
                binaryWriter.writeVarBytes(JSON.toJSONString(val).getBytes());
            }
        }

        byte[] parabytes = buildParams(ontid.getBytes(), pk, byteArrayOutputStream.toByteArray());

        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "regIDWithAttributes", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), ident.ontid.replace(Common.didont,""));
        sdk.signTx(tx, ontid, password);
        Identity identity = sdk.getWalletMgr().addOntIdController(ontid, info.encryptedPrikey, info.ontid);
        sdk.getWalletMgr().writeWallet();
        boolean b = false;
        b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        return identity;
    }

    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetPublicKeys(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getPublicKeys", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
        BinaryReader br = new BinaryReader(bais);
        Map publicKeyMap = new HashMap();
        while (true){
            try{
                publicKeyMap.put("index", br.readInt());
                publicKeyMap.put("publicKey",Helper.toHexString(br.readVarBytes()));
            }catch (Exception e){
                break;
            }
        }
        return JSON.toJSONString(publicKeyMap);
    }

    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetKeyState(String ontid,int index) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes(),index);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getKeyState", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        System.out.println(tx.toHexString());
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        return new String(Helper.hexToBytes((String) obj));
    }

    public String sendGetAttributes(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getAttributes", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes((String)obj));
        BinaryReader br = new BinaryReader(bais);
        Map attributeMap = new HashMap();
        while (true){
            try{
                attributeMap.put("key", new String(br.readVarBytes()));
                attributeMap.put("type",new String(br.readVarBytes()));
                attributeMap.put("value",new String(br.readVarBytes()));
            }catch (Exception e){
                break;
            }
        }
        return JSON.toJSONString(attributeMap);
    }


    /**
     *
     * @param ontid
     * @param password
     * @param newpubkey
     * @return
     * @throws Exception
     */
    public String sendAddPubKey(String ontid, String password, String newpubkey) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(newpubkey),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param removePubkey
     * @return
     * @throws Exception
     */
    public String sendRemovePubKey(String ontid, String password, String removePubkey) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Helper.hexToBytes(removePubkey),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "removeKey", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param recovery
     * @return
     * @throws Exception
     */
    public String sendAddRecovery(String ontid, String password, String recovery) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(recovery).toArray(),pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param newRecovery
     * @param oldRecovery
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "changeRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), oldRecovery);
        sdk.signTx(tx, oldRecovery, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param newRecovery
     * @param oldRecovery
     * @param addresses
     * @param password
     * @return
     * @throws Exception
     */
    public String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery,String[] addresses, String[] password) throws Exception {
        if(addresses.length != password.length) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        com.github.ontio.account.Account[] accounts = new com.github.ontio.account.Account[addresses.length];
        for(int i = 0; i< addresses.length; i++){
            accounts[i] = sdk.getWalletMgr().getAccount(addresses[i],password[i]);
        }
        String addr = ontid.replace(Common.didont, "");
        byte[] parabytes = buildParams(ontid.getBytes(),Address.decodeBase58(newRecovery).toArray(),Address.decodeBase58(oldRecovery).toArray());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "changeRecovery", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), oldRecovery);
        sdk.signTx(tx, new com.github.ontio.account.Account[][]{accounts});
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @param password
     * @param path
     * @param type
     * @param value
     * @return
     * @throws Exception
     */
    public String sendAddAttribute(String ontid, String password, byte[] path,byte[] type, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        byte[] parabytes = buildParams(ontid.getBytes(),path,type,value,pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addAttribute", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendAddAttributeArray(String ontid, String password, byte[] path,byte[] type, byte[] value) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = ontid.replace(Common.didont, "");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);

        byte[] parabytes = buildParams(ontid.getBytes(),path,type,value,pk);
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "addAttribute", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,contract.toArray(), VmType.Native.value(), addr);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param ontid
     * @return
     * @throws SDKException
     * @throws ConnectorException
     * @throws IOException
     */
    public String sendGetDDO(String ontid) throws SDKException, ConnectorException, IOException {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        byte[] parabytes = buildParams(ontid.getBytes());
        Contract contract = new Contract((byte) 0,null, Address.parse(contractAddress), "getDDO", parabytes);
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress, null, contract.toArray(), VmType.Native.value(), ontid.replace(Common.didont,""));
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj == null || ((String) obj).length() == 0) {
            throw new SDKException(ErrorCode.ResultIsNull);
        }
        Map map = parseDdoData2(ontid, (String) obj);
        if (map.size() == 0) {
            return "";
        }
        return JSON.toJSONString(map);
    }
    private Map parseDdoData2(String ontid, String obj) throws IOException {
        byte[] bys = Helper.hexToBytes(obj);

        ByteArrayInputStream bais = new ByteArrayInputStream(bys);
        BinaryReader br = new BinaryReader(bais);
        byte[] publickeyBytes;
        byte[] attributeBytes;
        byte[] recoveryBytes;
        try{
            publickeyBytes = br.readVarBytes();
        }catch (Exception e){
            publickeyBytes = new byte[]{};
        }
        try{
            attributeBytes = br.readVarBytes();
        }catch (Exception e){
            attributeBytes = new byte[]{};
        }
        try {
            recoveryBytes = br.readVarBytes();
        }catch (Exception e){
            recoveryBytes = new byte[]{};
        }
        Map publicKeyMap = new HashMap();
        if(publickeyBytes.length != 0){
            ByteArrayInputStream bais1 = new ByteArrayInputStream(publickeyBytes);
            BinaryReader br1 = new BinaryReader(bais1);
            while (true) {
                try {
                    publicKeyMap.put("index",br1.readInt());
                    publicKeyMap.put("publicKey",Helper.toHexString(br1.readVarBytes()));
                } catch (Exception e) {
                    break;
                }
            }
        }
        Map<String, Object> attributeMap = new HashMap();
        if(attributeBytes.length != 0){
            ByteArrayInputStream bais2 = new ByteArrayInputStream(attributeBytes);
            BinaryReader br2 = new BinaryReader(bais2);
            while (true) {
                try {
                    attributeMap.put("key",new String(br2.readVarBytes()));
                    attributeMap.put("type",new String(br2.readVarBytes()));
                    attributeMap.put("value",new String(br2.readVarBytes()));
                } catch (Exception e) {
                    break;
                }
            }
        }

        Map map = new HashMap();
        map.put("publicKey",publicKeyMap);
        map.put("attributes",attributeMap);
        map.put("recovery", Helper.toHexString(recoveryBytes));
        return map;
    }
    private Map parseDdoData(String ontid, String obj) {
        byte[] bys = Helper.hexToBytes(obj);
        int elen = parse4bytes(bys, 0);
        int offset = 4;
        if (elen == 0) {
            return new HashMap();
        }
        byte[] pubkeysData = new byte[elen];
        System.arraycopy(bys, offset, pubkeysData, 0, elen);
//        int pubkeysNum = pubkeysData[0];

        byte[] tmpb = new byte[4];
        System.arraycopy(bys, offset, tmpb, 0, 4);
        int pubkeysNum = bytes2int(tmpb);

        offset = 4;
        Map map = new HashMap();
        Map attriMap = new HashMap();
        List ownersList = new ArrayList();
        for (int i = 0; i < pubkeysNum; i++) {
            int pubkeyIdLen = parse4bytes(pubkeysData, offset);
            offset = offset + 4;
            int pubkeyId = (int) pubkeysData[offset];
            offset = offset + pubkeyIdLen;
            int len = parse4bytes(pubkeysData, offset);
            offset = offset + 4;
            byte[] data = new byte[len];
            System.arraycopy(pubkeysData, offset, data, 0, len);
            Map owner = new HashMap();
            owner.put("PublicKeyId", ontid + "#keys-" + String.valueOf(pubkeyId));
            if(sdk.signatureScheme == SignatureScheme.SHA256WITHECDSA) {
                owner.put("Type", KeyType.ECDSA);
                owner.put("Curve", new Object[]{"P-256"}[0]);
            }
            owner.put("Value", Helper.toHexString(data));
            ownersList.add(owner);
            offset = offset + len;
        }
        map.put("Owners", ownersList);
        map.put("OntId", ontid);
        offset = 4 + elen;

        elen = parse4bytes(bys, offset);
        offset = offset + 4;
        int totalOffset = offset + elen;
        if (elen == 0) {
            map.put("Attributes", attriMap);
        }
        if (elen != 0) {
            byte[] attrisData = new byte[elen];
            System.arraycopy(bys, offset, attrisData, 0, elen);

//        int attrisNum = attrisData[0];
            System.arraycopy(bys, offset, tmpb, 0, 4);
            int attrisNum = bytes2int(tmpb);

            offset = 4;
            for (int i = 0; i < attrisNum; i++) {

                int dataLen = parse4bytes(attrisData, offset);
                offset = offset + 4;
                byte[] data = new byte[dataLen];
                System.arraycopy(attrisData, offset, data, 0, dataLen);
                offset = offset + dataLen;


                int index = 0;
                int len = parse4bytes(data, index);
                index = index + 4;
                byte[] key = new byte[len];
                System.arraycopy(data, index, key, 0, len);
                index = index + len;

                len = parse4bytes(data, index);
                index = index + 4;
                len = data[index];
                index++;
                byte[] type = new byte[len];
                System.arraycopy(data, index, type, 0, len);
                index = index + len;

                byte[] value = new byte[dataLen - index];
                System.arraycopy(data, index, value, 0, dataLen - index);

                Map tmp = new HashMap();
                tmp.put("Type", new String(type));
                tmp.put("Value", new String(value));
                attriMap.put(new String(key), tmp);
            }
            map.put("Attributes", attriMap);
        }
        if (totalOffset < bys.length) {
            elen = parse4bytes(bys, totalOffset);
            if (elen == 0) {
                return map;
            }
            byte[] recoveryData = new byte[elen];
            offset = 4;
            System.arraycopy(bys, totalOffset + 4, recoveryData, 0, elen);
            map.put("Recovery", Helper.toHexString(recoveryData));
        }
        return map;
    }

    private int parse4bytes(byte[] bs, int offset) {
        return (bs[offset] & 0xFF) * 256 * 256 * 256 + (bs[offset + 1] & 0xFF) * 256 * 256 + (bs[offset + 2] & 0xFF) * 256 + (bs[offset + 3] & 0xFF);
    }

    private int bytes2int(byte[] b) {
        int i = 0;
        int ret = 0;
        for (; i < b.length; i++) {
            ret = ret * 256;
            ret = ret + b[i];
        }
        return ret;
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
