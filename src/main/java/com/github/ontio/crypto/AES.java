package com.github.ontio.crypto;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.security.auth.DestroyFailedException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AES {
	private static final String KEY_ALGORITHM = "AES";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException {
		if (key.length != 32 || iv.length != 16) {
			throw new IllegalArgumentException();
		}
		try {
			SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
			AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
			params.init(new IvParameterSpec(iv));
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
			return cipher.doFinal(encryptedData);
		} catch (NoSuchAlgorithmException | InvalidParameterSpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
		if (key.length != 32 || iv.length != 16) {
			throw new IllegalArgumentException();
		}
		try {
			SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
			AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
			params.init(new IvParameterSpec(iv));
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | InvalidParameterSpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] generateIV() {
		byte[] iv = new byte[16];
		SecureRandom rng = new SecureRandom();
		rng.nextBytes(iv);
		return iv;
	}
	
	public static byte[] generateKey() {
		SecretKey key = null;
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
			keyGenerator.init(256);
			key = keyGenerator.generateKey();
			return key.getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (key != null) {
				try {
					key.destroy();
				} catch (DestroyFailedException ex) {
				}
			}
		}
	}
	
	public static byte[] generateKey(String password) {
		byte[] passwordBytes = null, passwordHash = null;
		try {
			passwordBytes = password.getBytes("UTF-8");
			passwordHash = Digest.sha256(passwordBytes);
			return Digest.sha256(passwordHash);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (passwordBytes != null) {
				Arrays.fill(passwordBytes, (byte)0);
			}
			if (passwordHash != null) {
				Arrays.fill(passwordHash, (byte)0);
			}
		}
	}
}
