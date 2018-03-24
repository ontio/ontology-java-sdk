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

package com.github.ontio.sdk.websocket;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 *
 */
public class MsgQueue {

    private static Set<String> set = new HashSet<String>();
    private static String heartBeat = "";
    private static boolean changeHeartBeat = false;
    public static boolean getChangeFlag() {
        return changeHeartBeat;
    }
    public static void setChangeFlag(boolean b) {
       changeHeartBeat = b;
    }
    public static void addResult(Result obj) {
        set.add(JSON.toJSONString(obj));
    }
    public static boolean addHeartBeat(Result obj) {
        if (heartBeat.equals(JSON.toJSONString(obj))) {
            return false;
        }
        changeHeartBeat = true;
        heartBeat = JSON.toJSONString(obj);
        return true;
    }
    public static  Set<String> getResultSet(){
        Set<String> rt = new HashSet<String>();
        rt.addAll(set);
        return rt;
    }
    public static  String getHeartBeat(){
        return heartBeat;
    }

    public static  void removeResult(String ele){
        set.remove(ele);
    }
    public static int resultSize() {
        return set.size();
    }
}