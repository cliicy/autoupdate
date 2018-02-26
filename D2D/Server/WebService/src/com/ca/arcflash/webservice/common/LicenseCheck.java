package com.ca.arcflash.webservice.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.util.ServiceUtils;

public abstract class LicenseCheck<T> {
	
	public static final int LicenseValideTimeInHour = 24;
	
	public static final String LicenseDirectory = CommonUtil.D2DHAInstallPath+"Configuration\\license";
	
	private LicenseMapWrapper licenseCache = new LicenseMapWrapper();
	
	protected static Logger logger = Logger.getLogger(LicenseCheck.class);
	
	private String licFileName;
	
	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;
	
	private static String hostName = null;
	
	static{
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			String seed = "1234567890!@#$%^&*()";
			SecureRandom random = new SecureRandom(seed.getBytes());
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(56,random);
			SecretKey key = generator.generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (Exception e) {}
	}
	
	protected LicenseCheck(String licFileName){
		this.licFileName = licFileName;
		readLicenseFromFile();
	}
	
	private void readLicenseFromFile() {
		
		String fullFileName = LicenseDirectory + "\\" + licFileName;
		
		File file = new File(fullFileName);
		if(!file.exists())
			return;
		FileInputStream inputStream = null;
		ByteArrayOutputStream stream = null;
		ByteArrayInputStream input = null;
		try {
			//read the vcmLicens.lic file
			inputStream = new FileInputStream(fullFileName);
			byte[] inBytes = new byte[1024];
			int readNum = -1;
			stream = new ByteArrayOutputStream();
			while((readNum = inputStream.read(inBytes)) > 0) {
				stream.write(inBytes, 0, readNum);
			}
			
			byte[] objBytes = decryptCipher.doFinal(stream.toByteArray());
//			byte[] objBytes = stream.toByteArray();
			input = new ByteArrayInputStream(objBytes);
			
			LicenseMapWrapper cache = JAXB.unmarshal(input, LicenseMapWrapper.class);
			
			//Check whether the read licenseCache is for this machine.
			if(hostName != null && hostName.equalsIgnoreCase(cache.getHostNameLicenseApplies()))
				licenseCache = cache;
			else
				logger.error("The license in file " + licFileName + " can not be applied to this machine " + hostName
						+ ", but to " + cache.getHostNameLicenseApplies());
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			ServiceUtils.closeResource(input);
			ServiceUtils.closeResource(stream);
			ServiceUtils.closeResource(inputStream);
		}
	}
	
	private LICENSEDSTATUS getLicenseFromCache(T subject) {
			
		LicenseObject value = null;
		synchronized (this) {
			value = licenseCache.getLicenseMap().get(getCacheKey(subject));
		}
		
		LICENSEDSTATUS license = null;
		if(value != null) { 
			long elapse = (System.currentTimeMillis() - value.getLicenseFetchTime()); 
			if(elapse <  LicenseCheck.LicenseValideTimeInHour * 60 * 60 * 1000)
				license = value.getLicenseValue();
		}
		
		return license;
	}

	
	protected abstract String getCacheKey(T subject);

	private void writeLicenseToFile() {
		try {
			File directory = new File(LicenseDirectory);
			
			if(!directory.exists()) {
				directory.mkdir();
			}
			
			
			String fullFileName = LicenseDirectory + "\\" + licFileName;
			File file = new File(fullFileName);
			FileOutputStream outStream = new FileOutputStream(file);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			licenseCache.setHostNameLicenseApplies(hostName);
			JAXB.marshal(licenseCache, buffer);
			
			byte[] jobBytes = encryptCipher.doFinal(buffer.toByteArray());
//			byte[] jobBytes = buffer.toByteArray();
			
			outStream.write(jobBytes);
			outStream.flush();
			outStream.close();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public LICENSEDSTATUS checkLicense(T subject) throws LicenseCheckException {
		LICENSEDSTATUS license = null;
		try {
			license = getLicenseFromEdge(subject);
			updateLicenseToCacheAndFile(subject, license);
		}
		catch(LicenseCheckException le) {
			license = getLicenseFromCache(subject);
			if(license != null)
				return license;
			else
				throw le;
		}
		catch(Exception e){
			logger.error("Fail to get license from Edge. subject" + subject + ". Error:" + e.getMessage());
			license = getLicenseFromCache(subject);
		}
		
		
		return license;
	}

	private void updateLicenseToCacheAndFile(T subject, LICENSEDSTATUS license) {
		
		if(license == null)
			return;
		
		try {
			synchronized (this) {
				LicenseObject value = new LicenseObject(System.currentTimeMillis(), license);
				String key = getCacheKey(subject);
				value.setLicenseSubject(key);
				licenseCache.getLicenseMap().put(key, value);
				
				writeLicenseToFile();
			}
		}
		catch(Exception e) {
			logger.error("Update license to file fails. Error:" + e.getMessage(), e);
		}
	}
	
	protected abstract LICENSEDSTATUS getLicenseFromEdge(T subject) throws LicenseCheckException;
	
/*	public static void main(String[] args) {
		VCMLicenseCheck check = new VCMLicenseCheck();
		LicenseObject value = new LicenseObject(1, LICENSEDSTATUS.VALID);
		value.setLicenseSubject("param1");
		((LicenseCheck<MachineDetail>)check).licenseCache.getLicenseMap().put("param1", value);
		
		value = new LicenseObject(3, LICENSEDSTATUS.INVALID);
		value.setLicenseSubject("param3");
		((LicenseCheck<MachineDetail>)check).licenseCache.getLicenseMap().put("param3", value);
		
		value = new LicenseObject(2, LICENSEDSTATUS.TRIAL);
		value.setLicenseSubject("param2");
		((LicenseCheck<MachineDetail>)check).licenseCache.getLicenseMap().put("param2", value);
		((LicenseCheck<MachineDetail>)check).writeLicenseToFile();
		
		VCMLicenseCheck check1 = new VCMLicenseCheck();
		System.out.println(((LicenseCheck<MachineDetail>)check1).licenseCache.getLicenseMap());
		
		MachineDetail d  = new MachineDetail();
		d.setHostName("dd");
		
		try {
			System.out.println(check1.checkLicense(d));
		} catch (LicenseCheckException e) {
//			e.printStackTrace();
		}
		
	}
*/
}
