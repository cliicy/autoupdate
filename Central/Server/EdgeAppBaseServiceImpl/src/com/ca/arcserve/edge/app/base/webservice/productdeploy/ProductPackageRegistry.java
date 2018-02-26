package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;

@XmlRootElement( name = "ProductPackageRegistry" )
@XmlAccessorType( XmlAccessType.FIELD )
public class ProductPackageRegistry
{
	public enum ProductPackageState
	{
		Undownloaded,
		NotUnpacked,
		Ready,
	}
	
	private static class RegistryEntry
	{
		private ProductPackageType packageType = ProductPackageType.GMPackage;
		private ProductPackageState packageState = ProductPackageState.Undownloaded;
		private EdgeSimpleVersion packageVersion = new EdgeSimpleVersion();
		private Date stateTime;

		public ProductPackageType getPackageType()
		{
			return packageType;
		}

		public void setPackageType( ProductPackageType packageType )
		{
			this.packageType = packageType;
		}

		public ProductPackageState getPackageState()
		{
			return packageState;
		}

		public void setPackageState( ProductPackageState packageState )
		{
			if (packageState == null)
				packageState = ProductPackageState.Undownloaded;
			
			this.packageState = packageState;
		}

		public EdgeSimpleVersion getPackageVersion()
		{
			return packageVersion;
		}

		public void setPackageVersion( EdgeSimpleVersion packageVersion )
		{
			if (packageVersion == null)
				packageVersion = new EdgeSimpleVersion();
			
			this.packageVersion = packageVersion;
		}

		public Date getStateTime()
		{
			return stateTime;
		}

		public void setStateTime( Date stateTime )
		{
			this.stateTime = stateTime;
		}
	}
	
	public static class PackageInfo
	{
		private ProductPackageState packageState = ProductPackageState.Undownloaded;
		private EdgeSimpleVersion packageVersion = new EdgeSimpleVersion();
		private Date stateTime = null;
		
		public ProductPackageState getPackageState()
		{
			return packageState;
		}
		
		public void setPackageState( ProductPackageState packageState )
		{
			assert packageState != null : "packageState cannot be null";
			if (packageState == null)
				throw new IllegalArgumentException( "packageState is null" );
			
			this.packageState = packageState;
		}
		
		public EdgeSimpleVersion getPackageVersion()
		{
			return packageVersion;
		}

		public Date getStateTime()
		{
			return stateTime;
		}
		
		public void setStateTime( Date stateTime )
		{
			this.stateTime = stateTime;
		}
	}
	
	private static Logger logger = Logger.getLogger( ProductPackageRegistry.class );
	
	private List<RegistryEntry> entryList = new LinkedList<>();
	
	public ProductPackageRegistry()
	{
	}
	
	private RegistryEntry findEntry( ProductPackageType packageType )
	{
		for (RegistryEntry entry : entryList)
		{
			if (entry.getPackageType() == packageType)
				return entry;
		}
		
		return null;
	}
	
	public void setPackageInfo( ProductPackageType packageType, PackageInfo packageInfo )
	{
		assert packageType != null : "packageType cannot be null";
		if (packageType == null)
			throw new IllegalArgumentException( "packageType is null" );
		
		assert packageInfo != null : "packageInfo cannot be null";
		if (packageInfo == null)
			throw new IllegalArgumentException( "statusInfo is null" );
		
		RegistryEntry entry = findEntry( packageType );
		if (entry == null)
		{
			entry = new RegistryEntry();
			entry.setPackageType( packageType );
			entryList.add( entry );
		}
		
		entry.setPackageState( packageInfo.getPackageState() );
		entry.getPackageVersion().copy( packageInfo.getPackageVersion() );
		entry.setStateTime( (packageInfo.getStateTime() == null) ? null : (Date) packageInfo.getStateTime().clone() );
	}
	
	public PackageInfo getPackageInfo( ProductPackageType packageType )
	{
		assert packageType != null : "packageType cannot be null";
		if (packageType == null)
			throw new IllegalArgumentException( "packageType is null" );
		
		RegistryEntry entry = findEntry( packageType );
		if (entry == null)
		{
			entry = new RegistryEntry();
			entry.setPackageType( packageType );
		}
		
		PackageInfo packageInfo = new PackageInfo();
		packageInfo.setPackageState( entry.getPackageState() );
		packageInfo.getPackageVersion().copy( entry.getPackageVersion() );
		packageInfo.setStateTime( (entry.getStateTime() == null) ? null : (Date) entry.getStateTime().clone() );
		
		return packageInfo;
	}
	
	public void setPackageStateAndSave( ProductPackageType packageType, ProductPackageState state )
	{
		assert packageType != null : "packageType cannot be null";
		if (packageType == null)
			throw new IllegalArgumentException( "packageType is null" );
		
		assert state != null : "state cannot be null";
		if (state == null)
			throw new IllegalArgumentException( "state is null" );
		
		RegistryEntry entry = findEntry( packageType );
		if (entry == null)
		{
			entry = new RegistryEntry();
			entry.setPackageType( packageType );
			entryList.add( entry );
		}
		
		entry.setPackageState( state );
		entry.setStateTime( new Date() );
		
		this.save();
	}

	public static String FILENAME = "DeploymentPackageRegistry.xml";
	
	public static String getFilePath()
	{
		String folderPath = CommonUtil.BaseEdgeInstallPath;
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		folderPath += "Deployment\\";
		return folderPath + FILENAME;
	}
	
	public static ProductPackageRegistry load()
	{
		return load( getFilePath() );
	}
	
	private static ProductPackageRegistry load( String filePath )
	{
		ProductPackageRegistry packageStatus = null;
		
		File file = new File( filePath );
		if (file.exists())
		{
			try
			{
				packageStatus = JAXB.unmarshal( file, ProductPackageRegistry.class );
			}
			catch (Exception e)
			{
				logger.error( "Error loading DownloadedImages file.", e );
			}
		}
		
		if (packageStatus == null)
			packageStatus = new ProductPackageRegistry();
		
		return packageStatus;
	}
	
	public void save()
	{
		save( getFilePath() );
	}
	
	private void save( String filePath )
	{
		JAXB.marshal( this, new File( filePath ) );
	}
}
