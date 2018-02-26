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
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageType;

@XmlRootElement( name = "DownloadResults" )
@XmlAccessorType( XmlAccessType.FIELD )
public class DownloadResults
{
//	@XmlType( propOrder = {
//		"taskId",
//		"url",
//		"saveAs",
//		"startTime",
//		"completionTime",
//		"totalBytes",
//		"downloadedBytes",
//		"result",
//		"errorCode"
//	} )
	public static class DownloadResult
	{
		public enum DownloadResultValue
		{
			Succeeded,
			InternalError,
			FailedToCreateDestFolder,
			FailedToConnect,
			FailedToRequestData,
			FailedToRecieveData,
			FailedToSaveData,
			FailedToDownloadMD5File,
			FailedToVerifyMD5,
			FailedToUnpackData,
			FailedToCheckSizeOrWrongSize,
		}
		
		private String taskId;
		private ProductPackageType packageType;
		private EdgeSimpleVersion packageVersion;
		private String url;
		private String saveAs;
		private Date startTime;
		private Date completionTime;
		private long totalBytes;
		private long downloadedBytes;
		private DownloadResultValue result;
		private int errorCode;

		public String getTaskId()
		{
			return taskId;
		}

		public void setTaskId( String taskId )
		{
			this.taskId = taskId;
		}

		public ProductPackageType getPackageType()
		{
			return packageType;
		}

		public void setPackageType( ProductPackageType packageType )
		{
			this.packageType = packageType;
		}

		public EdgeSimpleVersion getPackageVersion()
		{
			return packageVersion;
		}

		public void setPackageVersion( EdgeSimpleVersion packageVersion )
		{
			this.packageVersion = packageVersion;
		}

		public String getUrl()
		{
			return url;
		}

		public void setUrl( String url )
		{
			this.url = url;
		}

		public String getSaveAs()
		{
			return saveAs;
		}

		public void setSaveAs( String saveAs )
		{
			this.saveAs = saveAs;
		}

		public Date getStartTime()
		{
			return startTime;
		}

		public void setStartTime( Date startTime )
		{
			this.startTime = startTime;
		}

		public Date getCompletionTime()
		{
			return completionTime;
		}

		public void setCompletionTime( Date completionTime )
		{
			this.completionTime = completionTime;
		}

		public long getTotalBytes()
		{
			return totalBytes;
		}

		public void setTotalBytes( long totalBytes )
		{
			this.totalBytes = totalBytes;
		}

		public long getDownloadedBytes()
		{
			return downloadedBytes;
		}

		public void setDownloadedBytes( long downloadedBytes )
		{
			this.downloadedBytes = downloadedBytes;
		}

		public DownloadResultValue getResult()
		{
			return result;
		}

		public void setResult( DownloadResultValue result )
		{
			this.result = result;
		}

		public int getErrorCode()
		{
			return errorCode;
		}

		public void setErrorCode( int errorCode )
		{
			this.errorCode = errorCode;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append( this.getClass().getSimpleName() + " { " );
			sb.append( "taskId = " + taskId );
			sb.append( ", url = '" + url + "'" );
			sb.append( ", saveAs = '" + saveAs + "'" );
			sb.append( ", startTime = " + startTime );
			sb.append( ", completionTime = " + completionTime );
			sb.append( ", totalBytes = " + totalBytes );
			sb.append( ", downloadedBytes = " + downloadedBytes );
			sb.append( ", result = " + result );
			sb.append( ", errorCode = " + errorCode );
			sb.append( " }" );
			return sb.toString();
		}
	}
	
	private static Logger logger = Logger.getLogger( DownloadResults.class );
	private List<DownloadResult> resultList = new ArrayList<>();
	
	public DownloadResults()
	{
	}

	public List<DownloadResult> getResultList()
	{
		return resultList;
	}
	
	public DownloadResult findResult( String taskId )
	{
		if (taskId == null)
			return null;
		
		for (DownloadResult result : resultList)
		{
			if (result.getTaskId().equals( taskId ))
				return result;
		}
		
		return null;
	}

	public static String FILENAME = "DownloadResults.xml";
	
	public static String getFilePath()
	{
		String folderPath = CommonUtil.BaseEdgeInstallPath;
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		folderPath += "Deployment\\";
		return folderPath + FILENAME;
	}
	
	public void save()
	{
		save( getFilePath() );
	}
	
	private void save( String filePath )
	{
		try
		{
			JAXB.marshal( this, new File( filePath ) );
		}
		catch (Throwable t)
		{
			logger.error( "DownloadResults.save(): Error saving DownloadingTasks.", t );
		}
	}
}
