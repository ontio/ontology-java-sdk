package ontology.sdk.websocket;

import com.alibaba.fastjson.JSON;

import java.util.*;

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