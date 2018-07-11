package demo;

import com.github.ontio.common.WalletQR;
import com.github.ontio.sdk.wallet.Wallet;

/**
 *
 *
 */
public class QrCodeDemo {
    public static void main(String[] args) {
        String keystore = "{\"address\":\"AG9W6c7nNhaiywcyVPgW9hQKvUYQr5iLvk\",\"key\":\"+UADcReBcLq0pn/2Grmz+UJsKl3ryop8pgRVHbQVgTBfT0lho06Svh4eQLSmC93j\",\"parameters\":{\"curve\":\"secp256r1\"},\"label\":\"11111\",\"scrypt\":{\"dkLen\":64,\"n\":4096,\"p\":8,\"r\":8},\"salt\":\"IfxFV0Fer5LknIyCLP2P2w==\",\"type\":\"I\",\"algorithm\":\"ECDSA\"}";
        try {
            String prikey = WalletQR.getPriKeyFromQrCode(keystore,"111111");
            System.out.println(prikey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
