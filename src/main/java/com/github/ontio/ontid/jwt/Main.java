package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        String jsonStr = "[{\n" +
                "    \"id\": \"did:example:ebfeb1f712ebc6f1c276e12ec21\",\n" +
                "    \"name\": \"Jayden Doe\",\n" +
                "    \"spouse\": \"did:example:c276e12ec21ebfeb1f712ebc6f1\"\n" +
                "  }, {\n" +
                "    \"id\": \"did:example:c276e12ec21ebfeb1f712ebc6f1\",\n" +
                "    \"name\": \"Morgan Doe\",\n" +
                "    \"spouse\": \"did:example:ebfeb1f712ebc6f1c276e12ec21\"\n" +
                "  }]";
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            System.out.println(jsonObject.getString("id"));
        } else {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            Set<String> subIdSet = new TreeSet<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                subIdSet.add(object.getString("id"));
            }
            String[] ids = new String[]{};
            ids = subIdSet.toArray(ids);
            System.out.println(Arrays.toString(ids));
        }
    }
}
