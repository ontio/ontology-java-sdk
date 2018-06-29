package demo.vmtest.vm;

import com.github.ontio.core.scripts.ScriptOp;
import demo.vmtest.utils.PushData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OpExec {
    public ScriptOp Opcode;
    public String Name;
    public Method ExecFunc;
    public Method ValidatorFunc;

    public OpExec(ScriptOp opcode, String name, Method execMethod, Method validatorMethod) throws Exception{
        Opcode = opcode;
        Name = name;
        ExecFunc = execMethod;
        ValidatorFunc = validatorMethod;
    }

    public VMState Exec(ExecutionEngine engine) {
        try {
           ExecFunc.invoke(PushData.class.newInstance(),engine);
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
        return VMState.NONE ;
    }

    boolean Validator(ExecutionEngine engine) {
        return true;
    }

    public void init() {
       // OpExecList
    }
}