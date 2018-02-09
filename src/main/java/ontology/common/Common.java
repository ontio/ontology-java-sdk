package ontology.common;

import ontology.crypto.Base58;
import ontology.crypto.Digest;
import ontology.crypto.ECC;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class Common implements AutoCloseable {

    public static final byte COIN_VERSION = 0x41;//0x17;
    public static byte[] generateKey64Bit() {
        return ECC.generateKey(64);
    }
    public static String currentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
    public static void print(String ss) {
        System.out.println(now() + " " + ss);
    }

    public static byte[] toAttr(String txDesc) {
        return (now() + "_" + txDesc).getBytes();
    }

    private static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static String toAddress(UInt160 scriptHash) {
    	byte[] data = new byte[25];
    	data[0] = COIN_VERSION;
    	System.arraycopy(scriptHash.toArray(), 0, data, 1, 20);
    	byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
    	System.arraycopy(checksum, 0, data, 21, 4);
        return Base58.encode(data);
    }


    public static UInt160 toScriptHash(String address) {
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

    public static void writeFile(String filePath, String sets) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

}
