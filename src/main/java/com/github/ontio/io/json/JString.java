package com.github.ontio.io.json;

import java.io.*;
import java.util.*;

public class JString extends JObject {
    private String _value;
    public String value() { 
    	return _value; 
    }

    public JString(String val) {
        if (val == null) {
            throw new NullPointerException();
        }
        this._value = val;
    }

    @Override
    public boolean asBoolean() {
        Collection<String> falseValues = new HashSet<String>();
        falseValues.add("0");
        falseValues.add("f");
        falseValues.add("false");
        falseValues.add("n");
        falseValues.add("no");
        falseValues.add("off");
        return ! falseValues.contains(_value.toLowerCase());
    }

    @Override
    public double asNumber() {
        return Double.parseDouble(_value);
    }

    @Override
    public String asString() {
        return _value;
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

    static JString parseString(BufferedReader reader) throws IOException {
        skipSpace(reader);
        char[] buffer = new char[4];
        int firstChar = reader.read();
        if (firstChar != '\"' && firstChar != '\'') throw new IOException();
        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = reader.read();
            if (c == 65535) {
            	throw new IOException();
            }
            if (c == firstChar) {
            	break;
            }
            if (c == '\\') {
                c = (char)reader.read();
                if (c == 'u') {
                    reader.read(buffer, 0, 4);
                    c = Integer.valueOf(new String(buffer), 16);
                }
            }
            sb.append((char)c);
        }
        return new JString(sb.toString());
    }

    @Override
    public String toString() {
    	return "\"" + _value.replaceAll("\"", "\\\"") + "\"";
    }
}