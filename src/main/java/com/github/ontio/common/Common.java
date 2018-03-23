package com.github.ontio.common;

import com.github.ontio.crypto.ECC;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class Common implements AutoCloseable {

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

    public static void writeFile(String filePath, String sets) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

}
