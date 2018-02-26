package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.ws.Holder;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncDataWritter {
	private final int BUFFER = 2048;
	private String outputPath = null;
	private String sourcePath = null;
	private String fileName = null;
	private IMySyncService service = null;
	private static final Logger logger = Logger.getLogger(SyncDataWritter.class);
	public SyncDataWritter(IMySyncService service) {
		this.service = service;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getOutputPath() {
		return outputPath;
	}

	public void setPath(String sourcePath, String outputFolder) {
		int i = sourcePath.lastIndexOf(File.separatorChar);
		fileName = sourcePath.substring(i + 1);
		
		this.sourcePath = sourcePath;
		this.outputPath = outputFolder + fileName;
	}
	
	public boolean Write() throws IOException {

		FileOutputStream writeFile = new FileOutputStream(outputPath
				+ ConfigurationOperator._ZipFileExtension);
		Holder<SyncFileType> syncFileInfo = new Holder<SyncFileType>();
		syncFileInfo.value = new SyncFileType();
		syncFileInfo.value.setStrFileName(sourcePath);
		syncFileInfo.value.setStartOffset(0);
		syncFileInfo.value.setReadSize(0);
		syncFileInfo.value.setMaxSendSize(1024 * 1024);

		do {
			Holder<String> encodedString = new Holder<String>();
			service.transferDataWithBase64(syncFileInfo, encodedString);
			if (encodedString.value == null
					|| syncFileInfo.value.getReadSize() == 0) {
				service.syncFileEnd(syncFileInfo.value.getStrFileName());
				break;
			}

			byte[] buffer = DeCodeBase64(encodedString.value);
			if (buffer == null || syncFileInfo.value.getReadSize() <= 0)
				break;
			writeFile.write(buffer, 0, syncFileInfo.value.getReadSize());
		} while (syncFileInfo.value.getReadSize() > 0);

		writeFile.close();
		return UnzipDataFile(outputPath);

	}
	
	public byte[] DeCodeBase64(@NotPrintAttribute String src) {
		BASE64Decoder base64Decoder = new BASE64Decoder();
    	byte[] buffer = null;
		try {
			buffer = base64Decoder.decodeBuffer(src);
		} catch (IOException e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		}
    	return buffer;
	}
	
	public boolean UnzipDataFile(String filePath) {
		try {
			// Open the compressed file
			String inFilename = filePath + 
				ConfigurationOperator._ZipFileExtension;
			
			File f = new File(inFilename);
			if(f.length() > 0)
			{
				GZIPInputStream in = new GZIPInputStream(new FileInputStream(
						inFilename));

				// Open the output file
				String outFilename = filePath;
				OutputStream out = new FileOutputStream(outFilename);

				// Transfer bytes from the compressed file to the output file
				byte[] buf = new byte[BUFFER];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Close the file and stream
				in.close();
				out.close();
			}

			f.delete();
			return true;
	      } catch(Exception e) {
	    	  logger.error(e.getMessage(), e);
	        return false;
	      }
	}
}
