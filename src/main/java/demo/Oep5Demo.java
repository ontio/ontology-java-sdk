package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.smartcontract.neovm.oep5.Oep5Param;

public class Oep5Demo {
    public static void main(String[] args) {
        try {
            OntSdk sdk = getOntSdk();
            Account account = sdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p", "xinhao");
            Account account1 = sdk.getWalletMgr().getAccount("ARR5ywDEx3ybXkMGmZPFYu9hiC8J4xvNdc", "xinhao");
            Account account2 = sdk.getWalletMgr().getAccount("AacHGsQVbTtbvSWkqZfvdKePLS6K659dgp", "xinhao");
            System.out.println(Helper.toHexString(account.serializePrivateKey()));
            if (false){
                String txhash = sdk.neovm().oep5().sendInit(account, account,281803, 0);
                return;
            }
            if (false){
                String name = sdk.neovm().oep5().queryName();
                System.out.println("name: " + name);
                long symbol = sdk.neovm().oep5().queryTotalSupply();
                System.out.println("symbol: " + sdk.neovm().oep5().querySymbol());
                System.out.println("totalsupply: " + symbol);

                long balance = sdk.neovm().oep5().queryBalanceOf(account.getAddressU160().toBase58());
                System.out.println("balance: " + balance);
                return;
            }

            if (true){
                byte[] tokenId = Helper.hexToBytes("01");
                String txhash = sdk.neovm().oep5().ownerOf(tokenId);
                System.out.println("txhash: " + txhash);
                return;
            }

            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Oep5Param param = new Oep5Param(account.getAddressU160().toArray(), tokenId);
                String txhash = sdk.neovm().oep5().transfer(account,param,account,20000,0);
                System.out.println("txhash: " + txhash);
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Account[] accounts = new Account[]{account};
                Oep5Param param = new Oep5Param(account.getAddressU160().toArray(), tokenId);
                Oep5Param param2 = new Oep5Param(account.getAddressU160().toArray(), tokenId);
                String res = sdk.neovm().oep5().transferMulti(accounts,new Oep5Param[]{param,param2},  account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Oep5Param param = new Oep5Param(account.getAddressU160().toArray(), tokenId);
                String res = sdk.neovm().oep5().approve(account,param, account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Oep5Param param = new Oep5Param(account.getAddressU160().toArray(), tokenId);
                String res = sdk.neovm().oep5().takeOwnership(account,param, account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://139.219.108.204";
        String ip = "http://127.0.0.1";
//        ip= "http://139.219.138.201";
//        String ip = "http://101.132.193.149";
//        String ip = "http://polaris1.ont.io";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.neovm().oep5().setContractAddress(Helper.reverse("668a6f4c7a9bf793f359edeb2be9389dd8648626"));
        wm.openWalletFile("oep8.dat");
        return wm;
    }
}