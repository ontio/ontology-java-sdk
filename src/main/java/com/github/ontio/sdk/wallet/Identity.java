/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.sdk.wallet;

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

