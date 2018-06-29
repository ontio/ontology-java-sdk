package demo.vmtest.types;

import demo.vmtest.vm.Interop;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class  StackItems{
    public boolean Equals( StackItems other) {
        return false;
    }
    public BigInteger GetBigInteger() {
        return new BigInteger("");
    }
    public boolean GetBoolean(){
        return false;
    }
    public byte[]  GetByteArray() {
        return new byte[]{};
   }
    public Interop GetInterface() {
        return  new Interop();
    }
    public StackItems[] GetArray(){
        return new StackItems[0];
    }
    public StackItems[] GetStruct(){
        return new StackItems[0];
    }
    public Map<String,StackItems> GetMap(){
     return new HashMap<>();
    }
}