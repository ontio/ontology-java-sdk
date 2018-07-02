package demo.vmtest.types;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class StackItems {
    public boolean Equals(StackItems other) {
        return false;
    }

    public BigInteger GetBigInteger() {
        return new BigInteger("");
    }

    public boolean GetBoolean() {
        return false;
    }

    public byte[] GetByteArray() {
        return new byte[]{};
    }

    public InteropItem GetInterface() {
        return null;
    }

    public StackItems[] GetArray() {
        return new StackItems[0];
    }

    public StackItems[] GetStruct() {
        return new StackItems[0];
    }

    public Map<StackItems, StackItems> GetMap() {
        return new HashMap<>();
    }
}