package com.github.ontio.io.json;

import java.io.*;
import java.util.*;

public class JArray extends JObject implements List<JObject> {
    private List<JObject> items = new ArrayList<JObject>();

    public JArray(JObject ...items) {
        this.items.addAll(Arrays.asList(items));
    }

    public JArray(Collection<? extends JObject> items) {
        this.items.addAll(items);
    }

    @Override
    public boolean add(JObject item) {
        return items.add(item);
    }
    
    @Override
    public void add(int index, JObject element) {
    	items.add(index, element);
    }
    
    @Override
    public boolean addAll(Collection<? extends JObject> c) {
    	return items.addAll(c);
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends JObject> c) {
    	return items.addAll(index, c);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean contains(Object item) {
        return items.contains(item);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
    	return items.containsAll(c);
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o == null) {
    		return false;
    	}
    	if (!(o instanceof JArray)) {
    		return false;
    	}
    	JArray array = (JArray)o;
    	return this.items.equals(array.items);
    }

    @Override
    public JObject get(int index) {
        return items.get(index);
    }
    
    @Override
    public int hashCode() {
    	return items.hashCode();
    }
    
    @Override
    public int indexOf(Object o) {
    	return items.indexOf(o);
    }
    
    @Override
    public boolean isEmpty() {
    	return items.isEmpty();
    }

	@Override
	public Iterator<JObject> iterator() {
		return items.iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<JObject> listIterator() {
		return items.listIterator();
	}
	
	@Override
	public ListIterator<JObject> listIterator(int index) {
		return items.listIterator(index);
	}

    static JArray parseArray(BufferedReader reader) throws IOException {
        skipSpace(reader);
        if (reader.read() != '[') {
        	throw new IOException();
        }
        skipSpace(reader);
        JArray array = new JArray();
        while (true) {
        	reader.mark(1);
        	int c = reader.read();
        	if (c == ']') {
        		break;
        	}
        	if (c != ',') {
        		reader.reset();
        	}
            JObject obj = JObject.parse(reader);
            array.items.add(obj);
            skipSpace(reader);
        }
        return array;
    }
    
    @Override
    public JObject remove(int index) {
    	return items.remove(index);
    }

    @Override
    public boolean remove(Object item) {
        return items.remove(item);
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
    	return items.removeAll(c);
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
    	return items.retainAll(c);
    }
    
    @Override
    public JObject set(int index, JObject jobj) {
        return items.set(index, jobj);
    }

    @Override
    public int size() {
        return items.size();
    }
    
    @Override
    public List<JObject> subList(int fromIndex, int toIndex) {
    	return items.subList(fromIndex, toIndex);
    }
    
    @Override
    public Object[] toArray() {
    	return items.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
    	return items.toArray(a);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (JObject item : items) {
            if (item == null) {
                sb.append("null");
            } else {
                sb.append(item);
            }
            sb.append(',');
        }
        if (items.size() == 0) {
            sb.append(']');
        } else {
            sb.setCharAt(sb.length() - 1, ']');
        }
        return sb.toString();
    }
}
