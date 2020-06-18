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

    public static String fetchId(Object credentialSubject) throws Exception {
        String jsonStr = JSON.toJSONString(credentialSubject);
        if (jsonStr.startsWith("[")) {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            Set<String> subIdSet = new TreeSet<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                if (id != null) {
                    subIdSet.add(id);
                }
            }
            // credential subject doesn't contain id field
            if (subIdSet.size() == 0) {
                return "";
            }
            if (subIdSet.size() == 1) {
                return (String) subIdSet.toArray()[0];
            }
            // more than one subjectId
            throw new SDKException("credential cannot unify subject id");
        } else {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            String id = jsonObject.getString("id");
            if (id == null) {
                return "";
            }
            return id;
        }
    }
}
