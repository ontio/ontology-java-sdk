package demo.vmtest.vm;

import demo.vmtest.types.StackItems;

import java.util.ArrayList;
import java.util.List;

public class RandomAccessStack{
    public List<StackItems> e = new ArrayList<StackItems>();
    public void Push(StackItems t){
        Insert(0, t);
    }
    public String info(){
        String str = "[";
        for(int i=0;i<e.size();i++){
            str = str + e.get(i).getClass().getName().split("types.")[1];
            if(i != e.size()-1){
                str = str  +",";
            }
        }
        return str+"]";
    }

    public int Count() {
        return e.size();
    }
    public void Insert(int index, StackItems t) {
        index = e.size() - index;
        e.add(index, t);
    }
    public StackItems Peek(int index) {
        int l = e.size();
        if (index >= l){
            return null;
        }
        index = l-index;
        return e.get(index-1);
    }
    public StackItems Remove(int index) {
        if(index >= e.size()){
            return null;
        }
        index = e.size()-index;
//        StackItems tmp = e.get(index);
//        e.remove(index);
        return e.remove(index-1);
    }
    public void Set(int index,StackItems t) {
        e.add(index,t);
    }
    public void Push(int index,StackItems t) {
        Insert(0,t);
    }
    public StackItems Pop() {
        return Remove(0);
    }
    public void Swap(int i,int j) {
        StackItems tmp = e.get(i);
        e.set(i,e.get(j));
        e.set(j,tmp);
    }
    public void CopyTo(RandomAccessStack stack) {
        stack.e.addAll(this.e);
    }
}