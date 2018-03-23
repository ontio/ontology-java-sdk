package com.github.ontio.io.json;

import java.io.*;

public class JBoolean extends JObject {
    private final boolean _value;
    public boolean value() { 
    	return _value; 
    }

    public JBoolean(boolean val) {
        this._value = val;
    }

    @Override
    public boolean asBoolean() {
        return _value;
    }

    @Override
    public String asString() {
        return String.valueOf(_value).toLowerCase();
    }

    @Override
    public boolean canConvertTo(Class<?> type) {
        if (type.equals(boolean.class)) {
            return true;
        }
        if (type.equals(String.class)) {
            return true;
        }
        return false;
    }

    static JBoolean parseBoolean(BufferedReader reader) throws IOException {
        skipSpace(reader);
        int firstChar = reader.read();
        if (firstChar == 't') {
            int c2 = reader.read();
            int c3 = reader.read();
            int c4 = reader.read();
            if (c2 == 'r' && c3 == 'u' && c4 == 'e') {
                return new JBoolean(true);
            }
        } else if (firstChar == 'f') {
            int c2 = reader.read();
            int c3 = reader.read();
            int c4 = reader.read();
            int c5 = reader.read();
            if (c2 == 'a' && c3 == 'l' && c4 == 's' && c5 == 'e') {
                return new JBoolean(false);
            }
        }
        throw new IOException();
    }

    @Override
    public String toString() {
        return asString();
    }
}