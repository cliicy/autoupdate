package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseStatus;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.webservice.jni.model.LicenseAPIReturnType;
import com.ca.lic98.jni.lic98j;
import com.ca.lic98.jni.lic98jException;

public class LicenseModuleWrapper implements ILicenseModuleWrapper {
	private final static Logger logger=Logger.getLogger(LicenseModuleWrapper.class);
	
	lic98j License=new lic98j(); 

	private static ILicenseModuleWrapper instance=null;
	
	public static synchronized ILicenseModuleWrapper getInstance(){
		if(instance==null)
			instance=new LicenseModuleWrapper();
		return instance;
	}
	
//	private static void setInstance(ILicenseModuleWrapper instance_test){
//		instance=instance_test;
//	}
	
	private LicenseModuleWrapper(){
		
	}
	
	@Override
	public synchronized int addLicenseKey(String key) {
		// bad key validation
		int valideKey = valideKey(key);
		if (valideKey == 3)
			return 3;
		key = key.trim();

		List<Integer> components=new ArrayList<Integer>();
		LicenseAPIReturnType result = BaseWSJNI.lookComponentsOfLicenseKey(key, components);
		if (result == LicenseAPIReturnType.ASE_SUCCESS) {
			if(components.size()!=1){
				System.err.println("license key map error");
				return -1;
			}
			result = BaseWSJNI.addLicenseKey(components.get(0), key);
		}
		if (result == null){
			System.err.println("addLicenseKey return null");
			return -1;
		}
		if (result == LicenseAPIReturnType.ASE_SUCCESS)
			return 0;
		else if (result == LicenseAPIReturnType.ASE_KEY_IN_USE)
			return 1;
		else if (result == LicenseAPIReturnType.ASE_BAD_KEY)
			return 3;
		else if (result == LicenseAPIReturnType.ASE_NO_MATCH)
			return 3;
		System.err.println("addLicenseKey return "+result);
		return -1;
	}
	
	private int valideKey(String key){
		if(key==null) return 3;
		key = key.trim();
		if(key.length()!=29) return 3;
		String[] split = key.split("-");
		if(split.length!=5) return 3;
		for(String keyPart : split){
			if(keyPart==null) return 3;
			keyPart = keyPart.trim();
			if(keyPart.length()!=5) return 3;
		}
		return 1;
	}

	@Override
	public int getTotalCount(String code){
		try {
			int total=getTotalLicenseCount(code);
			return total;
		} catch (lic98jException e) {
			logger.error(e);
			return 0;
		}
	}
	
	private int getTotalLicenseCount(String componentCode) throws lic98jException {
		 int rc = License.GetLicenseQuery(componentCode, lic98j.LIC_USER_COUNT );
	     if (rc == lic98j.LIC_AOK) {
	    	 caolffileexists=true;
	    	 int count = License.Query;
	    	 logger.debug("GetLicenseQuery SUCCESS: LIC_USER_COUNT: " + count );
	    	 return count;
	     }else {
	    	 if(isCaolfFileExists())
	    		 logger.error("GetLicenseQuery FAILED:  RC=" + rc); 		
	    	 return 0;
	     }
	}

	@Override
	public Date getInstallTime(String code) {
		try {
			int rc = License.GetLicenseQuery(code, lic98j.LIC_INSTALL_DATE);
			if (rc == lic98j.LIC_AOK) {
				int query_date = License.Query;
				logger.debug("GetLicenseQuery SUCCESS: LIC_USER_COUNT: "
						+ query_date);
				Date dt = new Date(query_date * 1000);
				return dt;
			} else {
				logger.error("GetLicenseQuery FAILED:  RC=" + rc);
				return null;
			}
		} catch (lic98jException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public LicenseStatus useLicense(String code, int count) {
		LicenseStatus result;
		try {
			int targetcount=getSpecialCount(code, count);
			int rc = License.Check(code, targetcount, lic98j.LIC_ERR_APP);
			logger.debug("Check license [ComponentCode "+code+" client_count "+count+"] return: "+rc);

			switch (rc) {
			case lic98j.LIC_AOK:
				result=LicenseStatus.VALID;
				break;
			case lic98j.LIC_CANT_OPEN:
			case lic98j.LIC_NO_LICENSE:    
				result=LicenseStatus.TRIAL;
				break;
			case lic98j.LIC_EXPIRED:
				result=LicenseStatus.EXPIRED;
				break;
			case lic98j.LIC_WILL_EXPIRE:
				result=LicenseStatus.WILL_EXPIRE;
				break;
			case lic98j.LIC_WG_COUNT:
				result=LicenseStatus.WG_COUNT;
				break;
			case lic98j.LIC_MACHINETYPE:
			case lic98j.LIC_TERMINATE:
				result=LicenseStatus.TERMINATE;
				break;
			default:
				result=LicenseStatus.ERROR;
			}
		} catch (lic98jException e) {
			logger.error(e);
			result=LicenseStatus.ERROR;
		}	
		return result;
	}

	@Override
	public LicenseStatus logLicense(String code, int count) {
		LicenseStatus result;
		try {
			int targetcount=getSpecialCount(code, count);
			int rc = License.Check(code, targetcount, lic98j.LIC_ERR_LIC);
			logger.debug("Check license [ComponentCode "+code+" client_count "+count+"] return: "+rc);
			
			switch (rc) {
			case lic98j.LIC_AOK:
				result=LicenseStatus.VALID;
				break;
			case lic98j.LIC_CANT_OPEN:
			case lic98j.LIC_NO_LICENSE:    
				result=LicenseStatus.TRIAL;
				break;
			case lic98j.LIC_EXPIRED:
				result=LicenseStatus.EXPIRED;
				break;
			case lic98j.LIC_WILL_EXPIRE:
				result=LicenseStatus.WILL_EXPIRE;
				break;
			case lic98j.LIC_WG_COUNT:
				result=LicenseStatus.WG_COUNT;
				break;
			case lic98j.LIC_MACHINETYPE:
			case lic98j.LIC_TERMINATE:
				result=LicenseStatus.TERMINATE;
				break;
			default:
				result=LicenseStatus.ERROR;
			}
		} catch (lic98jException e) {
			logger.error(e);
			result=LicenseStatus.ERROR;
		}	
		return result;
	}

	private static String[] MCs=new String[]{
		BundledLicense.UDPLIC_STANDARD_Managed_Capacity.getCode(),
		BundledLicense.UDPLIC_ADVANCED_Managed_Capacity.getCode(),
		BundledLicense.UDPLIC_PREMIUM_Managed_Capacity.getCode(),
		BundledLicense.UDPLIC_PREMIUM_Managed_Capacity_PLUS.getCode()
		};
	private int getSpecialCount(String code, int count) {
		// no need check count for MC licenses
		for(String c : MCs){
			if(c.equals(code))
				return 1;
		}
		return count;
	}

	@Override
	public boolean hasAnyLicense() {
		try{
			for(BundledLicense lic:BundledLicense.values()){
				if(BundledLicense.UDPLIC_TRIAL_LICENSE==lic ||
						BundledLicense.UDPLIC_ALP==lic)
					continue;
				if(getLicenseTrueCount(lic.getCode())>0)
					return true;
			}
		}catch(lic98jException e){
			return false;
		}
		return false;
	}
	
	
	private int getLicenseTrueCount(String componentCode) throws lic98jException {
		 int rc = License.GetLicenseQuery(componentCode, lic98j.LIC_USER_COUNT );
	     if (rc == lic98j.LIC_AOK) {
	    	 caolffileexists=true;
	    	 int count = License.Query;
	    	 logger.debug("GetLicenseQuery SUCCESS: LIC_COMP_INT: " + count );
	    	 return count;
	     }else {
	    	 if(isCaolfFileExists())
	    		 logger.error("GetLicenseQuery FAILED:  RC=" + rc); 		
	    	 return 0;
	     }
	}

	private boolean caolffileexists=true;
	private boolean isCaolfFileExists() {
		try{
			if(!caolffileexists)
				return false;
			if(License.Check("2UDP", 1, lic98j.LIC_ERR_APP)==lic98j.LIC_CANT_OPEN)
				caolffileexists=false;
			return caolffileexists;
		}catch(lic98jException e){
			return true;
		}
	}
}
