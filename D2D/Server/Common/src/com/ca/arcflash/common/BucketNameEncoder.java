package com.ca.arcflash.common;
import java.io.UnsupportedEncodingException;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class BucketNameEncoder {
	static BitSet needEncoding;
	static final int caseDiff = ('a' - 'A');
	static String dfltEncName = null;

	static {
		needEncoding = new BitSet(256);			
		needEncoding.set(' '); 
		needEncoding.set('-');
		needEncoding.set('_');
		needEncoding.set('.');
		needEncoding.set('*');
		needEncoding.set('%');
		needEncoding.set('(');
		needEncoding.set(')');
		needEncoding.set('!');
		needEncoding.set('@');
		needEncoding.set('#');
		needEncoding.set('$');
		needEncoding.set('^');
		needEncoding.set('&');
		needEncoding.set('+');
		needEncoding.set('~');
		needEncoding.set('`');
		needEncoding.set(',');
		needEncoding.set('<');
		needEncoding.set('>');
		needEncoding.set('?');
		needEncoding.set('/');
		needEncoding.set('|');
		needEncoding.set('\\');
		needEncoding.set('{');
		needEncoding.set('}');
		needEncoding.set('[');
		needEncoding.set(']');
		needEncoding.set(':');
		needEncoding.set(';');	
		needEncoding.set('"');		
		dfltEncName = AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
	}

	private BucketNameEncoder() {
	}
	
	public static String encodeWithUTF8(String str) throws UnsupportedEncodingException {
		return encode(str, "UTF-8");
	}
	
	public static String encode(String s, String enc) throws UnsupportedEncodingException {

		boolean needToChange = false;
		StringBuffer out = new StringBuffer(s.length());
		Charset charset;
		CharArrayWriter charArrayWriter = new CharArrayWriter();

		if (enc == null)
			throw new NullPointerException("charsetName");

		try {
			charset = Charset.forName(enc);
		} catch (IllegalCharsetNameException e) {
			throw new UnsupportedEncodingException(enc);
		} catch (UnsupportedCharsetException e) {
			throw new UnsupportedEncodingException(enc);
		}

		for (int i = 0; i < s.length();) {
			int c = (int) s.charAt(i);
			// System.out.println("Examining character: " + c);
			if (!needEncoding.get(c)) {
				if (c == ' ') {
					c = '+';
					needToChange = true;
				}
				// System.out.println("Storing: " + c);
				out.append((char) c);
				i++;
			} else {
				// convert to external encoding before hex conversion
				do {
					charArrayWriter.write(c);
					if (c >= 0xD800 && c <= 0xDBFF) {

						if ((i + 1) < s.length()) {
							int d = (int) s.charAt(i + 1);

							if (d >= 0xDC00 && d <= 0xDFFF) {

								charArrayWriter.write(d);
								i++;
							}
						}
					}
					i++;
				} while (i < s.length()
						&& needEncoding.get((c = (int) s.charAt(i))));

				charArrayWriter.flush();
				String str = new String(charArrayWriter.toCharArray());
				byte[] ba = str.getBytes(charset);
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);

					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
					ch = Character.forDigit(ba[j] & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
				}
				charArrayWriter.reset();
				needToChange = true;
			}
		}

		return (needToChange ? out.toString() : s).toLowerCase().replace("%", "-");
	}
	
}
