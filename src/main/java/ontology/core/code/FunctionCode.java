package ontology.core.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ontology.common.UInt160;
import ontology.core.scripts.Program;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.Serializable;
import ontology.core.contract.ContractParameterType;

public class FunctionCode implements ICode, Serializable{
	public byte[] code;
	public ContractParameterType[] parameterTypes;
	public ContractParameterType returnType;
	public UInt160 scriptHash;

	public FunctionCode(byte[] c,ContractParameterType[] params,ContractParameterType returntype){
		code = c;
		parameterTypes = params;
		returnType = returntype;
	}
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		code = reader.readVarBytes();
		parameterTypes = toEnum(reader.readVarBytes());
		returnType = toEnum(reader.readByte());
	}

	@Override
	public void serialize(BinaryWriter writer) throws IOException {

		writer.writeVarBytes(code);
		writer.writeVarBytes(toByte(parameterTypes));
		writer.writeByte((byte)returnType.getValue());
	}
	
	private ContractParameterType toEnum(byte bt) {
		return Arrays.stream(ContractParameterType.values()).filter(p -> p.getValue() == bt).findAny().get();
	}
	private ContractParameterType[] toEnum(byte[] bt) {
		if(bt == null) {
			return null;
		}
		List<ContractParameterType> list = new ArrayList<ContractParameterType>();
		for(byte b: bt) {
			ContractParameterType type = toEnum(b);
			list.add(type);
		}
		return list.stream().toArray(ContractParameterType[]::new);
	}
	private byte[] toByte(ContractParameterType[] types) {
		if(types == null) {
			return new byte[0];
		}
		int len = types.length;
		byte[] bt = new byte[len];
		for(int i=0; i<len; ++i) {
			bt[i] = (byte) types[i].getValue();
		}
		return bt;
	}
	

	@Override
	public byte[] getCode() {
		return code;
	}

	@Override
	public ContractParameterType[] getParameterList() {
		return parameterTypes;
	}

	@Override
	public ContractParameterType getReturnType() {
		return returnType;
	}

	public UInt160 getCodeHash() {
		if(scriptHash == null) {
			scriptHash = Program.toScriptHash(getCode());
		}
		return scriptHash;
	}
}
