package com.ca.arcflash.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;

public class HASecurityManager {

	private static final Logger logger = Logger.getLogger(HASecurityManager.class);
	
	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;
	
	static{
		try {
			final String seed = "1234567890!@#$%^&*()";
			SecureRandom random = new SecureRandom(seed.getBytes());
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(56,random);
			SecretKey key = generator.generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error(e.getMessage());
		} catch (InvalidKeyException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static byte[] encrypt(String plainText){
		try {
			
			
			byte[] encryptedBytes = encryptCipher.doFinal(plainText.getBytes());
			return encryptedBytes;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	
	
	public static String decrypt(String encyptedText){
		try {
			byte[] plainByte = decryptCipher.doFinal(encyptedText.getBytes());
			return new String(plainByte);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	
	public static byte[] encrypt(Object obj){
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			JAXB.marshal(obj, buffer);
			byte[] jobBytes = encryptCipher.doFinal(buffer.toByteArray());
			return jobBytes;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Object decrypt(File file,Class org){
		FileInputStream fin = null;
		ByteArrayOutputStream bufferOut = null;
		ByteArrayInputStream inputStream = null;
		try {
			fin = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			bufferOut = new ByteArrayOutputStream();
			while ((len=fin.read(buffer)) != -1) {
				bufferOut.write(buffer,0,len);
				Arrays.fill(buffer, (byte)0);
			}
			byte[] cipheredByte = decryptCipher.doFinal(bufferOut.toByteArray());
			inputStream = new ByteArrayInputStream(cipheredByte);
			JAXBContext ctx = JAXBContext.newInstance(org);
			Object result = ctx.createUnmarshaller().unmarshal(inputStream);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			try {
				if(fin != null) fin.close();
				if(inputStream != null) inputStream.close();
				if(bufferOut != null) bufferOut.close();
			}catch(Throwable t) {}
		}
	}
}
