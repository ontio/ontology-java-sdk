package demo.vmtest.types;

/**
 * @Description:
 * @date 2018/6/27
 */
public class ByteArrayItem extends StackItems {
    public byte[] value;

    public ByteArrayItem(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] GetByteArray() {
        return value;
    }
}
