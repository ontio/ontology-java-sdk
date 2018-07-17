package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.common.WalletQR;
import com.github.ontio.crypto.MnemonicCode;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Scrypt;

import java.util.Map;

/**
 *
 *
 */
public class MnemonicDemo {
    public static void main(String[] args) {

        try {
            //Mnemonic Codes
            String code = MnemonicCode.generateMnemonicCodesStr();

            //get prikey from FromMnemonicCodes
            byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(code);
            System.out.println(Helper.toHexString(prikey));

            //get keystore
            Scrypt scrypt = new Scrypt();
            com.github.ontio.sdk.wallet.Account account = new com.github.ontio.sdk.wallet.Account();
            //TODO change scrypt and account value
            Map keystore = WalletQR.exportAccountQRCode(scrypt,account);
            System.out.println(JSON.toJSONString(keystore));

            //import keystore
            String prikey2 = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(keystore),"password");

            //import from WIF
            byte[] prikey3 = Account.getPrivateKeyFromWIF("");

            //create account or from prikey
            Account acct = new Account(SignatureScheme.SHA256WITHECDSA);
            Account acct2 = new Account(prikey3,SignatureScheme.SHA256WITHECDSA);

            //WalletQR.exportAccountQRCode()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
