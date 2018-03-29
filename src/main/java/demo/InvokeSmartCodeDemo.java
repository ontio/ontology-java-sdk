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

package demo;

import com.github.ontio.common.Helper;
import com.github.ontio.OntSdk;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.alibaba.fastjson.JSON;
import com.github.ontio.sdk.websocket.MsgQueue;
import com.github.ontio.sdk.websocket.Result;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


/**
 *
 */
public class InvokeSmartCodeDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            //System.out.println(ontSdk.getWalletMgr().getWallet());

            InputStream is = new FileInputStream("C:\\ZX\\IdContract.abi.json");
            byte[] bys = new byte[is.available()];
            is.read(bys);
            is.close();
            String abi = new String(bys);

            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
//            System.out.println("codeHash:"+abiinfo.getHash());
            System.out.println("Entrypoint:" + abiinfo.getEntrypoint());
            System.out.println("Functions:" + abiinfo.getFunctions());


            if (ontSdk.getWalletMgr().getIdentitys().size() == 0) {
                Map map = new HashMap<>();
                map.put("test", "value00");
                Identity did = ontSdk.getOntIdTx().sendRegister("passwordtest");
            }
            Identity did = ontSdk.getWalletMgr().getIdentitys().get(0);
            System.out.println(did.ontid);
            System.out.println("did hex:" + Helper.toHexString(did.ontid.getBytes()) + "  " + Helper.toHexString(did.ontid.getBytes()).length());
            System.out.println();

            String ddo = ontSdk.getOntIdTx().sendGetDDO(did.ontid);
            System.out.println("Ddo:" + ddo);
            System.exit(0);

            AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(did.ontid, "passwordtest");
            AbiFunction func = abiinfo.getFunction("AddAttribute");
            System.out.println(func.getName() + ":  " + func.getParameters());
            String value = "{\"Context\":\"claim:context\",\"Content\":{\"Issuer\":\"did:ont:TA8WyMDTP7BXFK6pVZ55Wn9gvAjCvsMfTm\",\"Subject\":\"did:ont:TA7idxkVHWXQgk2fd4dPZRyp7V2G7qTBzd\"},\"Signature\":{\"Format\":\"pgp\",\"Value\":\"AXLttwbJT8PV3L8P721PEDSFrMZ+wf4tYEcQMfsBiDH9Wi3DUvUnLdeBkarH2ZcvqB3YICFBcJy8aA46VLjFkFA=\",\"Algorithm\":\"ECDSAwithSHA256\"},\"Metadata\":{\"Issuer\":\"did:ont:TA8WyMDTP7BXFK6pVZ55Wn9gvAjCvsMfTm\",\"CreateTime\":\"2018-03-29T16:45:09Z\",\"Subject\":\"did:ont:TA7idxkVHWXQgk2fd4dPZRyp7V2G7qTBzd\"},\"Id\":\"3903a2f8158d71b976936ab60656fc0b615be40715d9235e4992c15e530b8ed3\"}";
            func.setParamsValue(did.ontid.getBytes(), "key".getBytes(), "bytes".getBytes(), value.getBytes(), Helper.hexToBytes(info.pubkey));

            String hash = ontSdk.getSmartcodeTx().sendInvokeSmartCodeWithSign(did.ontid, "passwordtest", func, (byte) 0x80);

            System.out.println("invokeTransaction hash:" + hash);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
//        String ip = " http://139.219.108.204";
        String restUrl = ip + ":" + "20384";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("InvokeSmartCodeDemo.json");
        wm.setCodeAddress("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
        return wm;
    }
}
