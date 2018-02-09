package ontology.io.json;

import java.io.BufferedReader;
import java.io.IOException;

public class JNumber extends JObject {
    private double _value;
    public double value() { 
    	return _value; 
    }

    public JNumber(double val) {
        this._value = val;
    }

    @Override
    public boolean asBoolean() {
        if (_value == 0) {
            return false;
        }
        return true;
    }

    @Override
    public double asNumber() {
        return _value;
    }

    @Override
    public String asString() {
        return String.valueOf(_value);
    }

    @Override
    public boolean canConvertTo(Class<?> type) {
        if (type.equals(boolean.class)) {
            return true;
        }
        if (type.equals(double.class)) {
            return true;
        }
        if (type.equals(String.class)) {
            return true;
        }
        return false;
    }

    static JNumber parseNumber(BufferedReader reader) throws IOException {
        skipSpace(reader);
        StringBuilder sb = new StringBuilder();
        while (true) {
        	reader.mark(1);
            int c = reader.read();
            if (c >= '0' && c <= '9' || c == '.' || c == '-') {
                sb.append((char)c);
            } else {
            	reader.reset();
                break;
            }
        }
        return new JNumber(Double.parseDouble(sb.toString()));
    }

    @Override
    public String toString() {
        return asString();
    }
}
