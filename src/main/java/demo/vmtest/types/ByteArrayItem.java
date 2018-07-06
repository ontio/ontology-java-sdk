package demo.vmtest.types;

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
