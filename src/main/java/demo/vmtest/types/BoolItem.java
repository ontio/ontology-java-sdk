package demo.vmtest.types;

import com.github.ontio.common.Helper;

import java.math.BigInteger;

public class BoolItem extends StackItems {
    public boolean value;

    public BoolItem(boolean val) {
        value = val;
    }

    @Override
    public boolean GetBoolean() {
        return value;
    }

    @Override
    public BigInteger GetBigInteger() {
        if (value) {
            return BigInteger.valueOf(1);
        } else {
            return BigInteger.valueOf(0);
        }
    }

    @Override
    public byte[] GetByteArray() {
        return value == true ? new byte[]{1} : new byte[]{0};
    }
}
