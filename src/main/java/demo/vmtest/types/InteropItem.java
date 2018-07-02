package demo.vmtest.types;

public class InteropItem extends StackItems {
    public byte[] value;

    public InteropItem(byte[] value) {
        this.value = value;
    }

    @Override
    public InteropItem GetInterface() {
        return this;
    }
}