package ontology.network.rest;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 证书管理
 */
public class X509Imp implements X509TrustManager {
	@Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
	@Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }
	@Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }
}
