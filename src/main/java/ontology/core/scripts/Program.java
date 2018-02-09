package ontology.core.scripts;

import java.io.IOException;

import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.crypto.Digest;
import ontology.io.BinaryReader;
import ontology.io.BinaryWriter;
import ontology.io.JsonReader;
import ontology.io.JsonSerializable;
import ontology.io.Serializable;
import ontology.io.json.JObject;
import ontology.io.json.JString;

/**
 *  脚本
 */
public class Program implements Serializable, JsonSerializable {
    public byte[] parameter;
    public byte[] code;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	parameter = reader.readVarBytes();	// sign data
    	code = reader.readVarBytes();		// pubkey
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeVarBytes(parameter);
    	writer.writeVarBytes(code);
    }

    /**
     *  变成json对象
     *  <returns>返回json对象</returns>
     */
    public JObject json() {
        JObject json = new JObject();
        json.set("code", new JString(Helper.toHexString(code)));
        json.set("parameter", new JString(Helper.toHexString(parameter)));
        return json;
    }

    public static UInt160 toScriptHash(byte[] script) {
    	return new UInt160(Digest.hash160(script));
    }

    @Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		code = Helper.hexToBytes(json.get("Code").asString());
		parameter = Helper.hexToBytes(json.get("Parameter").asString());
	}
}
