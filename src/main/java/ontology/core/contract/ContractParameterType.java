package ontology.core.contract;

/**
 * 表示智能合约的参数类型
 */
public enum ContractParameterType {
    /**
     * 签名
     */
    Signature(0x00),
    /**
     * 布尔
     */
    Boolean(0x01),
    /**
     * 整数
     */
    Integer(0x02),
    /**
     * 160位散列值
     */
    Hash160(0x03),
    /**
     * 256位散列值
     */
    Hash256(0x04),
    /**
     * 字节数组
     */
    ByteArray(0x05),
    PublicKey(0x06),
    String(0x07),
    Array(0x10),
    InteropInterface(0xf0),
    Void(0xff);

    private byte value;

    ContractParameterType(int v) {
        value = (byte) v;
    }
    public byte getValue(){
        return value;
    }
}