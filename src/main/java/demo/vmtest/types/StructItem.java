package demo.vmtest.types;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructItem extends StackItems {
    public List<StackItems> stackItems = new ArrayList<>();

    public StructItem(List<StackItems> stackItems) {
        this.stackItems = stackItems;
    }

    @Override
    public boolean Equals(StackItems item) {
        return this.equals(item);
    }

    @Override
    public BigInteger GetBigInteger() {
        return null;
    }

    @Override
    public boolean GetBoolean() {
        return true;
    }

    @Override
    public byte[] GetByteArray() {
        return null;
    }

    @Override
    public InteropItem GetInterface() {
        return null;
    }

    @Override
    public StackItems[] GetArray() {
        return null;
    }

    @Override
    public StackItems[] GetStruct() {
        return stackItems.toArray(new StackItems[stackItems.size()]);
    }

    @Override
    public Map<StackItems, StackItems> GetMap() {
        return null;
    }

    public void Add(StackItems items) {
        stackItems.add(items);
    }

    public int Count() {
        return stackItems.size();
    }
}
