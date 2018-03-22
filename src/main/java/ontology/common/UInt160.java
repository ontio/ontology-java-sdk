package ontology.common;

import ontology.crypto.Base58;
import ontology.crypto.Digest;

/**
 * Custom type which inherits base class defines 20-bit data, 
 * it mostly used to defined contract address
 * 
 * @author 12146
 * @since  JDK1.8
 *
 */
public class UInt160 extends UIntBase implements Comparable<UInt160> {
    public static final UInt160 ZERO = new UInt160();
    public static final byte COIN_VERSION = 0x41;

    public UInt160() {
        this(null);
    }

    public UInt160(byte[] value) {
        super(20, value);
    }

    @Override
    public int compareTo(UInt160 other) {
        byte[] x = this.data_bytes;
        byte[] y = other.data_bytes;
        for (int i = x.length - 1; i >= 0; i--) {
        	int r = Byte.toUnsignedInt(x[i]) - Byte.toUnsignedInt(y[i]);
        	if (r != 0) {
        		return r;
        	}
        }
        return 0;
    }

    public static UInt160 parse(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        if (value.length() != 40) {
            throw new IllegalArgumentException();
        }
        byte[] v = Helper.hexToBytes(value);
        return new UInt160(v);
//        return new UInt160(Helper.reverse(v));
    }

    public static boolean tryParse(String s, UInt160 result) {
        try {
            UInt160 v = parse(s);
            result.data_bytes = v.data_bytes;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public String toBase58() {
        byte[] data = new byte[25];
        data[0] = COIN_VERSION;
        System.arraycopy(toArray(), 0, data, 1, 20);
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        System.arraycopy(checksum, 0, data, 21, 4);
        return Base58.encode(data);
    }
    public static UInt160 decodeBase58(String address) {
        byte[] data = Base58.decode(address);
        if (data.length != 25) {
            throw new IllegalArgumentException();
        }
        if (data[0] != COIN_VERSION) {
            throw new IllegalArgumentException();
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        for (int i = 0; i < 4; i++) {
            if (data[data.length - 4 + i] != checksum[i]) {
                throw new IllegalArgumentException();
            }
        }
        byte[] buffer = new byte[20];
        System.arraycopy(data, 1, buffer, 0, 20);
        return new UInt160(buffer);
    }
}