package demo.vmtest.types;

import java.math.BigInteger;

/**
 * @Description:
 * @date 2018/6/27
 */
public class IntegerItem extends StackItems {
    public BigInteger value;
    public IntegerItem(BigInteger value){
       this.value = value;
    }
    @Override
    public BigInteger GetBigInteger() {
        return value;
    }
}
