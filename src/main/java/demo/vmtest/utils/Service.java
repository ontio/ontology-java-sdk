package demo.vmtest.utils;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import demo.vmtest.types.*;
import demo.vmtest.vm.ExecutionEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service {
    public Method ExecFunc;
    public Method ValidatorFunc;

    public Service() {

    }

    public Service(Method execMethod, Method validatorMethod) {
        ExecFunc = execMethod;
        ValidatorFunc = validatorMethod;
    }

    public void Exec(Config config, ExecutionEngine engine) {
        try {
            ExecFunc.invoke(Service.class.newInstance(), config, engine);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void storagePut(Config config, ExecutionEngine engine) {
        InteropItem item = PushData.PopInteropInterface(engine);//ContractAddress
        byte[] key = PushData.PopByteArray(engine);
        byte[] value = PushData.PopByteArray(engine);

        config.getStorageMap().put(new String(item.value) + new String(key), value);
    }

    public void storageGetContext(Config config, ExecutionEngine engine) {
        PushData.PushData(engine, new InteropItem(config.ContractAddress.getBytes()));
    }

    public void storageGet(Config config, ExecutionEngine engine) {
        InteropItem item = PushData.PopInteropInterface(engine);//ContractAddress
        byte[] key = PushData.PopByteArray(engine);
        byte[] value = config.getStorageMap().get(new String(item.value) + new String(key));
        if (value == null) {
            value = new byte[]{};
        }
        PushData.PushData(engine, value);
    }

    public void runtimeLog(Config config, ExecutionEngine engine) {
        byte[] item = PushData.PopByteArray(engine);
        System.out.println("RuntimeLog:  " + new String(item));
    }

    public void runtimeNotify(Config config, ExecutionEngine engine) {
        StackItems item = PushData.PopStackItem(engine);
        System.out.println("RuntimeNotify:  " + ConvertNeoVmTypeHexString(item));
    }

    public void runtimeCheckWitness(Config config, ExecutionEngine engine) {
        byte[] data = PushData.PopByteArray(engine);
        if (data.length == 20) {
            Address address = Address.parse(Helper.toHexString(data));
            List list = config.GetSignatureAddresses();
            if (list.contains(address)) {
                PushData.PushData(engine, new BoolItem(true));
            } else {
                PushData.PushData(engine, new BoolItem(false));
            }
        }
    }

    public void runtimeDeserialize(Config config, ExecutionEngine engine) {
        byte[] bys = PushData.PopByteArray(engine);
        VmReader reader = new VmReader(bys);
        StackItems items = DeserializeStackItem(reader);
        PushData.PushData(engine, items);
    }

    public void runtimeSerialize(Config config, ExecutionEngine engine) {
        StackItems t = PushData.PopStackItem(engine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        serializeStackItem(t, bw);
        PushData.PushData(engine, baos.toByteArray());
    }

    private void serializeStackItem(StackItems item, BinaryWriter writer) {
        try {

            if (item instanceof ByteArrayItem) {
                writer.writeByte(BuildParams.Type.ByteArrayType.getValue());
                byte[] bys = item.GetByteArray();
                writer.writeVarBytes(bys);
            } else if (item instanceof IntegerItem) {
                writer.writeByte(BuildParams.Type.IntegerType.getValue());
                byte[] bys = item.GetByteArray();
                writer.writeVarBytes(bys);
            } else if (item instanceof BoolItem) {
                writer.writeByte(BuildParams.Type.BooleanType.getValue());
                byte[] bys = item.GetByteArray();
                writer.writeVarBytes(bys);
            } else if (item instanceof ArrayItem) {
                writer.writeByte(BuildParams.Type.ArrayType.getValue());
                StackItems[] arr = item.GetArray();
                writer.writeVarInt(arr.length);
                for (int i = 0; i < arr.length; i++) {
                    serializeStackItem(item, writer);
                }
            } else if (item instanceof StructItem) {

            } else if (item instanceof MapItem) {
                writer.writeByte(BuildParams.Type.MapType.getValue());
                Map<StackItems, StackItems> map = item.GetMap();
                writer.writeVarInt(map.size());
                for (Map.Entry<StackItems, StackItems> e : map.entrySet()) {
                    serializeStackItem(e.getKey(), writer);
                    serializeStackItem(e.getValue(), writer);
                }
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StackItems DeserializeStackItem(VmReader reader) {
        byte bt = reader.ReadByte();
        if (bt == BuildParams.Type.ByteArrayType.getValue()) {
            byte[] val = reader.readVarBytes();
            return new ByteArrayItem(val);
        } else if (bt == BuildParams.Type.BooleanType.getValue()) {
            return new BoolItem(reader.ReadBool());
        } else if (bt == BuildParams.Type.IntegerType.getValue()) {
            BigInteger b = new BigInteger(reader.readVarBytes());
            return new IntegerItem(b);
        } else if (bt == BuildParams.Type.ArrayType.getValue()) {
            int count = reader.readVarInt();
            StackItems[] arr = new StackItems[count];
            for (int i = 0; i < count; i++) {
                arr[i] = DeserializeStackItem(reader);
            }
            return new ArrayItem(arr);
        } else if (bt == BuildParams.Type.StructType.getValue()) {
            int count = reader.readVarInt();
            List<StackItems> arr = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                arr.add(DeserializeStackItem(reader));
            }
            return new StructItem(arr);
        } else if (bt == BuildParams.Type.MapType.getValue()) {

            int count = reader.readVarInt();
            MapItem map = new MapItem();
            for (int i = 0; i < count; i++) {
                StackItems key = DeserializeStackItem(reader);
                StackItems value = DeserializeStackItem(reader);
                map.map.put(key, value);
            }
            return map;
        }
        return null;
    }

    private Object ConvertNeoVmTypeHexString(StackItems item) {
        if (item == null) {
            return null;
        }
        if (item instanceof ByteArrayItem) {
            byte[] bys = item.GetByteArray();
            return Helper.toHexString(bys);
        } else if (item instanceof IntegerItem) {
            return item.GetBigInteger().longValue();
        } else if (item instanceof BoolItem) {
            return item.GetBoolean();
        } else if (item instanceof ArrayItem) {
            List list = new ArrayList();
            for (int i = 0; i < item.GetArray().length; i++) {
                Object obj = ConvertNeoVmTypeHexString(item.GetArray()[i]);
                list.add(obj);
            }
            list.set(0, list.get(0) + "(" + new String(Helper.hexToBytes((String) list.get(0))) + ")");
            return list;
        } else if (item instanceof StructItem) {

        } else if (item instanceof InteropItem) {
            return Helper.toHexString(item.GetByteArray());
        } else {
            return null;
        }
        return null;
    }

}
