package ontology.sdk.wallet;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zx on 2018/1/11.
 */
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    public Contract contract = new Contract();
    public Object extra = null;
    public Account(){
        this.algorithm = "ECDSA";
        this.parameters.put("curve","secp256r1");
        this.extra = null;
    }
    public void setExtra(Object extra){
        this.extra = extra;
    }
    public Object getExtra(){
        return extra;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

