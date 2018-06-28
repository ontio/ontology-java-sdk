package com.github.neo.core;


import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 */
public class Program implements Serializable {
    public byte[] parameter;
    public byte[] code;
    public Program(){}
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	parameter = reader.readVarBytes();	// sign data
    	code = reader.readVarBytes();		// pubkey
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
    	writer.writeVarBytes(parameter);
    	writer.writeVarBytes(code);
    }
    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException {
        return com.github.ontio.core.program.Program.ProgramFromParams(sigData);
    }
    public static byte[] ProgramFromPubKey(byte[] publicKey) throws Exception {
        return com.github.ontio.core.program.Program.ProgramFromPubKey(publicKey);
    }
    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
        return com.github.ontio.core.program.Program.ProgramFromMultiPubKey(m,publicKeys);
    }

}
