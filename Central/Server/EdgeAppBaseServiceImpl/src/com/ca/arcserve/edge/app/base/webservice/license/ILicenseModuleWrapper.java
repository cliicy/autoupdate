package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.Date;

import com.ca.arcflash.webservice.edge.license.LicenseStatus;

public interface ILicenseModuleWrapper {

	public int addLicenseKey(String key);

	public int getTotalCount(String code);

	public Date getInstallTime(String code);

	public LicenseStatus useLicense(String code, int count);

	public boolean hasAnyLicense();

	public LicenseStatus logLicense(String code, int count);
}