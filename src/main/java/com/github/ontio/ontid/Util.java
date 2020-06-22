package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.sdk.exception.SDKException;

import java.util.Set;
import java.util.TreeSet;

public class Util {

    public static int getIndexFromPubKeyURI(String pubKeyURI) throws Exception {
        String[] keyInfo = pubKeyURI.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid pubKeyURI %s", pubKeyURI));
        }
        return Integer.parseInt(keyInfo[1]);
    }

    public static String getOntIdFromPubKeyURI(String pubKeyURI) throws Exception {
        String[] keyInfo = pubKeyURI.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid pubKeyURI %s", pubKeyURI));
        }
        return keyInfo[0];
    }

    // fetch "id" field of object
    // if object doesn't contain "id" field, return ""
    // if object is array, return ""
    public static String fetchId(Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object.getClass().isPrimitive()) {
            return "";
        }
        if (object instanceof JSONArray) {
            return "";
        }
        if (object.getClass().isArray()) {
            return "";
        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
        String id = jsonObject.getString("id");
        if (id == null) {
            return "";
        }
        return id;
    }
}
