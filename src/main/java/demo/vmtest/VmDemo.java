package demo.vmtest;

import com.github.ontio.common.Helper;
import demo.vmtest.vm.ExecutionContext;
import demo.vmtest.vm.ExecutionEngine;

public class VmDemo {
    public static void main(String[] args) {

        try {
            String str = "52c56b61536c766b00527ac46c766b00c36c766b51527ac46203006c766b51c3616c7566";
            byte[] code = Helper.hexToBytes(str);
            ExecutionEngine engine = new ExecutionEngine();
            engine.PushContext(new ExecutionContext(engine, code));
            while (true) {
                if (engine.Contexts.size() == 0 || engine.Context == null) {
                    break;
                }
                engine.ExecuteCode();
                if (!engine.ValidateOp()) {
                    break;
                }
                System.out.println(engine.EvaluationStack.Count() + "  " + Helper.toHexString(new byte[]{engine.OpCode.getByte()}) + " " + engine.OpExec.Name + "     " + engine.EvaluationStack.info());
                engine.StepInto();
            }
            System.out.println("Stack Count:" + engine.EvaluationStack.Count());
            System.out.println("Result:" + engine.EvaluationStack.Peek(0).GetBigInteger().longValue());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*

using Neo.SmartContract.Framework.Services.Neo;
using Neo.SmartContract.Framework;
using System;
using System.ComponentModel;

namespace Neo.SmartContract
{
    public class HelloWorld : Framework.SmartContract
    {
        public static object Main()
        {
            int i = 1 +2;
            return i;
        }

    }
}


 */