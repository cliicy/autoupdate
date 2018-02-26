package com.ca.arcserve.edge.app.base.webservice.contract.license.bundled;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * The information of a license that is used by UDP.
 */
public class LicenseInformation implements Serializable,BeanModelTag{
	private static final long serialVersionUID = 368231953130423388L;

	private BundledLicense license;
	private Date installTime;
	private long total;
	private long used;
	private int needed;
	
	/**
	 * Get the license UDP is using.
	 * 
	 * @return
	 */
	public BundledLicense getLicense() {
		return license;
	}
	
	/**
	 * Set the license UDP is using.
	 * 
	 * @param license
	 */
	public void setLicense(BundledLicense license) {
		this.license = license;
	}
	
	/**
	 * Get the code of the license.
	 * 
	 * @return
	 */
	public String getCode() {
		return license.getCode();
	}
	
	/**
	 * Get the name of the license.
	 * 
	 * @return
	 */
	public String getLicenseName() {
		return license.getDisplayName();
	}
	
	/**
	 * Get the code of the license. Refer to {@link com.ca.arcflash.webservice.edge.license.LicenseDef}
	 * for values.
	 * 
	 * @return
	 */
	public long getFeatures() {
		return license.getFeature();
	}
	
	/**
	 * Get input time of the license.
	 * 
	 * @return
	 */
	public Date getInstallTime() {
		return installTime;
	}
	
	/**
	 * Set input time of the license.
	 * 
	 * @param installTime
	 */
	public void setInstallTime(Date installTime) {
		this.installTime = installTime;
	}
	
	/**
	 * Get total capacity of the license.
	 * 
	 * @return
	 */
	public long getTotal() {
		return total;
	}
	
	/**
	 * Set total capacity of the license.
	 * 
	 * @param total
	 */
	public void setTotal(long total) {
		this.total = total;
	}
	
	/**
	 * Get available capacity of the license.
	 * 
	 * @return
	 */
	public long getAvailable() {
		return total-used;
	}
	
	/**
	 * Get used capacity of the license.
	 * 
	 * @return
	 */
	public long getUsed() {
		return used;
	}
	
	/**
	 * Set used capacity of the license.
	 * 
	 * @param used
	 */
	public void setUsed(long used) {
		this.used = used;
	}
	
	/**
	 * Get needed capacity of the license.
	 * 
	 * @return
	 */
	public int getNeeded() {
		return needed;
	}
	
	/**
	 * Set needed capacity of the license.
	 * 
	 * @param needed
	 */
	public void setNeeded(int needed) {
		this.needed = needed;
	}
}
