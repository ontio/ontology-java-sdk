package ontology.sdk.info.abi;

import ontology.sdk.exception.Error;
import ontology.sdk.exception.SDKException;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;

/**
 * Created by zx on 2018/1/31.
 */
public class Parameter {
    public String name;
    public String type;
    public String value;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean setValue(Object value) {
        try {
            if(value == null) {
                this.value = null;
            }else if ("ByteArray".equals(type)) {
                byte[] tmp = (byte[]) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("String".equals(type)) {
                this.value = (String) value;
            } else if ("Boolean".equals(type)) {
                boolean tmp = (boolean) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Integer".equals(type)) {
                int tmp = (int) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Array".equals(type)) {
                Array tmp = (Array) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("InteropInterface".equals(type)) {
                Object tmp = (Object) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Void".equals(type)) {
            } else {
                throw new SDKException(Error.getDescArgError("type error"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
