package ontology.sdk.wallet;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 2018/1/11.
 */
public class Identity {
    public String label = "";
    public String ontid = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public List<Control> controls = new ArrayList<Control>();
    public  Object extra = null;
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

