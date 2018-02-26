/**
 * 
 */
package com.ca.arcflash.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

/**
 * @author lijwe02
 * 
 */
public class CipherUtils {
	private static final Logger logger = Logger.getLogger(CipherUtils.class);

	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;

	static {
		try {
			String seed = "1234567890!@#$%^&*()";
			SecureRandom random = new SecureRandom(seed.getBytes());
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(56, random);
			SecretKey key = generator.generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (Exception e) {
		}
	}

	private CipherUtils() {
	}

	public static final byte[] encryptByteArray(byte[] input) {
		try {
			if (input == null) {
				logger.error("The content for encrypt is null.");
				return null;
			}
			byte[] output = encryptCipher.doFinal(input);
			return output;
		} catch (IllegalBlockSizeException e) {
			logger.error("Failed to encrypt byte array.", e);
		} catch (BadPaddingException e) {
			logger.error("Failed to encrypt byte array.", e);
		}
		return null;
	}

	public static final byte[] decryptByteArray(byte[] input) {
		if (input == null) {
			logger.error("The content for decrypt is null.");
			return null;
		}
		try {
			byte[] output = decryptCipher.doFinal(input);
			return output;
		} catch (IllegalBlockSizeException e) {
			logger.error("Failed to decrypt byte array.", e);
		} catch (BadPaddingException e) {
			logger.error("Failed to decrypt byte array.", e);
		}
		return null;
	}
}
