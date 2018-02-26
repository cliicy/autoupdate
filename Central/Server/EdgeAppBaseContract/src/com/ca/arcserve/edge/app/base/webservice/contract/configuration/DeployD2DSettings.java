package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class DeployD2DSettings implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_PORT = 8014;
	public static final String DEAFULT_INSTALL_PATH_D2D = "%ProgramFiles%";
	public static final String INSTALL_SHOW_PATH_D2D = "\\Arcserve\\Unified Data Protection"; ///issue 86167; not show engine in UI;
	public static final String INSTALL_Inner_PATH_D2D = "\\Engine";    ///issue 86167; not show engine in UI;
	public static final String INSTALL_PATH_D2D =  INSTALL_SHOW_PATH_D2D + INSTALL_Inner_PATH_D2D;
	public static final String DEFAULT_INSTALL_PATH_D2DOD = "%ProgramFiles%";
	public static final String INSTALL_PATH_D2DOD = "\\CA\\ARCserve D2D On Demand";
	
	private int productType =Integer.parseInt(ProductType.ProductD2D);
	private int port;
	private String installPath;
	private boolean allowInstallDriver;
	private RebootType rebootType;
	private Protocol protocol;
	private String deployUserName ="";
	private String deployPassword ="";
	private Date rebootTime;
	
	public static DeployD2DSettings getDefaultSettings()
	{
		DeployD2DSettings settings = new DeployD2DSettings();
		settings.setPort( DEFAULT_PORT );
		settings.setProductType(Integer.parseInt(ProductType.ProductD2D));
		settings.setInstallPath( DEAFULT_INSTALL_PATH_D2D + INSTALL_SHOW_PATH_D2D );
		settings.setAllowInstallDriver( true );
		settings.setProtocol(Protocol.Https);
		//settings.setRebootType(RebootType.RebootAtOnce);
		//settings.setRebootTime(new Date());
		return settings;
	}

	public int getPort()
	{
		return port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	public String getInstallPath()
	{
		return installPath;
	}
	
	public void setInstallPath( String installPath )
	{
		this.installPath = installPath;
	}
	
	public boolean isAllowInstallDriver()
	{
		return allowInstallDriver;
	}
	
	public void setAllowInstallDriver( boolean allowInstallDriver )
	{
		this.allowInstallDriver = allowInstallDriver;
	}
	
	public RebootType getRebootType() {
		return rebootType;
	}

	public void setRebootType(RebootType rebootType) {
		this.rebootType = rebootType;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Date getRebootTime() {
		return rebootTime;
	}

	public void setRebootTime(Date rebootTime) {
		this.rebootTime = rebootTime;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}
	
	public String getDeployUserName() {
		return deployUserName;
	}

	public void setDeployUserName(String deployUserName) {
		this.deployUserName = deployUserName;
	}
	public String getDeployPassword() {
		return deployPassword;
	}

	public void setDeployPassword(String deployPassword) {
		this.deployPassword = deployPassword;
	}
	
	public String getInstallPathWithoutProductName(int productType) {
		return getInstallPathWithoutProductName(installPath, productType);
	}

	public String getInstallPathWithProductName(int productType) {
		return getInstallPathWithProductName(installPath, productType);
	}

	public static String getInstallPathWithoutProductName(String installPath, int productType) {
		String productName = productType == ProductType.D2DOD ? INSTALL_PATH_D2DOD : INSTALL_PATH_D2D;
		if (installPath != null) {
			String localInstallPath = installPath.trim();
			if (localInstallPath.endsWith(productName)) {
				int index = localInstallPath.lastIndexOf(productName);
				return localInstallPath.substring(0, index);
			}
			return localInstallPath;
		}
		return installPath;
	}

	public static String getInstallPathWithProductName(String installPath, int productType) {
//		String productName = productType == ProductType.D2DOD ? INSTALL_PATH_D2DOD : INSTALL_PATH_D2D;
//		if (installPath != null) {
//			String localInstallPath = installPath.trim();
//			if (!localInstallPath.endsWith(productName)) {
//				int index = localInstallPath.length();
//				if (localInstallPath.endsWith("\\")) {
//					index--;
//				}
//				return localInstallPath.substring(0, index) + productName;
//			}
//			return localInstallPath;
//		}
		
		// just return the original path that user set, since we don't need to change path to support D2DOD product. 
		return installPath;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeployD2DSettings other = (DeployD2DSettings) obj;
		return ((other.getPort() == this.getPort()) &&
				(simpleObjectEquals(other.getInstallPath(),this.getInstallPath() )) &&
				(other.isAllowInstallDriver() == this.isAllowInstallDriver()) &&
				//(simpleObjectEquals(other.getRebootType(),this.getRebootType()))&&
				//(other.getRebootTime().compareTo(this.getRebootTime())==0)&&
				(simpleObjectEquals(other.getProtocol(),this.getProtocol()))&&
				(other.getProductType() == this.getProductType())&&
				(simpleObjectEquals(other.getDeployUserName(),this.getDeployUserName()) )&&
				(simpleObjectEquals(other.getDeployPassword(),this.getDeployPassword()))
				);
		
	}
	
	private boolean simpleObjectEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
}
