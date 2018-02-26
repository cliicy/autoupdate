package com.ca.arcserve.edge.app.base.webservice.productdeploy.download;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.CommonUtil;

@XmlRootElement( name = "DownloadedImages" )
@XmlAccessorType( XmlAccessType.FIELD )
public class DownloadedImages
{
	private static Logger logger = Logger.getLogger( DownloadedImages.class );
	
	public static class DownloadedResource
	{
		private String url;
		private Date downloadTime;
		private boolean isSuccessful;
		
		public String getUrl()
		{
			return url;
		}
		
		public void setUrl( String url )
		{
			this.url = url;
		}
		
		public Date getDownloadTime()
		{
			return downloadTime;
		}
		
		public void setDownloadTime( Date downloadTime )
		{
			this.downloadTime = downloadTime;
		}

		public boolean isSuccessful()
		{
			return isSuccessful;
		}

		public void setSuccessful( boolean isSuccessful )
		{
			this.isSuccessful = isSuccessful;
		}
	}
	
	private List<DownloadedResource> images = new ArrayList<>();
	
	public DownloadedImages()
	{
	}
	
	public List<DownloadedResource> getImages()
	{
		return images;
	}
	
	public boolean containsImage( String url )
	{
		for (DownloadedResource resource : images)
		{
			if (resource.getUrl().equals( url ))
				return true;
		}
		
		return false;
	}

	public static String FILENAME = "DownloadedImages.xml";
	
	public static String getFilePath()
	{
		String folderPath = CommonUtil.BaseEdgeInstallPath;
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		folderPath += "Deployment\\";
		return folderPath + FILENAME;
	}
	
	public static DownloadedImages load()
	{
		return load( getFilePath() );
	}
	
	private static DownloadedImages load( String filePath )
	{
		DownloadedImages downloadImages = null;
		
		File file = new File( filePath );
		if (file.exists())
		{
			try
			{
				downloadImages = JAXB.unmarshal( file, DownloadedImages.class );
			}
			catch (Exception e)
			{
				logger.error( "Error loading DownloadedImages file.", e );
			}
		}
		
		if (downloadImages == null)
			downloadImages = new DownloadedImages();
		
		return downloadImages;
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
