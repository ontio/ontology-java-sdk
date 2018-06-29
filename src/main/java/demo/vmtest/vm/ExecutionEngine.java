package demo.vmtest.vm;

import com.github.ontio.core.scripts.ScriptOp;

import java.util.Arrays;

/**
 * @Description:
 * @date 2018/6/26
 */
public class ExecutionEngine {

    public RandomAccessStack EvaluationStack = new RandomAccessStack();
    public RandomAccessStack AltStack = new RandomAccessStack();
    public VMState       State;
    public ExecutionContext[] Contexts = new ExecutionContext[0];
    public ExecutionContext Context;
    public ScriptOp OpCode;
    public OpExec OpExec;
    public ExecutionEngine(){

    }
    public ExecutionContext CurrentContext(){
        if(this.Contexts.length ==0){
            return null;
        }
        return this.Contexts[this.Contexts.length-1];
    }
    public void PopContext(){
        this.Contexts = Arrays.copyOf(this.Contexts,this.Contexts.length-1);
        this.Context = CurrentContext();
    }
    public void PushContext(ExecutionContext ctx){
        ExecutionContext[] ctxs = new  ExecutionContext[this.Contexts.length+1];
        for(int i=0;i<this.Contexts.length;i++){
            ctxs[i] = this.Contexts[i];
        }
        ctxs[this.Contexts.length] = ctx;
        this.Contexts = ctxs;
        this.Context = CurrentContext();
    }
    public boolean Execute() throws Exception{
      this.State = VMState.valueOf(this.State.getValue() & VMState.BREAK.getValue());
      while (true){
          if(this.State == VMState.FAULT || this.State == VMState.HALT || this.State == VMState.BREAK){
              break;
          }
          if(!StepInto()){
              return false;
          }
      }
      return true;
    }
    public boolean ExecuteCode()throws Exception{
        byte code = this.Context.OpReader.reader.readByte();
        this.OpCode = ScriptOp.valueOf(code);
        return true;
    }

    public boolean ValidateOp() {
        OpExec = OpExecList.getOpExec(this.OpCode);
        if (OpExec == null){
            System.out.println(this.OpCode+" not found OpExec");
            return false;
        }
        return true;
    }
    public boolean StepInto(){
        try {
            this.State = ExecuteOp();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public  VMState ExecuteOp(){
        if(this.OpCode.getByte() >= ScriptOp.OP_PUSHBYTES1.getByte() && this.OpCode.getByte() <= ScriptOp.OP_PUSHBYTES75.getByte()){
            return VMState.NONE;
        }
        if (!this.OpExec.Validator(this)){
            return VMState.FAULT;
        }
       // System.out.println("==ExecuteOp==");
        return this.OpExec.Exec(this);
    }
}






