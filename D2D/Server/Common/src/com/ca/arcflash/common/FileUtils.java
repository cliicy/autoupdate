/**
 * 
 */
package com.ca.arcflash.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author lijwe02
 * 
 */
public class FileUtils {
	private static final Logger logger = Logger.getLogger(FileUtils.class);

	public static final byte[] readFileToByteArray(String filePath) {
		if (StringUtil.isEmptyOrNull(filePath)) {
			logger.error("The file path is empty.");
			return null;
		}
		File file = new File(filePath);
		return readFileToByteArray(file);
	}

	public static final byte[] readFileToByteArray(File file) {
		if (file == null) {
			logger.error("The file for read is null.");
			return null;
		}
		if (!file.exists()) {
			logger.error("The file:" + file.getAbsolutePath() + " doesn't exists.");
			return null;
		}
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			logger.error("Failed to read file to byte array.", e);
		} catch (IOException e) {
			logger.error("Failed to read file to byte array.", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Failed to close file input stream.");
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error("Failed to close byte array output stream.");
				}
			}
		}
		return null;
	}

	public static final void writeByteArrayToFile(String filePath, byte[] content) {
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			fos = new FileOutputStream(file);
			fos.write(content);
		} catch (FileNotFoundException e) {
			logger.error("Failed to write file.", e);
		} catch (IOException e) {
			logger.error("Failed to write file.", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("Failed to write file.", e);
				}
			}
		}

	}
}
