package demo.vmtest.types;

import com.github.ontio.common.Helper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


public class MapItem extends StackItems {
    public Map<StackItems, StackItems> map = new HashMap<>();

    public MapItem() {
    }

    public void Add(StackItems key, StackItems value) {
        map.put(key, value);
    }

    public void Clear() {
        map.clear();
    }

    public boolean ContainsKey(StackItems item) {
        return map.get(item) != null;
    }

    public void Remove(StackItems item) {
        map.remove(item);
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
        return null;
    }

    @Override
    public Map<StackItems, StackItems> GetMap() {
        return map;
    }

    public StackItems TryGetValue(StackItems key) {
        for (Map.Entry<StackItems, StackItems> e : map.entrySet()) {
            if (e.getKey() instanceof ByteArrayItem) {
                if (key instanceof ByteArrayItem) {
                    if (Helper.toHexString(e.getKey().GetByteArray()).equals(Helper.toHexString(key.GetByteArray()))) {
                        return e.getValue();
                    }
                } else if (key instanceof IntegerItem) {
                    if (e.getKey().GetBigInteger().compareTo(key.GetBigInteger()) > 0) {
                        return e.getValue();
                    }
                }

            }
        }
        return null;
    }
}
