package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Identity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class WasmDemo {


    public static void main(String[] args){
        try {
            OntSdk ontSdk = getOntSdk();

            InputStream is = new FileInputStream("C:\\ZX\\contract.wasm");//IdContract
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

            //code
            String code = "0061736d0100000001250760017f0060017f017f60027f7f017f60037f7f7f0060027f7f0060037f7f7f017f60000002d0010c03656e76066d656d6f727902000103656e760a6d656d6f727942617365037f0003656e760d44656c65746553746f72616765000003656e760a47657453746f72616765000103656e76104a736f6e4d617368616c526573756c74000203656e76114a736f6e556e6d617368616c496e707574000303656e760a50757453746f72616765000403656e760d52756e74696d654e6f74696679000003656e760861727261794c656e000103656e76066d616c6c6f63000103656e76066d656d637079000503656e7606737472636d70000203050402020202060b027f0141000b7f0141000b070a0106696e766f6b65000d0afd03040700200120006a0b3e01037f20001006210220011006220320026a10072104200241004a044020042000200210081a0b200341004a0440200420036a2001200310081a0b20040b6d01047f200010062104200110062105200441004a04400340200020034102746a28020020026a2102200341016a22032004470d00200221000b05410021000b200541004a0440410021020340200120024102746a28020020006a2100200241016a22022005470d000b0b20000bc50201027f23012103230141106a240120032102024020002300100904402000230041136a1009450440200241082001100320022802002002280204100a230041176a1002220010050c020b20002300411b6a1009450440200241082001100320022802002002280204100b230041226a1002220010050c020b2000230041296a1009450440200241082001100320022802002002280204100c230041176a1002220010050c020b2000230041326a100945044020024108200110032002280200200228020410042300413d6a230041226a1002220010050c020b2000230041c2006a1009450440200241042001100320022802001001230041226a1002220010050c020b2000230041cd006a1009044041002100052002410420011003200228020010002300413d6a230041226a1002220010050b05230041056a21000b0b2003240120000b0b60010023000b5a696e697400696e69742073756363657373210061646400696e7400636f6e63617400737472696e670073756d41727261790061646453746f7261676500446f6e650067657453746f726167650064656c65746553746f72616765";
            code = Helper.toHexString(bys);
            System.out.println("Code:" + Helper.toHexString(bys));
            System.out.println("CodeAddress:" + Helper.getContractAddress(code, VmType.WASMVM.value()));

            ontSdk.vm().setCodeAddress(Helper.getContractAddress(code, VmType.WASMVM.value()));

            if(false) {
                Transaction tx = ontSdk.vm().makeDeployCodeTransaction(code, true, "name", "1.0", "1", "1", "1", VmType.WASMVM.value(),identity.ontid,0);
                String txHex = Helper.toHexString(tx.toArray());
                System.out.println(txHex);
                ontSdk.getConnect().sendRawTransaction(txHex);
            }
//            System.exit(0);
            String params = ontSdk.wasmvm().buildWasmContractJsonParam(new Object[]{20,30});
            System.out.println(params);
            Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(ontSdk.vm().getCodeAddress(),"add",params.getBytes(),VmType.WASMVM.value(),null,0,0);
            ontSdk.signTx(tx,new Account[][]{{}});
            ontSdk.getConnect().sendRawTransaction(tx.toHexString());

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

        wm.openWalletFile("RecordTxDemo.json");

        wm.vm().setCodeAddress("80f6bff7645a84298a1a52aa3745f84dba6615cf");
        return wm;
    }
}
