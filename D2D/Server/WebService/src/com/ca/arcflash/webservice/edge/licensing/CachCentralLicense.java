package com.ca.arcflash.webservice.edge.licensing;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CipherUtils;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.FileUtils;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class CachCentralLicense {
	private static final Logger logger = Logger.getLogger(CachCentralLicense.class);
	private static NativeFacade nativeFacade = new NativeFacadeImpl();

	private static final long EXPIRED_INTERVAL=CommonService.getInstance().getCacheLicenseExpiration();
	private LicenseCheckResult license;
	private long timestamp;
	
	public CachCentralLicense(String path){
		loadLicenseFromFile(path);
	}
	
	public CachCentralLicense(LicenseCheckResult result) {
		this.license = result;
		this.timestamp=System.currentTimeMillis();
	}
	
	public LicenseCheckResult getLicenseWithLogActivity(long jobid) {
		logger.debug("jobid: "+jobid+" cache license timestamp: "+new Date(timestamp));
		if(System.currentTimeMillis()-this.timestamp>EXPIRED_INTERVAL){
			nativeFacade.addLogActivityWithJobID(Constants.AFRES_AFALOG_ERROR,
					jobid,
					Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{WebServiceMessages.getResource("cachedLicenseExpired"),"","","",""});
			return null;
		}
		if(license!=null){
			SimpleDateFormat format = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(), DataFormatUtil.getDateFormatLocale());
			String expiredTime = format.format(new Date(this.timestamp+EXPIRED_INTERVAL));
			nativeFacade.addLogActivityWithJobID(Constants.AFRES_AFALOG_WARNING,
					jobid,
					Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{WebServiceMessages.getResource("useCachedLicense",expiredTime),"","","",""});
		}
		return license;
	}
	
	public void flushToDisk(String filepath) {
		try {
			byte[] timestampBytes = convertToBytes(timestamp);
			byte[] licenseBytes= convertToBytes(license);
			byte[] cacheContent = new byte[licenseBytes.length+8];
			System.arraycopy(timestampBytes , 0, cacheContent, 0, 8);
			System.arraycopy(licenseBytes , 0, cacheContent, 8, licenseBytes.length);
			byte[] encryptedBytes = CipherUtils.encryptByteArray(cacheContent);
			FileUtils.writeByteArrayToFile(filepath, encryptedBytes);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private byte[] convertToBytes(LicenseCheckResult license) {
		if(license==null){
			return new byte[0];
		}
		StringWriter sw = new StringWriter();
		JAXB.marshal(license, sw);
		return sw.toString().getBytes();
	}

	private byte[] convertToBytes(long timestamp) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, timestamp);
		return buffer.array();
	}

	private void loadLicenseFromFile(String path) {
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(path);
			if(fileContent==null){
				return;
			}
			byte[] cacheContent = CipherUtils.decryptByteArray(fileContent);
			if(cacheContent == null || cacheContent.length<8){
				return;
			}
			byte[] timestampBytes = new byte[8];
			System.arraycopy(cacheContent , 0, timestampBytes, 0, timestampBytes.length);
			loadTimestamp(timestampBytes);
			
			byte[] licenseBytes= new byte[cacheContent.length-8];
			System.arraycopy(cacheContent , 8, licenseBytes, 0, licenseBytes.length);
			loadLicense(licenseBytes);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void loadLicense(byte[] licenseBytes) {
		ByteArrayInputStream input = new ByteArrayInputStream(licenseBytes);
		license = JAXB.unmarshal(input, LicenseCheckResult.class);
	}

	private void loadTimestamp(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		timestamp = buffer.getLong();
	}
	
	@Override
	public String toString(){
		StringBuffer sbuf=new StringBuffer();
		sbuf.append("[timestamp: ").append(timestamp);
		sbuf.append(", license: ").append(licenseToString(license));
		sbuf.append("]");
		return sbuf.toString();
	}
	
	private String licenseToString(LicenseCheckResult license) {
		if(license == null){
			return "null";
		}
		StringBuffer sbuf=new StringBuffer();
		sbuf.append("[BundledLicense: ").append(license.getLicense().getDisplayName());
		sbuf.append(", LicenseExpiredState: ").append(license.getState().name());
		sbuf.append(", used_num: ").append(license.getUsed_num());
		sbuf.append("]");
		return sbuf.toString();
	}

//	public static void main(String[] args){
//		String path="G:\\license";
//		LicenseCheckResult result= new LicenseCheckResult(BundledLicense.UDPLIC_ADVANCED_Per_HOST, LicenseExpiredState.Expired, 0);
//		CachCentralLicense cach= new CachCentralLicense(result, null, 0);
//		System.out.println(cach);
//		cach.flushToDisk(path);
//		CachCentralLicense cach2 =new CachCentralLicense(path); 
//		System.out.println(cach2);
//	}
}
