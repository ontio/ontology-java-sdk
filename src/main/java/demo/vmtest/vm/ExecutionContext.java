package demo.vmtest.vm;

import com.github.ontio.core.scripts.ScriptOp;
import demo.vmtest.utils.VmReader;

public class ExecutionContext {
    public byte[] Code;
    public VmReader OpReader;
    public int InstructionPointer;
    public ExecutionEngine engine;

    public ExecutionContext(ExecutionEngine engine, byte[] code) {
        this.engine = engine;
        Code = code;
        OpReader = new VmReader(code);
        InstructionPointer = 0;
    }
    public int GetInstructionPointer(){
        return OpReader.Position();
    }
    public long SetInstructionPointer(long offset){
        return OpReader.Seek(offset);
    }
    public ScriptOp NextInstruction(){
        return ScriptOp.valueOf(Code[OpReader.Position()]);
    }
    public ExecutionContext Clone(){
        ExecutionContext executionContext = new ExecutionContext(engine,Code);
        executionContext.SetInstructionPointer(this.GetInstructionPointer());
        return executionContext;
    }
}