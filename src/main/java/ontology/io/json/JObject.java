package ontology.io.json;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class JObject {
    public static final JObject NULL = null;
    private Map<String, JObject> properties = new HashMap<String, JObject>();
    
    public int size() {
    	return properties.size();
    }

    public JObject get(String name) {
        return properties.get(name);
    }

    public void set(String name, JObject value) {
        properties.put(name, value);
    }

    public boolean asBoolean() {
        throw new UnsupportedOperationException();
    }

    public boolean asBooleanOrDefault(boolean value) {
        if (!canConvertTo(boolean.class))
            return value;
        return asBoolean();
    }

    @SuppressWarnings("rawtypes")
	public <T extends Enum> T asEnum(boolean ignoreCase) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("rawtypes")
	public <T extends Enum> T asEnumOrDefault(T value, boolean ignoreCase) {
        if (!canConvertTo(value.getClass()))
            return value;
        return asEnum(ignoreCase);
    }

    public double asNumber() {
        throw new UnsupportedOperationException();
    }

    public double asNumberOrDefault(double value) {
        if (!canConvertTo(double.class))
            return value;
        return asNumber();
    }

    public String asString() {
        throw new UnsupportedOperationException();
    }

    public String asStringOrDefault(String value) {
        if (!canConvertTo(String.class)) {
            return value;
        }
        return asString();
    }

    public boolean canConvertTo(Class<?> type) {
        return false;
    }

    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    public static JObject parse(Reader reader2) throws IOException {
    	BufferedReader r = reader2 instanceof BufferedReader ? (BufferedReader)reader2 : new BufferedReader(reader2);
        skipSpace(r);
        r.mark(1);
        int firstChar = r.read();
        if (firstChar == '\"' || firstChar == '\'') {
        	r.reset();
            return JString.parseString(r);
        }
        if (firstChar == '[') {
        	r.reset();
            return JArray.parseArray(r);
        }
        if ((firstChar >= '0' && firstChar <= '9') || firstChar == '-') {
        	r.reset();
            return JNumber.parseNumber(r);
        }
        if (firstChar == 't' || firstChar == 'f') {
        	r.reset();
            return JBoolean.parseBoolean(r);
        }
        if (firstChar == 'n') {
        	r.reset();
            return parseNull(r);
        }
        if (firstChar != '{') throw new IOException();
        skipSpace(r);
        JObject obj = new JObject();
        while (true) {
        	r.mark(1);
        	int c = r.read();
        	if (c == '}') {
        		break;
        	}
            if (c != ',') {
            	r.reset();
            }
            skipSpace(r);
            String name = JString.parseString(r).value();
            skipSpace(r);
            if (r.read() != ':') {
            	throw new IOException();
            }
            JObject value = parse(r);
            obj.properties.put(name, value);
            skipSpace(r);
        }
        return obj;
    }

    public static JObject parse(String value) {
        StringReader reader = new StringReader(value);
        try {
			return parse(reader);
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex);
		}
    }

    static JObject parseNull(Reader reader) throws IOException {
        char firstChar = (char)reader.read();
        if (firstChar == 'n') {
            int c2 = reader.read();
            int c3 = reader.read();
            int c4 = reader.read();
            if (c2 == 'u' && c3 == 'l' && c4 == 'l') {
                return null;
            }
        }
        throw new IllegalArgumentException();
    }

    protected static void skipSpace(BufferedReader reader) throws IOException {
    	while (true) {
    		reader.mark(1);
    		int c = reader.read();
    		if (c != ' ' && c != '\r' && c != '\n') {
    			reader.reset();
    			return;
    		}
    	}
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Entry<String, JObject> pair : properties.entrySet()) {
            sb.append('"');
            sb.append(pair.getKey());
            sb.append('"');
            sb.append(':');
            if (pair.getValue() == null) {
                sb.append("null");
            } else {
                sb.append(pair.getValue());
            }
            sb.append(',');
        }
        if (properties.size() == 0) {
            sb.append('}');
        } else {
            sb.setCharAt(sb.length() - 1, '}');
        }
        return sb.toString();
    }
}
