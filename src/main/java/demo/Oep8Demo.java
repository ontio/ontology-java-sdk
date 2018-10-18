package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.oep8.Oep8State;
import com.github.ontio.core.oep8.TransferFrom;

public class Oep8Demo {
    public static void main(String[] args) {
        try {
            OntSdk sdk = getOntSdk();
            Account account = sdk.getWalletMgr().getAccount("AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p", "xinhao");
            Account account1 = sdk.getWalletMgr().getAccount("ARR5ywDEx3ybXkMGmZPFYu9hiC8J4xvNdc", "xinhao");
            Account account2 = sdk.getWalletMgr().getAccount("AacHGsQVbTtbvSWkqZfvdKePLS6K659dgp", "xinhao");
            if (false){
                String txhash = sdk.neovm().oep8().sendInit(account, account,200000, 0);
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                String name = sdk.neovm().oep8().queryName(tokenId);
                System.out.println("name: " + name);
                return;
            }
            if(false){
                byte[] tokenId = Helper.hexToBytes("01");
                long name = sdk.neovm().oep8().queryTotalSupply(tokenId);
                System.out.println("symbol: " + sdk.neovm().oep8().querySymbol(tokenId));
                System.out.println("totalsupply: " + name);
                return;
            }
            if(false){
//                有问题
                byte[] tokenId = Helper.hexToBytes("01");
                long name = sdk.neovm().oep8().queryDecimals();
                System.out.println("decimal: " + name);
            }
            if(true){
                byte[] tokenId = Helper.hexToBytes("09");
                long name = sdk.neovm().oep8().queryBalanceOf(account.getAddressU160().toBase58(), tokenId);
                System.out.println("balance: " + name);
                return;
            }
            if(true){
                String txhash = sdk.neovm().oep8().sendCompound(account, account, 71378, 0);
                Thread.sleep(3);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                String txhash = sdk.neovm().oep8().sendTransfer(account,account1.getAddressU160().toBase58(),tokenId,10,account,20000,0);
                System.out.println("txhash: " + txhash);
                return;
            }

            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                String res = sdk.neovm().oep8().queryAllowance(account.getAddressU160().toBase58(),account1.getAddressU160().toBase58(), tokenId);
                System.out.println("res: " + res);
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                String res = sdk.neovm().oep8().sendApprove(account,account1.getAddressU160().toBase58(), tokenId, 1, account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                String res = sdk.neovm().oep8().sendTransferFrom(account1,account.getAddressU160().toBase58(),account1.getAddressU160().toBase58(), tokenId, 1, account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Account[] accounts = new Account[]{account};
                Oep8State state = new Oep8State(account.getAddressU160().toArray(), account1.getAddressU160().toArray(), tokenId, 1);
                Oep8State state2 = new Oep8State(account.getAddressU160().toArray(), account2.getAddressU160().toArray(), tokenId, 1);
                String res = sdk.neovm().oep8().sendTransferMulti(accounts,new Oep8State[]{state},  account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Account[] accounts = new Account[]{account, account1};
                Oep8State state = new Oep8State(account.getAddressU160().toArray(), account1.getAddressU160().toArray(), tokenId, 1);
                Oep8State state2 = new Oep8State(account.getAddressU160().toArray(), account2.getAddressU160().toArray(), tokenId, 1);
                String res = sdk.neovm().oep8().sendApproveMulti(accounts,new Oep8State[]{state, state2},  account, 20000, 0);
                System.out.println("res: " + res);
                Thread.sleep(3000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(res));
                return;
            }
            if (false){
                byte[] tokenId = Helper.hexToBytes("01");
                Account[] accounts = new Account[]{account1, account2};
                TransferFrom state = new TransferFrom(account1.getAddressU160().toArray(),account.getAddressU160().toArray(), account1.getAddressU160().toArray(), tokenId, 1);
                TransferFrom state2 = new TransferFrom(account2.getAddressU160().toArray(),account.getAddressU160().toArray(), account2.getAddressU160().toArray(), tokenId, 1);
                String res = sdk.neovm().oep8().sendTransferFromMulti(accounts,new TransferFrom[]{state, state2}, account, 27740, 0);
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
        wm.neovm().oep8().setContractAddress(Helper.reverse("1c000c989817eefeb6dd58cb9551a0a3a0ce2ee5"));
        wm.openWalletFile("oep8.dat");
        return wm;
    }
}
