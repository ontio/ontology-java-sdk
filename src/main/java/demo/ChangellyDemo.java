package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.network.rest.http;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 */

public class ChangellyDemo {
    //https://changelly.com/developers#keys
    static String API_KEY = "1903148a33714fbdb783de1e6b67dbd4";
    static String API_SECRET = "44fe665d0d0c208f8e5efd46c5f6da146135c870d61c9c66d7223103309f73c8";
    public static void main(String[] args) {

        try {
            Map map = new LinkedHashMap();
            if(false){
                Map req =new HashMap();
                req.put("from","btc");
                req.put("to","eth");
                req.put("address","0xE1B305994aFa1EadAd5EB1e534b6CF9b9E76e2a8");
                req.put("amount",1);
                req.put("extraId",2);

                map.put("jsonrpc","2.0");
                map.put("id",1);
                map.put("method","createTransaction");
                map.put("params",req);
            }
            if(false){
                Map req =new HashMap();
                //req.put("from","btc");
               // req.put("to","eth");
                req.put("address","0xE1B305994aFa1EadAd5EB1e534b6CF9b9E76e2a8");
                req.put("limit",10);
                req.put("offset",0);

                map.put("jsonrpc","2.0");
                map.put("id",1);
                map.put("method","getTransactions");
                map.put("params",req);
            }
            if(true){
                Map req =new HashMap();
                req.put("id","aa85eb250b6d");

                map.put("jsonrpc","2.0");
                map.put("id",1);
                map.put("method","getStatus");
                map.put("params",req);
            }
            if (false) {
                Map req =new HashMap();
                req.put("from","btc");
                req.put("to","eth");
                req.put("amount",1);

                map.put("jsonrpc", "2.0");
                map.put("id", 1);
                map.put("method","getExchangeAmount");
                map.put("params",req);
            }
            if (false) {
                map.put("jsonrpc", "2.0");
                map.put("id", 1);
                map.put("method", "getCurrencies");
                map.put("params", new Object[]{});
            }
            String text = JSON.toJSONString(map);//.replace(":",": ").replace(",",", ");
            byte[] sign =  Digest.hmacSha512(API_SECRET.getBytes(),text.getBytes());
            System.out.println(Helper.toHexString(sign));
            Map<String,String> header = new HashMap<>();
            header.put("api-key",API_KEY);
            header.put("sign",Helper.toHexString(sign));
            String result = http.post("https://api.changelly.com",header,text,false);
            System.out.println(result);
        }catch (Exception e){
        e.printStackTrace();}
    }
}
