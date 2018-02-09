package ontology.sdk.wallet;

import com.alibaba.fastjson.JSON;

/**
 * Created by zx on 2018/1/11.
 */
public class Scrypt {
    private int n = 16384;
    private int r = 8;
    private int p = 8;
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
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
