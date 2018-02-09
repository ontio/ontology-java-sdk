package ontology.io;

import java.io.IOException;

import ontology.io.json.JObject;

public interface JsonSerializable {
//	public void toJson(JsonWriter writer);
	public void fromJson(JsonReader reader);

	// 序列化
//    default JObject to() {
//    	JsonWriter writer = new JsonWriter(new JObject());
//    	toJson(writer);
//    	return writer.json();
//    }
    
    // 反序列化
    static <T extends JsonSerializable> T from(JObject json, Class<T> t) throws InstantiationException, IllegalAccessException {
    	JsonReader reader = new JsonReader(json);
    	try {
			return reader.readSerializable(t);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
    }

}
