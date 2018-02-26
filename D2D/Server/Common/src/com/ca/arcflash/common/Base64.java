package com.ca.arcflash.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

public class Base64 {
	
	private static final Logger logger = Logger.getLogger(Base64.class);
	 
    private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
 
    public static byte[] paddInsufficient(int length, byte[] bytes) {
        byte[] padded = new byte[length]; 
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }
 
    public static String decode(String encryptedStr) {
    	if(encryptedStr == null)
    		return null;
    	
    	String result = "";
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	for(int i = 0, count = encryptedStr.length(); i < count; i += 4) {
    		
			int j = leftShit(encryptedStr.charAt(i), 18)
    		 		| leftShit(encryptedStr.charAt(i+1), 12) 
    		 		| leftShit(encryptedStr.charAt(i+2), 6)
    		 		| leftShit(encryptedStr.charAt(i+3), 0); 
    		 
			try {
				out.write(new byte[] {(byte)(j >>> 16), (byte)(j >>> 8), (byte)j});
			} catch (IOException e) {
				logger.error("decryption error:" + e.getMessage(), e);
			}
			
    	}
    	
    	int paddingCount = 0;
		if(encryptedStr.indexOf('=') > 0)
			paddingCount = encryptedStr.length() - encryptedStr.indexOf('=');
		
    	byte[] s = out.toByteArray();
    	try {
    		result = new String(s, 0, s.length - paddingCount, "UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		logger.error("decryption error:" + e.getMessage());
    		result = new String(s, 0, s.length - paddingCount);
    	}
    	
    	return result;
    }

	private static int leftShit(char ch, int count) {
		int index = base64code.indexOf(ch);
		if(index < 0)
			index = 0;
		
		return index << count;
	}
    
    public static String encode(String str) {
    	
    	if(str == null)
    		return null;
 
        byte[] byteArray;
        try {
            byteArray = str.getBytes("UTF-8");  
        } catch (UnsupportedEncodingException e) {
            byteArray = str.getBytes(); 
        }
        
        int paddingCount = (3 - (byteArray.length % 3)) % 3;
        //make sure the the number of bytes is the times of 3.
        byteArray = paddInsufficient(byteArray.length + paddingCount, byteArray);
        // process 3 bytes at a time
        String encoded = "";
        for (int i = 0; i < byteArray.length; i += 3) {
            int j = ((byteArray[i] & 0xff) << 16) +
                ((byteArray[i + 1] & 0xff) << 8) + 
                (byteArray[i + 2] & 0xff);
            encoded = encoded + base64code.charAt((j >> 18) & 0x3f) +
                base64code.charAt((j >> 12) & 0x3f) +
                base64code.charAt((j >> 6) & 0x3f) +
                base64code.charAt(j & 0x3f);
        }
        //replace encoded padding nulls with "="
        return encoded.substring(0, encoded.length() -
            paddingCount) + "==".substring(0, paddingCount);
 
    }
    
    public static void main(String[] args) {
    	args = new String[]{"#@#%$$/#$",
    			"#@#%$$#dasfmlmpdfde?$/",
    			"#@#%$$#dasfmlmpdfde?$234235",
    			""};
    	 for (int i = 0; i < args.length; i++) {
			String original = args[i];
			System.out.println("    " + original);
			String encode = encode(original);
			System.out.println("    " + encode);
			String decode = decode(encode);
			System.out.println("    " + decode);
			System.out.println("Result:" + original.equals(decode));
		}
 
    }
 
}
