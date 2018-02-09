package ontology.common;

/**
 * Custom type which inherits base class defines 32-bit data, 
 * it mostly used to defined transaction identity
 * 
 * @author 12146
 * @since  JDK1.8
 *
 */
public class UInt256 extends UIntBase implements Comparable<UInt256> {
    public static final UInt256 ZERO = new UInt256();

    public UInt256() {
        this(null);
    }

    public UInt256(byte[] value) {
        super(32, value);
    }

    @Override
    public int compareTo(UInt256 other) {
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

    public static UInt256 parse(String s) {
        if (s == null) {
            throw new NullPointerException(); 
        }
        if (s.startsWith("0x")) {
            s = s.substring(2);
        }
        if (s.length() != 64) {
            throw new IllegalArgumentException(String.format("字符串\"{0}\"无法识别为正确的UInt256。", s));
        }
        byte[] v = Helper.hexToBytes(s);
        return new UInt256(v);
        //return new UInt256(Helper.reverse(v));
    }

    public static boolean tryParse(String s, UInt256 result) {
        try {
            UInt256 v = parse(s);
            result.data_bytes = v.data_bytes;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}