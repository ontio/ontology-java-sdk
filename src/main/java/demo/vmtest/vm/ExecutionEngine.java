package demo.vmtest.vm;

import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptOp;
import demo.vmtest.utils.PushData;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecutionEngine {

    public RandomAccessStack EvaluationStack = new RandomAccessStack();
    public RandomAccessStack AltStack = new RandomAccessStack();
    public VMState State;
    public List<ExecutionContext> Contexts = new ArrayList<>();
    public ExecutionContext Context;
    public ScriptOp OpCode;
    public byte OpCodeValue;
    public OpExec OpExec;

    public ExecutionEngine() {

    }

    public ExecutionContext CurrentContext() {
        if (this.Contexts.size() == 0) {
            return null;
        }
        return this.Contexts.get(this.Contexts.size() - 1);
    }

    public void PopContext() {
        if (this.Contexts.size() > 0) {
            this.Contexts.remove(this.Contexts.size() - 1);
        }
        this.Context = CurrentContext();
    }

    public void PushContext(ExecutionContext ctx) {
        this.Contexts.add(ctx);
        this.Context = CurrentContext();
    }

    public boolean Execute() throws Exception {
        this.State = VMState.valueOf(this.State.getValue() & VMState.BREAK.getValue());
        while (true) {
            if (this.State == VMState.FAULT || this.State == VMState.HALT || this.State == VMState.BREAK) {
                break;
            }
            if (!StepInto()) {
                return false;
            }
        }
        return true;
    }

    public boolean ExecuteCode() throws Exception {
        byte code = this.Context.OpReader.reader.readByte();
        this.OpCode = ScriptOp.valueOf(code);
        OpCodeValue = code;
        return true;
    }

    public boolean ValidateOp() {
        OpExec = OpExecList.getOpExec(this.OpCode);
        if (OpExec == null) {
            System.out.println(this.OpCode + " does not support the operation code");
            return false;
        }
        return true;
    }

    public boolean StepInto() {
        try {
            this.State = ExecuteOp();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public VMState ExecuteOp() {
        if (this.OpCodeValue >= ScriptOp.OP_PUSHBYTES1.getByte() && this.OpCodeValue <= ScriptOp.OP_PUSHBYTES75.getByte()) {
            PushData.PushData(this, this.Context.OpReader.ReadBytes(this.OpCodeValue));
            return VMState.NONE;
        }
        if (!this.OpExec.Validator(this)) {
            return VMState.FAULT;
        }
        return this.OpExec.Exec(this);
    }
}






