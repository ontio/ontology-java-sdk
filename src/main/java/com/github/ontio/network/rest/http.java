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

package com.github.ontio.network.rest;
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

public class http {
    private static final String DEFAULT_CHARSET = "UTF-8";
    

    public static String post(String url, String body, boolean https) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
    	URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        try (OutputStream out = http.getOutputStream()) {
	        out.write(body.getBytes(DEFAULT_CHARSET));
	        out.flush();
        }
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
        	try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
        		String str = null;
        		while((str = reader.readLine()) != null) {
        			sb.append(str);str = null;
        		}
        	}
        }
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
    public static String delete(String url, String body, boolean https) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        try (OutputStream out = http.getOutputStream()) {
            out.write(body.getBytes(DEFAULT_CHARSET));
            out.flush();
        }
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
                String str = null;
                while((str = reader.readLine()) != null) {
                    sb.append(str);str = null;
                }
            }
        }
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
//    public static String delete(String url, String body) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
//        if(url.startsWith("https")){
//            return delete(url, body, true);
//        }else{
//            return delete(url, body, false);
//        }
//    }
//    public static String post(String url, String body) throws Exception{
//    	System.out.println(String.format("POST url=%s", url));
//    	if(url.startsWith("https")){
//    		return post(url, body, true);
//    	}else{
//    		return post(url, body, false);
//    	}
//    }
    public static String delete(String url, Map<String, String> params, Map<String, Object> body) throws Exception {
        if(url.startsWith("https")){
            return delete(url+cvtParams(params), JSON.toJSONString(body),true);
        }else{
            return delete(url+cvtParams(params), JSON.toJSONString(body), false);
        }
    }

    public static String post(String url, Map<String, String> params, Map<String, Object> body) throws Exception {
        System.out.println(String.format("POST url=%s", url));
        if(url.startsWith("https")){
            return post(url+cvtParams(params), JSON.toJSONString(body), true);
        }else{
            return post(url+cvtParams(params), JSON.toJSONString(body), false);
        }
    }

    private static String get(String url  ,boolean https) throws Exception {
    	URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setConnectTimeout(50000);
        http.setReadTimeout(50000);
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
        	try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
        		String str = null;
        		while((str = reader.readLine()) != null) {
        			sb.append(str);str = null;
        		}
        	}
        }
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
    private static String get(String url) throws Exception {
    	System.out.println(String.format(" GET url=%s, params=%s", url, null));
    	if(url.startsWith("https")){
    		return get(url, true);
        }else{
        	return get(url, false);
        }
    }

    public static String get(String url, Map<String, String> params) throws Exception {
        if(url.startsWith("https")){
            return get(url+cvtParams(params), true);
        }else{
            return get(url+cvtParams(params), false);
        }
    }
    

    private static String cvtParams( Map<String, String> params){
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
     *
     * @param objs
     * @throws IOException
     */
    private static void close(Closeable... objs) throws IOException {
    	if(objs != null	&& objs.length > 0) {
    		Arrays.stream(objs).forEach(p -> {try {p.close(); } catch(Exception e){}});
    	}
    }
}