package ontology.sdk.wallet;

import ontology.core.contract.ContractParameterType;
import com.alibaba.fastjson.JSON;

public class Contract{
    public String script;
    public ContractParameterType[] parameters;
    public boolean deployed;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}