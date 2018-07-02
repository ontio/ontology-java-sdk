package demo.vmtest.vm;

import com.github.ontio.common.Helper;
import demo.vmtest.types.*;

import java.util.ArrayList;
import java.util.List;

public class RandomAccessStack {
    public List<StackItems> e = new ArrayList<StackItems>();

    public void Push(StackItems t) {
        Insert(0, t);
    }

    public String info() {
        String str = "[";
        for (int i = 0; i < e.size(); i++) {
            String className = e.get(i).getClass().getName().split("types.")[1].replace("Item", "");
//            if(e.get(i) instanceof ByteArrayItem) {
//                className = "ByteArrayItem";
//            } else if(e.get(i) instanceof IntegerItem) {
//                className = "IntegerItem";
//            } else if(e.get(i) instanceof BoolItem) {
//                className = "BoolItem";
//            } else if(e.get(i) instanceof ArrayItem) {
//                StackItems[] arr = ((ArrayItem)e.get(i)).stackItems;
//                className = "ArrayItem" ;
//            }
            str = str + className;
            if (i != e.size() - 1) {
                str = str + ",";
            }
        }
        return str + "]";
    }

    public String info2() {
        String str = "[";
        for (int i = 0; i < e.size(); i++) {
            String className = e.get(i).getClass().getName().split("types.")[1].replace("Item", "");
            if (e.get(i) instanceof ByteArrayItem) {
                className = className + "(" + Helper.toHexString(e.get(i).GetByteArray()) + ")";
            } else if (e.get(i) instanceof IntegerItem) {
                className = className + "(" + e.get(i).GetBigInteger().longValue() + ")";
            } else if (e.get(i) instanceof BoolItem) {
                className = className + "(" + e.get(i).GetBoolean() + ")";
            } else if (e.get(i) instanceof ArrayItem) {
                StackItems[] arr = ((ArrayItem) e.get(i)).stackItems;
                className = className + "[";
                for (int j = 0; j < arr.length; j++) {
                    if (arr[j] instanceof ByteArrayItem) {
                        className = className + "ByteArray:" + Helper.toHexString(arr[j].GetByteArray());
                    } else if (arr[j] instanceof IntegerItem) {
                        className = className + "Integer:" + arr[j].GetBigInteger().longValue();
                    } else if (arr[j] instanceof BoolItem) {
                        className = className + "Bool:" + arr[j].GetBoolean();
                    } else if (arr[j] instanceof ArrayItem) {
                        className = className + "ArrayItem";
                    }
                    if (j < arr.length - 1) {
                        className = className + ",";
                    }
                }
                className = className + "]";
            }
            str = str + className;
            if (i != e.size() - 1) {
                str = str + ",";
            }
        }
        return str + "]";
    }

    public int Count() {
        return e.size();
    }

    public void Insert(int index, StackItems t) {
        if (t == null) {
            return;
        }
        index = e.size() - index;
        e.add(index, t);
    }

    public StackItems Peek(int index) {
        int l = e.size();
        if (index >= l) {
            return null;
        }
        index = l - index;
        return e.get(index - 1);
    }

    public StackItems Remove(int index) {
        if (index >= e.size()) {
            return null;
        }
        index = e.size() - index;
        return e.remove(index - 1);
    }

    public void Set(int index, StackItems t) {
        e.add(index, t);
    }

    public void Push(int index, StackItems t) {
        Insert(0, t);
    }

    public StackItems Pop() {
        return Remove(0);
    }

    public void Swap(int i, int j) {
        StackItems tmp = e.get(i);
        e.set(i, e.get(j));
        e.set(j, tmp);
    }

    public void CopyTo(RandomAccessStack stack) {
        stack.e.addAll(this.e);
    }
}