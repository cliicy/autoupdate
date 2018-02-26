package com.ca.arcserve.edge.app.base.webservice.gateway;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encoder
{
	private Logger logger = Logger.getLogger( Encoder.class );
	
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private final static int KEY_LENGTH = 128;
	
	private static Encoder instance = new Encoder();
	private BASE64Encoder base64encoder = new BASE64Encoder();
	private BASE64Decoder base64decoder = new BASE64Decoder();
	
	private byte[] key = new byte[] { -52, 115, 52, -27, 78, 109, 38, 27, 89, 67, -100, 91, -20, 5, 75, -127 };
	private byte[] iv = new byte[] { 1, 73, -41, 117, -16, -34, -95, -103, 29, -33, 117, 92, 8, -53, -106, 40 };
	
	private SecretKey secretKey = null;
	
	private Encoder()
	{
		try
		{
			secretKey = new SecretKeySpec( key, ALGORITHM );
		}
		catch (Exception e)
		{
			logger.error( "Error initializing encoder.", e );
		}
	}
	
	public static Encoder getInstance()
	{
		return instance;
	}
	
	public String encode( String string )
	{
		try
		{
			Cipher cipher = Cipher.getInstance( TRANSFORMATION );
			cipher.init( Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec( iv ) );
			byte[] encrypted = cipher.doFinal( string.getBytes() );
			return this.base64encoder.encode( encrypted );
		}
		catch (Exception e)
		{
			logger.error( "Error coding string.", e );
			return null;
		}
	}
	
	public String decode( String string )
	{
		try
		{
			byte[] base64decoded = this.base64decoder.decodeBuffer( string );
			Cipher cipher = Cipher.getInstance( TRANSFORMATION );
			cipher.init( Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec( iv ) );
			byte[] decoded = cipher.doFinal( base64decoded );
			return new String( decoded );
		}
		catch (Exception e)
		{
			logger.error( "Error decoding string.", e );
			return null;
		}
	}
	
	public static void main( String[] args )
	{
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance( ALGORITHM );
			keyGen.init( KEY_LENGTH );
			SecretKey key = keyGen.generateKey();
			
			byte[] iv = new byte[KEY_LENGTH / 8];
			SecureRandom random = new SecureRandom();
			random.nextBytes( iv );
			
			System.out.println( "key = " + key );
			System.out.println( "iv = " + iv.toString() );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
