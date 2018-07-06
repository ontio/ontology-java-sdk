package demo.vmtest.vm;

import com.github.ontio.core.scripts.ScriptOp;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import demo.vmtest.types.ArrayItem;
import demo.vmtest.types.MapItem;
import demo.vmtest.types.StackItems;
import demo.vmtest.types.StructItem;
import demo.vmtest.utils.PushData;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class OpExecList {
    public static Map<ScriptOp, OpExec> OpExecList = new HashMap();

    public static OpExec getOpExec(ScriptOp op) {
        if (OpExecList.size() == 0) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return OpExecList.get(op);
    }

    public static void init() throws Exception {
        Method opPushData = PushData.class.getMethod("opPushData", ExecutionEngine.class);
        Method opNop = PushData.class.getMethod("opNop", ExecutionEngine.class);
        Method opJmp = PushData.class.getMethod("opJmp", ExecutionEngine.class);
        Method opCall = PushData.class.getMethod("opCall", ExecutionEngine.class);
        Method opRet = PushData.class.getMethod("opRet", ExecutionEngine.class);
        Method opToAltStack = PushData.class.getMethod("opToAltStack", ExecutionEngine.class);
        Method opToDupFromAltStack = PushData.class.getMethod("opToDupFromAltStack", ExecutionEngine.class);
        Method opFromAltStack = PushData.class.getMethod("opFromAltStack", ExecutionEngine.class);
        Method opXDrop = PushData.class.getMethod("opXDrop", ExecutionEngine.class);
        Method opXSwap = PushData.class.getMethod("opXSwap", ExecutionEngine.class);
        Method opXTuck = PushData.class.getMethod("opXTuck", ExecutionEngine.class);
        Method opDepth = PushData.class.getMethod("opDepth", ExecutionEngine.class);
        Method opDrop = PushData.class.getMethod("opDrop", ExecutionEngine.class);
        Method opDup = PushData.class.getMethod("opDup", ExecutionEngine.class);
        Method opNip = PushData.class.getMethod("opNip", ExecutionEngine.class);
        Method opOver = PushData.class.getMethod("opOver", ExecutionEngine.class);
        Method opPick = PushData.class.getMethod("opPick", ExecutionEngine.class);
        Method opRoll = PushData.class.getMethod("opRoll", ExecutionEngine.class);
        Method opRot = PushData.class.getMethod("opRot", ExecutionEngine.class);
        Method opSwap = PushData.class.getMethod("opSwap", ExecutionEngine.class);
        Method opTuck = PushData.class.getMethod("opTuck", ExecutionEngine.class);
        Method opCat = PushData.class.getMethod("opCat", ExecutionEngine.class);
        Method opSubStr = PushData.class.getMethod("opSubStr", ExecutionEngine.class);
        Method opLeft = PushData.class.getMethod("opLeft", ExecutionEngine.class);
        Method opRight = PushData.class.getMethod("opRight", ExecutionEngine.class);
        Method opSize = PushData.class.getMethod("opSize", ExecutionEngine.class);

        Method opInvert = PushData.class.getMethod("opInvert", ExecutionEngine.class);
        Method opBigIntZip = PushData.class.getMethod("opBigIntZip", ExecutionEngine.class);
        Method opEqual = PushData.class.getMethod("opEqual", ExecutionEngine.class);
        Method opBigInt = PushData.class.getMethod("opBigInt", ExecutionEngine.class);
        Method opSign = PushData.class.getMethod("opSign", ExecutionEngine.class);
        Method opNot = PushData.class.getMethod("opNot", ExecutionEngine.class);
        Method opNz = PushData.class.getMethod("opNz", ExecutionEngine.class);
        Method opBoolZip = PushData.class.getMethod("opBoolZip", ExecutionEngine.class);
        Method opBigIntComp = PushData.class.getMethod("opBigIntComp", ExecutionEngine.class);
        Method opWithIn = PushData.class.getMethod("opWithIn", ExecutionEngine.class);
        Method opHash = PushData.class.getMethod("opHash", ExecutionEngine.class);

        Method opArraySize = PushData.class.getMethod("opArraySize", ExecutionEngine.class);
        Method opPack = PushData.class.getMethod("opPack", ExecutionEngine.class);
        Method opUnpack = PushData.class.getMethod("opUnpack", ExecutionEngine.class);
        Method opPickItem = PushData.class.getMethod("opPickItem", ExecutionEngine.class);
        Method opSetItem = PushData.class.getMethod("opSetItem", ExecutionEngine.class);
        Method opNewArray = PushData.class.getMethod("opNewArray", ExecutionEngine.class);
        Method opNewMap = PushData.class.getMethod("opNewMap", ExecutionEngine.class);
        Method opNewStruct = PushData.class.getMethod("opNewStruct", ExecutionEngine.class);

        Method opAppend = PushData.class.getMethod("opAppend", ExecutionEngine.class);
        Method opReverse = PushData.class.getMethod("opReverse", ExecutionEngine.class);
        Method opThrow = PushData.class.getMethod("opThrow", ExecutionEngine.class);
        Method opThrowIfNot = PushData.class.getMethod("opThrowIfNot", ExecutionEngine.class);

        Method validatePickItem = OpExecList.class.getMethod("validatePickItem", ExecutionEngine.class);


        OpExecList.put(ScriptOp.OP_PUSH0, new OpExec(ScriptOp.OP_PUSH0, "PUSH0", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSHBYTES1, new OpExec(ScriptOp.OP_PUSHBYTES1, "PUSHBYTES1", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSHBYTES75, new OpExec(ScriptOp.OP_PUSHBYTES75, "PUSHBYTES75", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSHDATA1, new OpExec(ScriptOp.OP_PUSHDATA1, "PUSHDATA1", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSHDATA2, new OpExec(ScriptOp.OP_PUSHDATA2, "PUSHDATA2", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSHDATA4, new OpExec(ScriptOp.OP_PUSHDATA4, "PUSHDATA4", opPushData, null));

        OpExecList.put(ScriptOp.OP_PUSHM1, new OpExec(ScriptOp.OP_PUSHM1, "PUSHM1", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH1, new OpExec(ScriptOp.OP_PUSH1, "PUSH1", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH2, new OpExec(ScriptOp.OP_PUSH2, "PUSH2", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH3, new OpExec(ScriptOp.OP_PUSH3, "PUSH3", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH4, new OpExec(ScriptOp.OP_PUSH4, "PUSH4", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH5, new OpExec(ScriptOp.OP_PUSH5, "PUSH5", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH6, new OpExec(ScriptOp.OP_PUSH6, "PUSH6", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH7, new OpExec(ScriptOp.OP_PUSH7, "PUSH7", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH8, new OpExec(ScriptOp.OP_PUSH8, "PUSH8", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH9, new OpExec(ScriptOp.OP_PUSH9, "PUSH9", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH10, new OpExec(ScriptOp.OP_PUSH10, "PUSH10", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH11, new OpExec(ScriptOp.OP_PUSH11, "PUSH11", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH12, new OpExec(ScriptOp.OP_PUSH12, "PUSH12", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH13, new OpExec(ScriptOp.OP_PUSH13, "PUSH13", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH14, new OpExec(ScriptOp.OP_PUSH14, "PUSH14", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH15, new OpExec(ScriptOp.OP_PUSH15, "PUSH15", opPushData, null));
        OpExecList.put(ScriptOp.OP_PUSH16, new OpExec(ScriptOp.OP_PUSH16, "PUSH16", opPushData, null));

        OpExecList.put(ScriptOp.OP_NOP, new OpExec(ScriptOp.OP_NOP, "NOP", opNop, null));
        OpExecList.put(ScriptOp.OP_JMP, new OpExec(ScriptOp.OP_JMP, "JMP", opJmp, null));
        OpExecList.put(ScriptOp.OP_JMPIF, new OpExec(ScriptOp.OP_JMPIF, "JMPIF", opJmp, null));
        OpExecList.put(ScriptOp.OP_JMPIFNOT, new OpExec(ScriptOp.OP_JMPIFNOT, "JMPIFNOT", opJmp, null));
        OpExecList.put(ScriptOp.OP_CALL, new OpExec(ScriptOp.OP_CALL, "CALL", opCall, null));
        OpExecList.put(ScriptOp.OP_RET, new OpExec(ScriptOp.OP_RET, "RET", opRet, null));
        OpExecList.put(ScriptOp.OP_APPCALL, new OpExec(ScriptOp.OP_APPCALL, "APPCALL", null, null));
        OpExecList.put(ScriptOp.OP_SYSCALL, new OpExec(ScriptOp.OP_SYSCALL, "SYSCALL", null, null));

        OpExecList.put(ScriptOp.OP_DUPFROMALTSTACK, new OpExec(ScriptOp.OP_DUPFROMALTSTACK, "DUPFROMALTSTACK", opToDupFromAltStack, null));
        OpExecList.put(ScriptOp.OP_TOALTSTACK, new OpExec(ScriptOp.OP_TOALTSTACK, "TOALTSTACK", opToAltStack, null));
        OpExecList.put(ScriptOp.OP_FROMALTSTACK, new OpExec(ScriptOp.OP_FROMALTSTACK, "FROMALTSTACK", opFromAltStack, null));
        OpExecList.put(ScriptOp.OP_XDROP, new OpExec(ScriptOp.OP_XDROP, "XDROP", opXDrop, null));
        OpExecList.put(ScriptOp.OP_XSWAP, new OpExec(ScriptOp.OP_XSWAP, "XSWAP", opXSwap, null));
        OpExecList.put(ScriptOp.OP_XTUCK, new OpExec(ScriptOp.OP_XTUCK, "XTUCK", opXTuck, null));
        OpExecList.put(ScriptOp.OP_DEPTH, new OpExec(ScriptOp.OP_DEPTH, "DEPTH", opDepth, null));
        OpExecList.put(ScriptOp.OP_DROP, new OpExec(ScriptOp.OP_DROP, "DROP", opDrop, null));
        OpExecList.put(ScriptOp.OP_DUP, new OpExec(ScriptOp.OP_DUP, "DUP", opDup, null));
        OpExecList.put(ScriptOp.OP_NIP, new OpExec(ScriptOp.OP_NIP, "NIP", opNip, null));
        OpExecList.put(ScriptOp.OP_OVER, new OpExec(ScriptOp.OP_OVER, "OVER", opOver, null));
        OpExecList.put(ScriptOp.OP_PICK, new OpExec(ScriptOp.OP_PICK, "PICK", opPick, null));
        OpExecList.put(ScriptOp.OP_ROLL, new OpExec(ScriptOp.OP_ROLL, "ROLL", opRoll, null));
        OpExecList.put(ScriptOp.OP_ROT, new OpExec(ScriptOp.OP_ROT, "ROT", opRot, null));
        OpExecList.put(ScriptOp.OP_SWAP, new OpExec(ScriptOp.OP_SWAP, "SWAP", opSwap, null));
        OpExecList.put(ScriptOp.OP_TUCK, new OpExec(ScriptOp.OP_TUCK, "TUCK", opTuck, null));

        OpExecList.put(ScriptOp.OP_CAT, new OpExec(ScriptOp.OP_CAT, "CAT", opCat, null));
        OpExecList.put(ScriptOp.OP_SUBSTR, new OpExec(ScriptOp.OP_SUBSTR, "SUBSTR", opSubStr, null));
        OpExecList.put(ScriptOp.OP_LEFT, new OpExec(ScriptOp.OP_LEFT, "LEFT", opLeft, null));
        OpExecList.put(ScriptOp.OP_RIGHT, new OpExec(ScriptOp.OP_RIGHT, "RIGHT", opRight, null));
        OpExecList.put(ScriptOp.OP_SIZE, new OpExec(ScriptOp.OP_SIZE, "SIZE", opSize, null));

        OpExecList.put(ScriptOp.OP_INVERT, new OpExec(ScriptOp.OP_INVERT, "INVERT", opInvert, null));
        OpExecList.put(ScriptOp.OP_AND, new OpExec(ScriptOp.OP_AND, "AND", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_OR, new OpExec(ScriptOp.OP_OR, "OR", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_XOR, new OpExec(ScriptOp.OP_XOR, "XOR", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_EQUAL, new OpExec(ScriptOp.OP_EQUAL, "EQUAL", opEqual, null));


        OpExecList.put(ScriptOp.OP_INC, new OpExec(ScriptOp.OP_OR, "OR", opBigInt, null));
        OpExecList.put(ScriptOp.OP_DEC, new OpExec(ScriptOp.OP_DEC, "DEC", opBigInt, null));
        OpExecList.put(ScriptOp.OP_SIGN, new OpExec(ScriptOp.OP_SIGN, "SIGN", opSign, null));
        OpExecList.put(ScriptOp.OP_NEGATE, new OpExec(ScriptOp.OP_NEGATE, "NEGATE", opBigInt, null));
        OpExecList.put(ScriptOp.OP_ABS, new OpExec(ScriptOp.OP_ABS, "ABS", opBigInt, null));
        OpExecList.put(ScriptOp.OP_NOT, new OpExec(ScriptOp.OP_NOT, "NOT", opNot, null));
        OpExecList.put(ScriptOp.OP_NZ, new OpExec(ScriptOp.OP_NZ, "NZ", opNz, null));
        OpExecList.put(ScriptOp.OP_ADD, new OpExec(ScriptOp.OP_ADD, "ADD", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_SUB, new OpExec(ScriptOp.OP_SUB, "SUB", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_MUL, new OpExec(ScriptOp.OP_MUL, "MUL", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_DIV, new OpExec(ScriptOp.OP_DIV, "DIV", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_MOD, new OpExec(ScriptOp.OP_MOD, "MOD", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_SHL, new OpExec(ScriptOp.OP_SHL, "SHL", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_SHR, new OpExec(ScriptOp.OP_SHR, "SHR", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_BOOLAND, new OpExec(ScriptOp.OP_BOOLAND, "BOOLAND", opBoolZip, null));
        OpExecList.put(ScriptOp.OP_BOOLOR, new OpExec(ScriptOp.OP_BOOLOR, "BOOLOR", opBoolZip, null));
        OpExecList.put(ScriptOp.OP_NUMEQUAL, new OpExec(ScriptOp.OP_NUMEQUAL, "NUMEQUAL", opBigIntComp, null));
        OpExecList.put(ScriptOp.OP_LT, new OpExec(ScriptOp.OP_LT, "LT", opBigIntComp, null));
        OpExecList.put(ScriptOp.OP_GT, new OpExec(ScriptOp.OP_GT, "GT", opBigIntComp, null));
        OpExecList.put(ScriptOp.OP_LTE, new OpExec(ScriptOp.OP_LTE, "LTE", opBigIntComp, null));
        OpExecList.put(ScriptOp.OP_GTE, new OpExec(ScriptOp.OP_GTE, "GTE", opBigIntComp, null));
        OpExecList.put(ScriptOp.OP_MIN, new OpExec(ScriptOp.OP_MIN, "MIN", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_MAX, new OpExec(ScriptOp.OP_MAX, "MAX", opBigIntZip, null));
        OpExecList.put(ScriptOp.OP_WITHIN, new OpExec(ScriptOp.OP_WITHIN, "WITHIN", opWithIn, null));

        OpExecList.put(ScriptOp.OP_SHA1, new OpExec(ScriptOp.OP_SHA1, "SHA1", opHash, null));
        OpExecList.put(ScriptOp.OP_SHA256, new OpExec(ScriptOp.OP_SHA256, "SHA256", opHash, null));
        OpExecList.put(ScriptOp.OP_HASH160, new OpExec(ScriptOp.OP_HASH160, "HASH160", opHash, null));
        OpExecList.put(ScriptOp.OP_HASH256, new OpExec(ScriptOp.OP_HASH256, "HASH256", opHash, null));
        OpExecList.put(ScriptOp.OP_VERIFY, new OpExec(ScriptOp.OP_VERIFY, "VERIFY", null, null));

        OpExecList.put(ScriptOp.OP_ARRAYSIZE, new OpExec(ScriptOp.OP_ARRAYSIZE, "ARRAYSIZE", opArraySize, null));
        OpExecList.put(ScriptOp.OP_PACK, new OpExec(ScriptOp.OP_PACK, "PACK", opPack, null));
        OpExecList.put(ScriptOp.OP_UNPACK, new OpExec(ScriptOp.OP_UNPACK, "UNPACK", opUnpack, null));
        OpExecList.put(ScriptOp.OP_PICKITEM, new OpExec(ScriptOp.OP_PICKITEM, "PICKITEM", opPickItem, null));
        OpExecList.put(ScriptOp.OP_SETITEM, new OpExec(ScriptOp.OP_SETITEM, "SETITEM", opSetItem, null));
        OpExecList.put(ScriptOp.OP_NEWARRAY, new OpExec(ScriptOp.OP_NEWARRAY, "NEWARRAY", opNewArray, null));
        OpExecList.put(ScriptOp.OP_NEWMAP, new OpExec(ScriptOp.OP_NEWMAP, "NEWMAP", opNewMap, null));
        OpExecList.put(ScriptOp.OP_NEWSTRUCT, new OpExec(ScriptOp.OP_NEWSTRUCT, "NEWSTRUCT", opNewStruct, null));
        OpExecList.put(ScriptOp.OP_APPEND, new OpExec(ScriptOp.OP_APPEND, "APPEND", opAppend, null));
        OpExecList.put(ScriptOp.OP_REVERSE, new OpExec(ScriptOp.OP_REVERSE, "REVERSE", opReverse, null));

        OpExecList.put(ScriptOp.OP_THROW, new OpExec(ScriptOp.OP_THROW, "THROW", opThrow, null));
        OpExecList.put(ScriptOp.OP_THROWIFNOT, new OpExec(ScriptOp.OP_THROWIFNOT, "THROWIFNOT", opThrowIfNot, null));


    }

    public boolean validatePickItem(ExecutionEngine engine) {

        StackItems item = PushData.PeekNStackItem(1, engine);
        if (item == null) {
            return false;
        }
        if (item instanceof MapItem) {
        } else if (item instanceof ArrayItem) {
            BigInteger index = PushData.PeekBigInteger(engine);
            if (index.signum() < 0) {
                System.out.println("ERR_BAD_VALUE");
                return false;
            }
            StackItems[] arr = item.GetArray();
            if (index.compareTo(BigInteger.valueOf(arr.length)) >= 0) {
                System.out.println("ERR_OVER_MAX_ARRAY_SIZE");
                return false;
            }
        } else if (item instanceof StructItem) {
        }
        return false;
    }
}
