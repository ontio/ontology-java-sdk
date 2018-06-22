package com.github.neo.core;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.network.exception.RpcException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @date 2018/6/22
 */
public class NeoRpc {

    public static Object sendRawTransaction(String url,String sData) throws Exception {
        Object result = call(url,"sendrawtransaction", new Object[]{sData});
        return result;
    }
    public static Object getBalance(String url,String contractAddr,String addr) throws Exception {
        Object result = call(url,"getstorage", new Object[]{contractAddr,addr});
        return result;
    }
    public static Object call(String url,String method, Object... params) throws RpcException, IOException {
        Map req = makeRequest(method, params);
        Map response = (Map) send(url,req);
        if (response == null) {
            throw new RpcException(0, ErrorCode.ConnectUrlErr(  url + "response is null. maybe is connect error"));
        }
        else if (response.get("result")  != null) {
            return response.get("result");
        }
        else if (response.get("Result")  != null) {
            return response.get("Result");
        }
        else if (response.get("error") != null) {
            throw new RpcException(0, JSON.toJSONString(response));
        }
        else {
            throw new RpcException(0,JSON.toJSONString(response));
        }
    }

    private static Map makeRequest(String method, Object[] params) {
        Map request = new HashMap();
        request.put("jsonrpc", "2.0");
        request.put("method", method);
        request.put("params", params);
        request.put("id", 1);
        System.out.println(String.format("POST %s", JSON.toJSONString(request)));
        return request;
    }


    public static Object send(String url,Object request) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection)  new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
                w.write(JSON.toJSONString(request));
            }
            try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
                StringBuffer temp = new StringBuffer();
                int c = 0;
                while ((c = r.read()) != -1) {
                    temp.append((char) c);
                }
                //System.out.println("result:"+temp.toString());
                return JSON.parseObject(temp.toString(), Map.class);
            }
        } catch (IOException e) {
        }
        return null;
    }
}
