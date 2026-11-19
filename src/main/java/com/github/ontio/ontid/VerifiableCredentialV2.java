package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;


public class VerifiableCredentialV2 {
    @JSONField(name = "@context")
    public String[] context;
    public String id; // uuid
    public String[] type;
    public Object issuer; // issuer ontId, or an object contains ONTID
    public String validFrom;
    public String validUntil;
    public Object credentialSubject;
    public CredentialStatusV2 credentialStatus;
    public List<Evidence> evidence;
    public ProofV2 proof;

    public VerifiableCredentialV2() {
        this.id = "urn:uuid:" + UUID.randomUUID();
    }

    public String fetchIssuerOntId() {
        return Util.fetchId(this.issuer);
    }

    public byte[] genNeedSignData() {
        ProofV2 proof = this.proof;
        this.proof = this.proof.genNeedSignProof();
        String jsonStr = JSON.toJSONString(toSortedMap(this), SerializerFeature.MapSortField);
        this.proof = proof;
        return jsonStr.getBytes();
    }

    public Object toSortedMap(Object obj) {
        if (obj instanceof Map) {
            TreeMap<String, Object> sortedMap = new TreeMap<>();
            ((Map<?, ?>) obj).forEach((k, v) ->
                    sortedMap.put(String.valueOf(k), this.toSortedMap(v)));
            return sortedMap;
        } else if (obj instanceof List) {
            List<Object> sortedList = new ArrayList<>();
            for (Object item : (List<?>) obj) {
                sortedList.add(this.toSortedMap(item));
            }
            return sortedList;
        } else if (obj instanceof VerifiableCredentialV2) {
            TreeMap<String, Object> sortedMap = new TreeMap<>();
            try {
                for (java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    sortedMap.put(field.getName(), this.toSortedMap(value));
                }
            } catch (IllegalAccessException ignore) {
            }
            return sortedMap;
        }
        return obj;
    }

    public String findSubjectId() {
        return Util.fetchId(credentialSubject);
    }
}
