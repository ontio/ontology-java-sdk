package demo;

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.common.WalletQR;
import com.github.ontio.crypto.MnemonicCode;

/**
 *
 *
 */
public class MnemonicDemo {
    public static void main(String[] args) {

        try {
            String code = MnemonicCode.generateMnemonicCodesStr();
            byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(code);
            System.out.println(Helper.toHexString(prikey));

            //WalletQR.exportAccountQRCode()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
