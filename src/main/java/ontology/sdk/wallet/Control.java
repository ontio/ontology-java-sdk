package ontology.sdk.wallet;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
    public Control(){

    }
    public Control(String key,String id){
        this.key = key;
        this.algorithm = "ECDSA";
        this.id = id;
        this.parameters.put("curve","secp256r1");
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}