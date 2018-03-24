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

import static com.github.ontio.common.Common.print;

/**
 * Created by zx on 2018/1/17.
 */
public class OntAssetDemo {

    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            //String cliamHash = ontSdk.getAssetTx().claimTx(ontSdk.getWalletMgr().getAccounts().get(0).address,"passwordtest","a9f1bf985b1ad4cec8ae372f79879523402711ec70f12d175bdc8daf418eb57d");
            //System.out.println(cliamHash);
            //System.exit(0);
            Account info1 = null;
            Account info2 = null;
            Account info3 = null;
            if (ontSdk.getWalletMgr().getAccounts().size() < 2) {
                info1 = ontSdk.getWalletMgr().createAccountFromPrikey("passwordtest", "2ff1de0e26990385c5b7aa580e8516de20c95ac77a794e296be9e6fe005d6ed8");
                info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
                info3 = ontSdk.getWalletMgr().createAccount("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }

//            ontSdk.getWalletMgr().createAccount("passwordtest","50468cf55de3808728a6e040adec12ad27750cf0d82aa390d551ff2b18c676f2");
//            ontSdk.getWalletMgr().writeWallet();
            info1 = ontSdk.getWalletMgr().getAccounts().get(0);
            info2 = ontSdk.getWalletMgr().getAccounts().get(1);
            info3 = ontSdk.getWalletMgr().getAccounts().get(2);
//            String hh = Address.addressFromMultiPubKeys(1,ontSdk.getWalletMgr().getAccount(info2.address,"passwordtest").publicKey,ontSdk.getWalletMgr().getAccount(info1.address,"passwordtest").publicKey).toBase58();
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info1.address,"passwordtest").pubkey);
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info2.address,"passwordtest").pubkey);
//            System.out.println(hh);
//            System.out.println(Helper.getCodeHash("aa", VmType.NEOVM.value()));
//            System.exit(0);
            System.out.println(info1.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info1.address, "passwordtest").publicKey));
            System.out.println(info2.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info2.address, "passwordtest").publicKey));
            System.out.println(info3.address + " " + Address.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info3.address, "passwordtest").publicKey));
//            String hash = ontSdk.getOntAssetTx().transfer("ont",info1.address,"passwordtest",info2.address,100L);
//            String hash1 = ontSdk.getOntAssetTx().transferToMany("ont",info1.address,"passwordtest",new String[]{info2.address,info3.address},new long[]{100L,200L});
            String hash2 = ontSdk.getOntAssetTx().transferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{"passwordtest", "passwordtest"}, info3.address, new long[]{1L, 2L});
//            System.out.println(hash);
            //  System.out.println(Helper.toHexString(Address.decodeBase58(info1.address).toArray()));
            //  String addr = Address.addressFromPubKey(ontSdk.getWalletMgr().getPubkey("0399b851bc2cd05506d6821d4bc5a92139b00ac4bc7399cd9ca0aac86a468d1c05")).toBase58();
            //  System.out.println(addr);
            //  System.out.println(Helper.toHexString(Address.addressFromPubKey(ontSdk.getWalletMgr().getPubkey("0399b851bc2cd05506d6821d4bc5a92139b00ac4bc7399cd9ca0aac86a468d1c05")).toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

        String url = "http://127.0.0.1:20386";
//        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRpcConnection(url);
        //配置 ontid 文件
        wm.openWalletFile("OntAssetDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
