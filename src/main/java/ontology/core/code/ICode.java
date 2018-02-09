package ontology.core.code;

import ontology.core.contract.ContractParameterType;

public interface ICode {
	public byte[] getCode(); 
	public ContractParameterType[] getParameterList();
	public ContractParameterType getReturnType();
}
