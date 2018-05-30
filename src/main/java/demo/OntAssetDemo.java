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
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Account;


/**
 *
 */
public class OntAssetDemo {

    public static void main(String[] args) {

        try {
            OntSdk sdk = getOntSdk();
            Account info1 = null;
            Account info2 = null;
            Account info3 = null;
            String password="passwordtest";

            sdk.getWalletMgr().setSignatureScheme(SignatureScheme.SHA256WITHECDSA);
            if (sdk.getWalletMgr().getAccounts().size() < 3) {
//                String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
                String privatekey0 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
                info1 = sdk.getWalletMgr().createAccountFromPriKey(password, privatekey0);
                info2 = sdk.getWalletMgr().createAccount(password);
                info3 = sdk.getWalletMgr().createAccountFromPriKey(password,"70789d4ac31576c61c5d12e38a66de605b18faf2c8d60a2c1952a6286b67318f");
                info3.label = "myaccount";
                sdk.getWalletMgr().writeWallet();
            }

            Account payerAcc = sdk.getWalletMgr().createAccount(password);

            //System.exit(0);
            info1 = sdk.getWalletMgr().getAccounts().get(0);
            info2 = sdk.getWalletMgr().getAccounts().get(1);
            info3 = sdk.getWalletMgr().getAccounts().get(2);
            System.out.println(info1.address);
            System.out.println(info2.address);
            System.out.println(info3.address);
            System.out.println(sdk.getConnect().getBalance(info1.address));

//            System.exit(0);
//            System.out.println(sdk.nativevm().ont().queryName("ont"));
//            System.out.println(sdk.nativevm().ont().querySymbol("ont"));
//            System.out.println(sdk.nativevm().ont().queryDecimals("ont"));
//            System.out.println(sdk.nativevm().ont().queryTotalSupply("ont"));
////
//            System.out.println(sdk.nativevm().ont().queryName("ong"));
//            System.out.println(sdk.nativevm().ont().querySymbol("ong"));
//            System.out.println(sdk.nativevm().ont().queryDecimals("ong"));
//            System.out.println(sdk.nativevm().ont().queryTotalSupply("ong"));

//            String hh = Address.addressFromMultiPubKeys(1,ontSdk.getWalletMgr().getAccount(info2.address,"passwordtest").publicKey,ontSdk.getWalletMgr().getAccount(info1.address,"passwordtest").publicKey).toBase58();
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info1.address,"passwordtest").pubkey);
//            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info2.address,"passwordtest").pubkey);
//            System.out.println(hh);
//            System.out.println(Helper.getCodeHash("aa", VmType.NEOVM.value()));
//            System.exit(0);
            System.out.println("address address address");
//            System.out.println(info1.address + " " + Address.addressFromPubKey(sdk.getWalletMgr().getAccount(info1.address, "111111").serializePublicKey()));
//            System.out.println(info2.address + " " + Address.addressFromPubKey(sdk.getWalletMgr().getAccount(info2.address, "passwordtest").serializePublicKey()));
//            System.out.println(info3.address + " " + Address.addressFromPubKey(sdk.getWalletMgr().getAccount(info3.address, "passwordtest").serializePublicKey()));
//            System.out.println("balance balance balance balance");
            System.out.println("info1 : ont :" + sdk.nativevm().ont().queryBalanceOf(info1.address));
            System.out.println("info2 : ont :" + sdk.nativevm().ont().queryBalanceOf(info2.address));
            System.out.println("info3 : ont :" + sdk.nativevm().ont().queryBalanceOf(info3.address));
//            System.out.println(sdk.nativevm().ong().unclaimOng(info1.address));
//            String hash = sdk.nativevm().ont().claimOng(info1.address,"passwordtest",info1.address,22390000000000L,0);
//            String hash = sdk.nativevm().ont().sendTransfer(info1.address,password,info2.address,30L,payerAcc.address,password,sdk.DEFAULT_GAS_LIMIT,0);
//            String hash1 = ontSdk.getOntAssetTx().sendTransferToMany("ont",info1.address,"passwordtest",new String[]{info2.address,info3.address},new long[]{100L,200L});
//            String hash2 = ontSdk.getOntAssetTx().sendTransferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{"passwordtest", "passwordtest"}, info3.address, new long[]{1L, 2L});
//            sdk.nativevm().ont().sendApprove(info1.address,password,info2.address,10,payerAcc.address,password,sdk.DEFAULT_GAS_LIMIT,0);
//
//
            long l = sdk.nativevm().ont().queryAllowance(info1.address,info2.address);
            System.out.println(l);
//            sdk.nativevm().ont().sendTransferFrom(info2.address,password,info1.address,info2.address,10,payerAcc.address,password,sdk.DEFAULT_GAS_LIMIT,0);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
