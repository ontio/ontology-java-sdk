package demo.vmtest.types;

public class ArrayItem extends StackItems {
    public StackItems[] stackItems;

    public ArrayItem(StackItems[] items) {
        stackItems = items;
    }

    @Override
    public StackItems[] GetArray() {
        return stackItems;
    }
}
