package demo.vmtest.vm;

import com.github.ontio.common.ErrorCode;

public enum VMState{
    NONE(0x00),
    HALT(0x01),
    FAULT(0x02),
    BREAK(0x04),
    INSUFFICIENT_RESOURCE(0x10);
    public int value;

    private VMState(int b) {
        this.value = b;
    }
    public int getValue(){
        return value;
    }
    public static VMState valueOf(int b) throws Exception {
        for (VMState k : VMState.values()) {
            if (k.value == b) {
                return k;
            }
        }
        throw new Exception(ErrorCode.ParamError);
    }
}