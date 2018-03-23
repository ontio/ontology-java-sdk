package com.github.ontio.io;

import java.io.*;

/**
 *  为序列化提供一个接口
 */
public interface Serializable {    
    /**
     *  反序列化
     *  <param name="reader">数据来源</param>
     * @throws IOException 
     */
    void deserialize(BinaryReader reader) throws IOException;
    
    /**
     *  序列化
     *  <param name="writer">存放序列化后的结果</param>
     * @throws IOException 
     */
    void serialize(BinaryWriter writer) throws IOException;

    default byte[] toArray() {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
	        try (BinaryWriter writer = new BinaryWriter(ms)) {
	            serialize(writer);
	            writer.flush();
	            return ms.toByteArray();
	        }
        } catch (IOException ex) {
			throw new UnsupportedOperationException(ex);
		}
    }
    
    static <T extends Serializable> T from(byte[] value, Class<T> t) throws InstantiationException, IllegalAccessException {
    	try (ByteArrayInputStream ms = new ByteArrayInputStream(value)) {
    		try (BinaryReader reader = new BinaryReader(ms)) {
    			return reader.readSerializable(t);
    		}
    	} catch (IOException ex) {
			throw new IllegalArgumentException(ex);
		}
    }
}
