package ontology.network.rest;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.alibaba.fastjson.JSON;

public class RestHttp {
    private static final String DEFAULT_CHARSET = "UTF-8";
    

    public static String post(String url, String body, boolean https) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
    	// 创建链接
    	URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        // 连接超时
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509Imp()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        // 写入参数
        try (OutputStream out = http.getOutputStream()) {
	        out.write(body.getBytes(DEFAULT_CHARSET));
	        out.flush();
        }
        // 获取返回
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
        	try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
        		String str = null;
        		while((str = reader.readLine()) != null) {
        			sb.append(str);str = null;
        		}
        	}
        }
        // 关闭链接
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
    public static String delete(String url, String body, boolean https) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        // 创建链接
        URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        // 连接超时
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509Imp()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        // 写入参数
        try (OutputStream out = http.getOutputStream()) {
            out.write(body.getBytes(DEFAULT_CHARSET));
            out.flush();
        }
        // 获取返回
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
                String str = null;
                while((str = reader.readLine()) != null) {
                    sb.append(str);str = null;
                }
            }
        }
        // 关闭链接
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
    public static String delete(String url, String body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        if(url.startsWith("https")){
            return delete(url, body, true);
        }else{
            return delete(url, body, false);
        }
    }
    public static String post(String url, String body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
//    	System.out.println(String.format("POST url=%s, body=%s", url, body));
    	if(url.startsWith("https")){
    		return post(url, body, true);
    	}else{
    		return post(url, body, false);
    	}
    }
    public static String delete(String url, Map<String, String> params, Map<String, Object> body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        return delete(url+initParams(params), JSON.toJSONString(body));
    }
    public static String post(String url, Map<String, String> params, Map<String, String> body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
    	return post(url+initParams(params), JSON.toJSONString(body));
    }
    public static String postObject(String url, Map<String, String> params, Map<String, Object> body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        return post(url+initParams(params), JSON.toJSONString(body));
    }
    /**
     * Get请求
     *
     * @param url
     * @param https
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws KeyManagementException
     */
    private static String get(String url  ,boolean https) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyManagementException {
    	// 创建链接
    	URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        // 连接超时
        http.setConnectTimeout(50000);
        http.setReadTimeout(50000);
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509Imp()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        // 获取返回
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
        	try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
        		String str = null;
        		while((str = reader.readLine()) != null) {
        			sb.append(str);str = null;
        		}
        	}
        }
        // 关闭链接
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
    public static String get(String url) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    	System.out.println(String.format(" GET url=%s, params=%s", url, null));
    	if(url.startsWith("https")){
    		return get(url, true);
        }else{
        	return get(url, false);
        }
    }
    
    public static String get(String url, Map<String, String> params) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    	return get(url+initParams(params));
    }
    

    private static String initParams( Map<String, String> params){
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
				value = value == null ? value:URLEncoder.encode(value, DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            sb.append("&").append(key).append("=").append(value);
        }
        return "?"+sb.toString().substring(1);
    }
    
    /**
     * 关闭链接
     * 
     * @param objs
     * @throws IOException
     */
    public static void close(Closeable... objs) throws IOException {
    	if(objs != null	&& objs.length > 0) {
    		Arrays.stream(objs).forEach(p -> {try {p.close(); } catch(Exception e){}});
    	}
    }
}