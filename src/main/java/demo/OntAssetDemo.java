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

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.core.VmType;
import com.github.ontio.sdk.wallet.Account;


/**
 *
 */
public class OntAssetDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            Account info1 = null;
            Account info2 = null;
            Account info3 = null;
            //1202015225b1d934deb6c3f2ebe4b9b50aa434bfb1b7cb6596b992b67c94c4cbcd1b027b12afda773ed5489aa4f8426ed78043ba61602cf38f8588c6faf01f4652f27c
            //1202027b12afda773ed5489aa4f8426ed78043ba61602cf38f8588c6faf01f4652f27c
            if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
                info1 = ontSdk.getWalletMgr().createAccountFromPriKey("111111", "015225b1d934deb6c3f2ebe4b9b50aa434bfb1b7cb6596b992b67c94c4cbcd1b");
                info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
                info3 = ontSdk.getWalletMgr().createAccount("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }
            System.out.println(ontSdk.getConnectMgr().getBalance("TA6mgRHJC4MoTHgQE3dG4FkuYVVjrtjmAC"));

            //System.exit(0);
            info1 = ontSdk.getWalletMgr().getAccounts().get(0);
            info2 = ontSdk.getWalletMgr().getAccounts().get(1);
            info3 = ontSdk.getWalletMgr().getAccounts().get(2);


//            System.out.println(ontSdk.getOntAssetTx().queryName("ont",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().querySymbol("ont",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().queryDecimals("ont",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().queryTotalSupply("ont",info1.address));
//
//            System.out.println(ontSdk.getOntAssetTx().queryName("ong",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().querySymbol("ong",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().queryDecimals("ong",info1.address));
//            System.out.println(ontSdk.getOntAssetTx().queryTotalSupply("ong",info1.address));

//            String hh = Address.addressFromMultiPubKeys(1,ontSdk.getWalletMgr().getAccount(info2.address,"passwordtest").publicKey,ontSdk.getWalletMgr().getAccount(info1.address,"passwordtest").publicKey).toBase58();
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info1.address,"passwordtest").pubkey);
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info2.address,"passwordtest").pubkey);
//            System.out.println(hh);
//            System.out.println(Helper.getCodeHash("aa", VmType.NEOVM.value()));
//            System.exit(0);
            System.out.println("address address address");
            System.out.println(info1.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info1.address, "111111").serializePublicKey()));
            System.out.println(info2.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info2.address, "passwordtest").serializePublicKey()));
            System.out.println(info3.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info3.address, "passwordtest").serializePublicKey()));
            System.out.println("balance balance balance balance");
            System.out.println("info1 : ong :" + ontSdk.getOntAssetTx().sendBalanceOf("ong",info1.address));
            System.out.println("info2 : ong :" + ontSdk.getOntAssetTx().sendBalanceOf("ong",info2.address));
            System.out.println("info3 : ong :" + ontSdk.getOntAssetTx().sendBalanceOf("ong",info3.address));

//            String hash = ontSdk.getOntAssetTx().sendOngTransferFrom(info1.address,"111111","TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW",1000);
//            String hash = ontSdk.getOntAssetTx().sendTransfer("ont",info1.address,"111111",info3.address,10L);
//            String hash1 = ontSdk.getOntAssetTx().sendTransferToMany("ont",info1.address,"passwordtest",new String[]{info2.address,info3.address},new long[]{100L,200L});
//            String hash2 = ontSdk.getOntAssetTx().sendTransferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{"passwordtest", "passwordtest"}, info3.address, new long[]{1L, 2L});
//              ontSdk.getOntAssetTx().sendApprove("ont",info1.address,"111111",info2.address,10);
//            ontSdk.getOntAssetTx().sendTransferFrom("ont",info2.address,"passwordtest",info1.address,info2.address,10);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
