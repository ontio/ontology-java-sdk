package demo.vmtest.types;

/**
 * @Description:
 * @date 2018/6/27
 */
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
