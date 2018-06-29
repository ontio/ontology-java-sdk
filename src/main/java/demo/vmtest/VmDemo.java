package demo.vmtest;

import com.github.ontio.common.Helper;
import demo.vmtest.vm.ExecutionContext;
import demo.vmtest.vm.ExecutionEngine;

public class VmDemo {
    public static void main(String[] args) {

        try {
            String str = "51c56b61536c766b00527ac46203006c766b00c3616c7566";
            byte[] code = Helper.hexToBytes(str);
            ExecutionEngine engine = new ExecutionEngine();
            engine.PushContext(new ExecutionContext(engine,code));
            while (true) {
                if(engine.Contexts.length == 0){
                    break;
                }
                engine.ExecuteCode();
                if (!engine.ValidateOp()){
                    break;
                }
                System.out.println(engine.EvaluationStack.Count()+"  "+Helper.toHexString(new byte[]{engine.OpCode.getByte()})+" "+engine.OpExec.Name+ "     "+engine.EvaluationStack.info());
                engine.StepInto();
            }
            System.out.println("Stack Count:"+engine.EvaluationStack.Count());
            System.out.println("Result:"+engine.EvaluationStack.Peek(0).GetBigInteger().longValue());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
