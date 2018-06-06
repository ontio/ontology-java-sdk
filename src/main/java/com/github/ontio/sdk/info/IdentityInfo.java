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

package com.github.ontio.sdk.info;

import com.alibaba.fastjson.JSON;

/**
 *
 */
public class IdentityInfo {
    public String ontid;
    public String pubkey;

    public String encryptedPrikey;
    public String addressU160;
    private String prikey;
    private String prikeyWif;

    public void setPrikey(String prikey) {
        //this.prikey = prikey;
    }

    public void setPriwif(String priwif) {
        //this.prikeyWif = priwif;
    }

    public String getPrikeyWif() {
        return prikeyWif;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}