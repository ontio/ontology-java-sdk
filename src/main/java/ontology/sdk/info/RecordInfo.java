package ontology.sdk.info;

import com.alibaba.fastjson.JSON;

/**
 * Created by zx on 2018/1/26.
 */
public class RecordInfo {
    public String key;
    public String value;
    public String ontid;
    public String opreation;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
