package com.github.ontio.smartcontract.nativevm.abi;

import com.github.ontio.common.Address;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description:
 * @date 2018/6/10
 */
public class Struct {
    public List list = new ArrayList();
    public Struct(){

    }
    public Struct add(Object... objs){
        for(int i=0;i<objs.length;i++){
            list.add(objs[i]);
        }
        return this;
    }
}
