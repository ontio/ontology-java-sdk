/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

/**
 * Number type in block. this type Can be accurate to 64-bit fixed-point, the rounding 
 * error to a minimum. By controlling the multiplier's accuracy, 
 * the rounding error can be completely eliminated
 * 
 * @author 12146
 * @since  JDK1.8
 */
public class Fixed8 implements Comparable<Fixed8>, Serializable {
    public static  int DefaultPrecision = 8;//8
    private static final long D = (long)Math.pow(10,DefaultPrecision);//100000000L;

    private long value;

    public static final Fixed8 MAX_VALUE = new Fixed8(Long.MAX_VALUE);

    public static final Fixed8 MIN_VALUE = new Fixed8(Long.MIN_VALUE);

    public static final Fixed8 ONE = new Fixed8(D);

    public static final Fixed8 SATOSHI = new Fixed8(1);

    public static final Fixed8 ZERO = new Fixed8(0);
    
    public Fixed8() {
    	this(0);
    }

    public Fixed8(long data) {
        this.value = data;
    }

    public Fixed8 abs() {
        if (value >= 0) {
        	return this;
        }
        return new Fixed8(-value);
    }

    @Override
    public int compareTo(Fixed8 other) {
        return Long.compare(value, other.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fixed8)) {
        	return false;
        }
        return value == ((Fixed8) obj).value;
    }

    public static Fixed8 fromDecimal(BigDecimal val) {
        return new Fixed8(val.multiply(new BigDecimal(D)).longValueExact());
    }
    
    public static Fixed8 fromLong(long val) {
    	if (val < 0 || val > Long.MAX_VALUE / D) {
    		throw new IllegalArgumentException();
    	}
    	return new Fixed8(val * D);
    }
    
    public static Fixed8 parse(String s) {
        return fromDecimal(new BigDecimal(s));
    }

    public long getData() { 
    	return value; 
    }

    @Override
    public int hashCode() {
        return Long.valueOf(value).hashCode();
    }

    public static Fixed8 max(Fixed8 first, Fixed8 ...others) {
        for (Fixed8 other : others) {
            if (first.compareTo(other) < 0) {
            	first = other;
            }
        }
        return first;
    }

    public static Fixed8 min(Fixed8 first, Fixed8 ...others) {
        for (Fixed8 other : others) {
            if (first.compareTo(other) > 0) {
            	first = other;
            }
        }
        return first;
    }

    public static Fixed8 sum(Fixed8[] values) {
    	return sum(values, p -> p);
    }
    
    public static <T> Fixed8 sum(T[] values, Function<T, Fixed8> selector) {
    	Fixed8 sum = Fixed8.ZERO;
        for (T item : values) {
            sum = sum.add(selector.apply(item));
        }
        return sum;
    }

    @Override
    public String toString() {
        BigDecimal v = new BigDecimal(value);
        v = v.divide(new BigDecimal(D), DefaultPrecision, BigDecimal.ROUND_UNNECESSARY);
        return v.toPlainString();
    }

    public static boolean tryParse(String s, Fixed8 result) {
        try {
            BigDecimal val = new BigDecimal(s);
            result.value = val.longValueExact();
            return true;
        } catch(NumberFormatException | ArithmeticException ex) {
            return false;
        }
    }

    public long toLong() {
        return value / D;
    }

    public Fixed8 multiply(long other) {
        return new Fixed8(value * other);
    }

    public Fixed8 divide(long other) {
        return new Fixed8(value / other);
    }

    public Fixed8 add(Fixed8 other) {
    	return new Fixed8(Math.addExact(this.value, other.value));
    }

    public Fixed8 subtract(Fixed8 other) {
    	return new Fixed8(Math.subtractExact(this.value, other.value));
    }

    public Fixed8 negate() {
        return new Fixed8(-value);
    }

	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeLong(value);
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		value = reader.readLong();
	}
}