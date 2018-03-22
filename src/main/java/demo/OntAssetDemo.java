package demo;

import ontology.OntSdk;
import ontology.common.Common;
import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.core.VmType;
import ontology.core.contract.Contract;
import ontology.sdk.info.account.AccountInfo;
import ontology.sdk.wallet.Account;

import java.util.Date;

import static ontology.common.Common.print;

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
            if(ontSdk.getWalletMgr().getAccounts().size() < 2){
                info1 = ontSdk.getWalletMgr().createAccountFromPrikey("passwordtest","50468cf55de3808728a6e040adec12ad27750cf0d82aa390d551ff2b18c676f2");
                info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
                ontSdk.getWalletMgr().writeWallet();
            }
//            ontSdk.getWalletMgr().createAccount("passwordtest","50468cf55de3808728a6e040adec12ad27750cf0d82aa390d551ff2b18c676f2");
//            ontSdk.getWalletMgr().writeWallet();
            info1 = ontSdk.getWalletMgr().getAccounts().get(0);
            info2 = ontSdk.getWalletMgr().getAccounts().get(1);
            String hh = Contract.addressFromMultiPubKeys(1,ontSdk.getWalletMgr().getAccount(info2.address,"passwordtest").publicKey,ontSdk.getWalletMgr().getAccount(info1.address,"passwordtest").publicKey).toBase58();
            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info1.address,"passwordtest").pubkey);
            System.out.println(ontSdk.getWalletMgr().getAccountInfo(info2.address,"passwordtest").pubkey);
            System.out.println(hh);
            System.out.println(Helper.getCodeHash("aa", VmType.NEOVM.value()));
            System.exit(0);
            System.out.println(info1.address+" "+Contract.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info1.address,"passwordtest").publicKey));
            System.out.println(info2.address+" "+Contract.addressFromPubKey(ontSdk.getWalletMgr().getAccount(info2.address,"passwordtest").publicKey));
            String hash = ontSdk.getOntAssetTx().transfer(info1.address,"passwordtest",1,info2.address,"no");
            System.out.println(hash);
            System.out.println(Helper.toHexString(UInt160.decodeBase58(info1.address).toArray()));
            String addr = Contract.addressFromPubKey(ontSdk.getWalletMgr().getPubkey("0399b851bc2cd05506d6821d4bc5a92139b00ac4bc7399cd9ca0aac86a468d1c05")).toBase58();
            System.out.println(addr);
            System.out.println(Helper.toHexString(Contract.addressFromPubKey(ontSdk.getWalletMgr().getPubkey("0399b851bc2cd05506d6821d4bc5a92139b00ac4bc7399cd9ca0aac86a468d1c05")).toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {

        String url = "http://127.0.0.1:20384";
//        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        //配置 ontid 文件
        wm.openWalletFile("OntAssetDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
