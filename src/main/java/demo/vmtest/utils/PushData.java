package demo.vmtest.utils;

import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.crypto.Digest;
import demo.vmtest.vm.ExecutionContext;
import demo.vmtest.vm.ExecutionEngine;
import demo.vmtest.types.InteropItem;
import demo.vmtest.vm.VMState;
import demo.vmtest.types.*;

import java.math.BigInteger;

/**
 * @Description:
 * @date 2018/6/27
 */
public class PushData {
    public static VMState opPushData(ExecutionEngine engine) {
        Object data = getPushData(engine);
        PushData(engine, data);
        return VMState.NONE;
    }

    public static Object getPushData(ExecutionEngine e) {
        Object data = null;
        if (e.OpCode.getByte() >= ScriptOp.OP_PUSHBYTES1.getByte() && e.OpCode.getByte() <= ScriptOp.OP_PUSHBYTES75.getByte()) {
            data = e.Context.OpReader.ReadBytes((int) e.OpCode.getByte());
        }
        if (e.OpCode == ScriptOp.OP_PUSH0) {
            data = 0;
        } else if (e.OpCode == ScriptOp.OP_PUSHDATA1) {
            byte b = e.Context.OpReader.ReadByte();
            data = e.Context.OpReader.ReadBytes(b);
        } else if (e.OpCode == ScriptOp.OP_PUSHDATA2) {
            data = e.Context.OpReader.ReadBytes(2);
        } else if (e.OpCode == ScriptOp.OP_PUSHDATA4) {
            data = e.Context.OpReader.ReadBytes(4);
        } else if (e.OpCode == ScriptOp.OP_PUSHM1 || (e.OpCode.getByte() >= ScriptOp.OP_PUSH1.getByte() && e.OpCode.getByte() <= ScriptOp.OP_PUSH16.getByte())) {
            data = e.OpCode.getByte() - ScriptOp.OP_PUSH1.getByte() + 1;
        }
        return data;
    }

    public static void PushData(ExecutionEngine engine, Object data) {
        if (data instanceof Integer) {
            engine.EvaluationStack.Push(new IntegerItem(BigInteger.valueOf((int) data)));
        } else if (data instanceof Boolean) {
            engine.EvaluationStack.Push(new BoolItem((boolean) data));
        } else if (data instanceof Long) {
            engine.EvaluationStack.Push(new IntegerItem(BigInteger.valueOf((long) data)));
        } else if (data instanceof byte[]) {
            engine.EvaluationStack.Push(new ByteArrayItem((byte[]) (data)));
        } else if (data instanceof ArrayItem) {
            engine.EvaluationStack.Push(new ArrayItem(((ArrayItem) data).stackItems));
        } else if (data instanceof IntegerItem) {
            engine.EvaluationStack.Push((IntegerItem) data);
        } else if (data instanceof BoolItem) {
            engine.EvaluationStack.Push((BoolItem) data);
        } else if (data instanceof ByteArrayItem) {
            engine.EvaluationStack.Push((ByteArrayItem) data);
        } else if (data instanceof MapItem) {
            engine.EvaluationStack.Push((MapItem) data);
        } else if (data instanceof StructItem) {
            engine.EvaluationStack.Push((StructItem) data);
        } else if (data instanceof StackItems) {
            engine.EvaluationStack.Push((StackItems) data);
        } else if (data instanceof StackItems[]) {
            engine.EvaluationStack.Push(new ArrayItem((StackItems[]) data));
        }


    }

    public static VMState opNop(ExecutionEngine engine) {
        return VMState.NONE;
    }

    public static VMState opJmp(ExecutionEngine engine) {
        int offset = engine.Context.OpReader.readInt16();
        offset = engine.Context.GetInstructionPointer() + offset - 3;
        if (offset < 0 || offset > engine.Context.Code.length) {
            return VMState.FAULT;
        }
        boolean fValue = true;
        if (engine.OpCode.getByte() > ScriptOp.OP_JMP.getByte()) {
            if (EvaluationStackCount(engine) < 1) {
                return VMState.FAULT;
            }
            fValue = PopBoolean(engine);
            if (engine.OpCode == ScriptOp.OP_JMPIFNOT) {
                fValue = !fValue;
            }
        }
        if (fValue) {
            engine.Context.SetInstructionPointer(offset);
        }
        return VMState.NONE;
    }

    public static VMState opCall(ExecutionEngine engine) {
        ExecutionContext executionContext = engine.Context.Clone();
        engine.Context.SetInstructionPointer(engine.Context.GetInstructionPointer() + 2);
        engine.OpCode = ScriptOp.OP_JMP;
        engine.PushContext(executionContext);
        return opJmp(engine);
    }

    public static VMState opRet(ExecutionEngine engine) {
        engine.PopContext();
        return VMState.NONE;
    }

    public static VMState opToDupFromAltStack(ExecutionEngine engine) {
        Push(engine, engine.AltStack.Peek(0));
        return VMState.NONE;
    }

    public static VMState opToAltStack(ExecutionEngine engine) {
        engine.AltStack.Push(PopStackItem(engine));
        return VMState.NONE;
    }

    public static VMState opFromAltStack(ExecutionEngine engine) {
        StackItems items = engine.AltStack.Pop();
        Push(engine, items);
        return VMState.NONE;
    }

    public static VMState opXDrop(ExecutionEngine engine) {
        int n = PopInt(engine);
        engine.AltStack.Remove(n);
        return VMState.NONE;
    }

    public static VMState opXSwap(ExecutionEngine engine) {
        int n = PopInt(engine);
        if (n == 0) {
            return VMState.NONE;
        }
        engine.EvaluationStack.Swap(0, n);
        return VMState.NONE;
    }

    public static VMState opXTuck(ExecutionEngine engine) {
        int n = PopInt(engine);
        engine.EvaluationStack.Insert(n, PeekStackItem(engine));
        return VMState.NONE;
    }

    public static VMState opDepth(ExecutionEngine engine) {
        PushData(engine, Count(engine));
        return VMState.NONE;
    }

    public static VMState opDrop(ExecutionEngine engine) {
        PopStackItem(engine);
        return VMState.NONE;
    }

    public static VMState opDup(ExecutionEngine engine) {
        StackItems items = PeekStackItem(engine);
        Push(engine, items);
        return VMState.NONE;
    }

    public static VMState opNip(ExecutionEngine engine) {
        StackItems x2 = PopStackItem(engine);
        PeekStackItem(engine);
        Push(engine, x2);
        return VMState.NONE;
    }

    public static VMState opOver(ExecutionEngine engine) {
        StackItems x2 = PopStackItem(engine);
        StackItems x1 = PeekStackItem(engine);
        PeekStackItem(engine);
        Push(engine, x2);
        Push(engine, x1);
        return VMState.NONE;
    }

    public static VMState opPick(ExecutionEngine engine) {
        int n = PopInt(engine);
        if (n == 0) {
            return VMState.NONE;
        }
        Push(engine, engine.EvaluationStack.Peek(n));
        return VMState.NONE;
    }

    public static VMState opRoll(ExecutionEngine engine) {
        int n = PopInt(engine);
        if (n == 0) {
            return VMState.NONE;
        }
        Push(engine, engine.EvaluationStack.Remove(n));
        return VMState.NONE;
    }

    public static VMState opRot(ExecutionEngine engine) {
        StackItems x3 = PopStackItem(engine);
        StackItems x2 = PopStackItem(engine);
        StackItems x1 = PopStackItem(engine);
        Push(engine, x2);
        Push(engine, x3);
        Push(engine, x1);
        return VMState.NONE;
    }

    public static VMState opSwap(ExecutionEngine engine) {
        StackItems x2 = PopStackItem(engine);
        StackItems x1 = PopStackItem(engine);
        Push(engine, x2);
        Push(engine, x1);
        return VMState.NONE;
    }

    public static VMState opTuck(ExecutionEngine engine) {
        StackItems x2 = PopStackItem(engine);
        StackItems x1 = PopStackItem(engine);
        Push(engine, x2);
        Push(engine, x1);
        Push(engine, x2);
        return VMState.NONE;
    }

    private static byte[] Concat(byte[] array1, byte[] array2) {
        return Helper.addBytes(array1, array2);
    }

    public static VMState opCat(ExecutionEngine engine) {
        byte[] bs2 = PopByteArray(engine);
        byte[] bs1 = PopByteArray(engine);
        byte[] r = Concat(bs1, bs2);
        PushData(engine, r);
        return VMState.NONE;
    }

    public static VMState opSubStr(ExecutionEngine engine) {
        int count = PopInt(engine);
        int index = PopInt(engine);
        byte[] arr = PopByteArray(engine);
        byte[] bs = new byte[count];
        System.arraycopy(arr, index, bs, 0, count);
        PushData(engine, bs);
        return VMState.NONE;
    }

    public static VMState opLeft(ExecutionEngine engine) {
        int count = PopInt(engine);
        byte[] arr = PopByteArray(engine);
        byte[] bs = new byte[count];
        System.arraycopy(arr, 0, bs, 0, count);
        PushData(engine, bs);
        return VMState.NONE;
    }

    public static VMState opRight(ExecutionEngine engine) {
        int count = PopInt(engine);
        byte[] arr = PopByteArray(engine);
        byte[] bs = new byte[count];
        System.arraycopy(arr, arr.length - count, bs, 0, count);
        PushData(engine, bs);
        return VMState.NONE;
    }

    public static VMState opSize(ExecutionEngine engine) {
        byte[] arr = PopByteArray(engine);
        PushData(engine, arr.length);
        return VMState.NONE;
    }

    public static VMState opInvert(ExecutionEngine engine) {
        BigInteger i = PopBigInt(engine);
        PushData(engine, i.not());
        return VMState.NONE;
    }

    public static VMState opEqual(ExecutionEngine engine) {
        StackItems b1 = PopStackItem(engine);
        StackItems b2 = PopStackItem(engine);
        PushData(engine, b1.equals(b2));
        return VMState.NONE;
    }

    public static VMState opArraySize(ExecutionEngine engine) {
        StackItems item = PopStackItem(engine);
        if (item instanceof ArrayItem) {
            StackItems[] bys = item.GetArray();
            PushData(engine, bys.length);
        } else {
            byte[] bys = item.GetByteArray();
            PushData(engine, bys.length);
        }
        return VMState.NONE;
    }

    public static VMState opPack(ExecutionEngine engine) {
        int size = PopInt(engine);
        StackItems[] items = new StackItems[size];
        for (int i = 0; i < size; i++) {
            items[i] = PopStackItem(engine);
        }
        PushData(engine, items);
        return VMState.NONE;
    }

    public static VMState opUnpack(ExecutionEngine engine) {
        StackItems[] items = PopArray(engine);
        int l = items.length;
        for (int i = l - 1; i > 0; i--) {
            Push(engine, items[i]);
        }
        PushData(engine, l);
        return VMState.NONE;
    }

    public static VMState opPickItem(ExecutionEngine engine) {
        StackItems index = PopStackItem(engine);
        StackItems items = PopStackItem(engine);
        if (items instanceof ArrayItem) {
            int i = index.GetBigInteger().intValue();
            StackItems[] arr = items.GetArray();
            PushData(engine, arr[i]);
        } else if (items instanceof MapItem) {
            StackItems t = ((MapItem) items).TryGetValue(index);
            PushData(engine, t);
        }
        return VMState.NONE;
    }

    public static VMState opSetItem(ExecutionEngine engine) {
        StackItems newItem = PopStackItem(engine);
        StackItems index = PopStackItem(engine);
        StackItems item = PopStackItem(engine);
        if (item instanceof ArrayItem) {
            int i = index.GetBigInteger().intValue();
            StackItems[] items = item.GetArray();
            items[i] = newItem;
        } else if (item instanceof MapItem) {
            ((MapItem) item).Add(index, newItem);
        } else if (item instanceof StructItem) {
            int i = index.GetBigInteger().intValue();
            ((StructItem) item).stackItems.set(i, newItem);
        }
        return VMState.NONE;
    }

    public static VMState opNewArray(ExecutionEngine engine) {
        int count = PopInt(engine);
        StackItems[] items = new StackItems[count];
        for (int i = 0; i < count; i++) {
            items[i] = new BoolItem(false);
        }
        PushData(engine, new ArrayItem(items));
        return VMState.NONE;
    }

    public static VMState opNewStruct(ExecutionEngine engine) {
        return VMState.NONE;
    }

    public static VMState opNewMap(ExecutionEngine engine) {
        PushData(engine, new MapItem());
        return VMState.NONE;
    }

    public static VMState opAppend(ExecutionEngine engine) {
        StackItems newItem = PopStackItem(engine);
        StackItems items = PopStackItem(engine);
        //TODO
        return VMState.NONE;
    }

    public static VMState opReverse(ExecutionEngine engine) {
        StackItems[] items = PopArray(engine);
        //TODO
        return VMState.NONE;
    }

    public static VMState opThrow(ExecutionEngine engine) {
        return VMState.FAULT;
    }

    public static VMState opThrowIfNot(ExecutionEngine engine) {
        boolean b = PopBoolean(engine);
        if (!b) {
            return VMState.FAULT;
        }
        return VMState.NONE;
    }

    public static VMState opHash(ExecutionEngine engine) {
        byte[] x = PopByteArray(engine);
        PushData(engine, Hash(x, engine));
        return VMState.NONE;
    }

    //    public static VMState opCheckSig(ExecutionEngine engine){
//        byte[] pubkey = PopByteArray(engine);
//        byte[] signature = PopByteArray(engine);
//        try {
//            boolean b = new Account(false,pubkey).verifySignature(engine.,signature);
//            PushData(engine,b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return VMState.NONE;
//    }
    public static byte[] Hash(byte[] bs, ExecutionEngine engine) {
        if (engine.OpCode == ScriptOp.OP_SHA1) {
            return null;
        } else if (engine.OpCode == ScriptOp.OP_SHA256) {
            return Digest.sha256(bs);
        } else if (engine.OpCode == ScriptOp.OP_HASH160) {
            return Digest.hash160(bs);
        } else if (engine.OpCode == ScriptOp.OP_HASH256) {
            return Digest.hash256(bs);
        }
        return null;
    }

    public static VMState opBigInt(ExecutionEngine engine) {
        BigInteger x = PopBigInt(engine);
        PushData(engine, BigIntOp(x, engine.OpCode));
        return VMState.NONE;
    }

    public static VMState opSign(ExecutionEngine engine) {
        BigInteger x = PopBigInt(engine);
        PushData(engine, x.signum());
        return VMState.NONE;
    }

    public static VMState opNot(ExecutionEngine engine) {
        boolean x = PopBoolean(engine);
        PushData(engine, !x);
        return VMState.NONE;
    }

    public static VMState opNz(ExecutionEngine engine) {
        BigInteger x = PopBigInt(engine);
        PushData(engine, BigIntComp(x, engine.OpCode));
        return VMState.NONE;
    }

    public static VMState opBigIntZip(ExecutionEngine engine) {
        BigInteger x2 = PopBigInt(engine);
        BigInteger x1 = PopBigInt(engine);
        BigInteger b = BigIntZip(x2, x1, engine.OpCode);
        PushData(engine, b);
        return VMState.NONE;
    }

    public static VMState opBoolZip(ExecutionEngine engine) {
        boolean x2 = PopBoolean(engine);
        boolean x1 = PopBoolean(engine);
        boolean b = BoolZip(x2, x1, engine.OpCode);
        PushData(engine, b);
        return VMState.NONE;
    }

    public static VMState opBigIntComp(ExecutionEngine engine) {
        BigInteger x2 = PopBigInt(engine);
        BigInteger x1 = PopBigInt(engine);
        boolean b = BigIntMultiComp(x1, x2, engine.OpCode);
        PushData(engine, b);
        return VMState.NONE;
    }

    public static VMState opWithIn(ExecutionEngine engine) {
        BigInteger b = PopBigInt(engine);
        BigInteger a = PopBigInt(engine);
        BigInteger c = PopBigInt(engine);
        PushData(engine, WithInOp(c, a, b));
        return VMState.NONE;
    }

    public static BigInteger BigIntOp(BigInteger a, ScriptOp op) {
        if (op == ScriptOp.OP_INC) {
            return a.add(BigInteger.valueOf(1));
        } else if (op == ScriptOp.OP_DEC) {
            return a.subtract(BigInteger.valueOf(1));
        } else if (op == ScriptOp.OP_NEGATE) {
            return a.negate();
        } else if (op == ScriptOp.OP_ABS) {
            return a.abs();
        } else {
            return a;
        }
    }

    public static boolean BigIntComp(BigInteger a, ScriptOp op) {
        if (op == ScriptOp.OP_NZ) {
            return a.compareTo(BigInteger.valueOf(0)) != 0;
        }
        return false;
    }

    public static boolean WithInOp(BigInteger a, BigInteger b, BigInteger c) {
        boolean b1 = BigIntMultiComp(a, b, ScriptOp.OP_GTE);
        boolean b2 = BigIntMultiComp(a, c, ScriptOp.OP_GTE);
        return BoolZip(b1, b2, ScriptOp.OP_BOOLAND);
    }

    public static boolean BoolZip(boolean a, boolean b, ScriptOp op) {
        if (op == ScriptOp.OP_BOOLAND) {
            return a && b;
        } else if (op == ScriptOp.OP_BOOLOR) {
            return a || b;
        }
        return false;
    }

    public static boolean BigIntMultiComp(BigInteger a, BigInteger b, ScriptOp op) {
        if (op == ScriptOp.OP_NUMEQUAL) {
            return a.compareTo(b) == 0;
        } else if (op == ScriptOp.OP_NUMNOTEQUAL) {
            return a.compareTo(b) != 0;
        } else if (op == ScriptOp.OP_LT) {
            return a.compareTo(b) < 0;
        } else if (op == ScriptOp.OP_GT) {
            return a.compareTo(b) > 0;
        } else if (op == ScriptOp.OP_LTE) {
            return a.compareTo(b) <= 0;
        } else if (op == ScriptOp.OP_GTE) {
            return a.compareTo(b) >= 0;
        }
        return false;
    }

    public static BigInteger BigIntZip(BigInteger a, BigInteger b, ScriptOp op) {
        if (op == ScriptOp.OP_AND) {
            return a.and(b);
        } else if (op == ScriptOp.OP_OR) {
            return a.or(b);
        } else if (op == ScriptOp.OP_XOR) {
            return a.xor(b);
        } else if (op == ScriptOp.OP_ADD) {
            return a.add(b);
        } else if (op == ScriptOp.OP_SUB) {
            return a.subtract(b);
        } else if (op == ScriptOp.OP_MUL) {
            return a.multiply(b);
        } else if (op == ScriptOp.OP_DIV) {
            return a.divide(b);
        } else if (op == ScriptOp.OP_MOD) {
            return a.mod(b);
        } else if (op == ScriptOp.OP_SHL) {
            return a.shiftLeft(b.intValue());
        } else if (op == ScriptOp.OP_SHR) {
            return a.shiftRight(b.intValue());
        } else if (op == ScriptOp.OP_MIN) {
            if (a.compareTo(b) < 0) {
                return a;
            } else {
                return b;
            }
        } else if (op == ScriptOp.OP_MAX) {
            if (a.compareTo(b) > 0) {
                return a;
            } else {
                return b;
            }
        }
        return null;
    }

    public static int EvaluationStackCount(ExecutionEngine engine) {
        return engine.EvaluationStack.Count();
    }

    public static BigInteger PopBigInt(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetBigInteger();
    }

    public static int PopInt(ExecutionEngine engine) {
        StackItems item = engine.EvaluationStack.Pop();
        return item.GetBigInteger().intValue();
    }

    public static boolean PopBoolean(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetBoolean();
    }

    public static StackItems[] PopArray(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetArray();
    }

    public static InteropItem PopInteropInterface(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetInterface();
    }

    public static byte[] PopByteArray(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetByteArray();
    }

    public static StackItems PopStackItem(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop();
    }

    public static StackItems[] PeekArray(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetArray();
    }

    public static BigInteger PeekBigInteger(ExecutionEngine engine) {
        return PeekStackItem(engine).GetBigInteger();
    }

    public static InteropItem PeekInteropInterface(ExecutionEngine engine) {
        return engine.EvaluationStack.Pop().GetInterface();
    }

    public static void Push(ExecutionEngine engine, StackItems ele) {
        engine.EvaluationStack.Push(ele);
    }

    public static StackItems PeekStackItem(ExecutionEngine engine) {
        return engine.EvaluationStack.Peek(0);
    }

    public static StackItems PeekNStackItem(int i, ExecutionEngine engine) {
        return engine.EvaluationStack.Peek(i);
    }

    public static int Count(ExecutionEngine engine) {
        return engine.EvaluationStack.Count();
    }

}
