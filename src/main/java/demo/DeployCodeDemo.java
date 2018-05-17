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

import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.VmType;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Identity;

import java.io.FileInputStream;
import java.io.InputStream;


/**
 *
 */
public class DeployCodeDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();

            InputStream is = new FileInputStream("/Users/sss/dev/ontologytest/IdContract/IdContract.avm");//IdContract
            byte[] bys = new byte[is.available()];
            is.read(bys);
            is.close();

            String password = "111111";
            Identity identity;
            if(ontSdk.getWalletMgr().getIdentitys().size() < 1){
                identity = ontSdk.getWalletMgr().createIdentity(password);
            }else{
                identity = ontSdk.getWalletMgr().getIdentitys().get(0);
            }
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            com.github.ontio.account.Account acct0 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0), ontSdk.signatureScheme);
            //code
            String code = "";
            code = Helper.toHexString(bys);
            System.out.println("Code:" + Helper.toHexString(bys));
            System.out.println("CodeAddress:" + Helper.getCodeAddress(code, VmType.NEOVM.value()));

            ontSdk.setCodeAddress(Helper.getCodeAddress(code, VmType.NEOVM.value()));

            Transaction tx = ontSdk.getSmartcodeTx().makeDeployCodeTransaction(code, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value(),identity.ontid,0);
            ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
            String txHex = Helper.toHexString(tx.toArray());
            System.out.println(txHex);
            ontSdk.getConnectMgr().sendRawTransaction(txHex);

            System.out.println("txhash:" + tx.hash().toString());
            Thread.sleep(6000);
            String txhash = tx.hash().toString();
            DeployCode t = (DeployCode) ontSdk.getConnectMgr().getTransaction(txhash);
            System.out.println(t.txType.value() & 0xff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://139.219.108.204";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("DeployDemo.json");

        return wm;
    }
}
