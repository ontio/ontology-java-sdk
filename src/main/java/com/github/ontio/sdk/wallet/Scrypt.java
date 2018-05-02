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

/**
 */
public class Scrypt {
    private int n = 16384;
    private int r = 8;
    private int p = 8;
    private int DkLen = 64;
    private String Salt;
    public Scrypt(){
    }
    public Scrypt(int n,int r,int p){
        this.n = n;
        this.r = r;
        this.p = p;
    }
    public void setN(int n){
        this.n = n;
    }
    public int getN(){
        return n;
    }
    public void setR(int r){
        this.r = r;
    }
    public int getR(){
        return r;
    }
    public void setP(int p){
        this.p = p;
    }
    public int getP(){
        return p;
    }
    public void setDkLen(int DkLen){
        this.DkLen = DkLen;
    }
    public int getDkLen(){
        return DkLen;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
