package demo.vmtest.types;

import com.github.ontio.common.Helper;

import java.math.BigInteger;

public class IntegerItem extends StackItems {
    public BigInteger value;

    public IntegerItem(BigInteger value) {
        this.value = value;
    }

    @Override
    public BigInteger GetBigInteger() {
        return value;
    }

    @Override
    public byte[] GetByteArray() {
        return Helper.BigInt2Bytes(value);
    }
}
