package com.ca.arcserve.edge.app.base.webservice.productdeploy.download;

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
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry.ProductPackageState;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageType;

@XmlRootElement( name = "DownloadingTasks" )
@XmlAccessorType( XmlAccessType.FIELD )
public class DownloadingTasks
{
//	@XmlType( propOrder = {
//		"taskId",
//		"url",
//		"saveAs",
//		"md5Url",
//		"md5SaveAs",
//		"isGMPacakge",
//		"startTime",
//		"totalBytes",
//		"downloadedBytes",
//		"lastUpdateTime"
//	} )
	public static class DownloadTask
	{
		private String taskId;
		private ProductPackageType packageType;
		private EdgeSimpleVersion packageVersion;
		private ProductPackageState currentState;
		private String url;
		private String saveAs;
		private String md5Url;
		private String md5SaveAs;
		private Date startTime;
		private long totalBytes;
		private long downloadedBytes;
		private Date lastUpdateTime;

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

		public ProductPackageState getCurrentState()
		{
			return currentState;
		}

		public void setCurrentState( ProductPackageState currentState )
		{
			this.currentState = currentState;
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

		public String getMd5Url()
		{
			return md5Url;
		}

		public void setMd5Url( String md5Url )
		{
			this.md5Url = md5Url;
		}

		public String getMd5SaveAs()
		{
			return md5SaveAs;
		}

		public void setMd5SaveAs( String md5SaveAs )
		{
			this.md5SaveAs = md5SaveAs;
		}

		public Date getStartTime()
		{
			return startTime;
		}

		public void setStartTime( Date startTime )
		{
			this.startTime = startTime;
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

		public Date getLastUpdateTime()
		{
			return lastUpdateTime;
		}

		public void setLastUpdateTime( Date lastUpdateTime )
		{
			this.lastUpdateTime = lastUpdateTime;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append( this.getClass().getSimpleName() + " { " );
			sb.append( "taskId = " + taskId );
			sb.append( ", packageVersion = " + packageVersion );
			sb.append( ", url = '" + url + "'" );
			sb.append( ", saveAs = '" + saveAs + "'" );
			sb.append( ", packageType = " + packageType );
			sb.append( ", startTime = " + startTime );
			sb.append( ", totalBytes = " + totalBytes );
			sb.append( ", downloadedBytes = " + downloadedBytes );
			sb.append( ", lastUpdateTime = " + lastUpdateTime );
			sb.append( " }" );
			return sb.toString();
		}

	}

	private static Logger logger = Logger.getLogger( DownloadingTasks.class );
	private List<DownloadTask> taskList = new LinkedList<>();
	
	public DownloadingTasks()
	{
	}

	public List<DownloadTask> getTaskList()
	{
		return taskList;
	}
	
	public DownloadTask findTask( String url )
	{
		if (url == null)
			return null;
		
		for (DownloadTask task : taskList)
		{
			if (task.getUrl().equals( url ))
				return task;
		}
		
		return null;
	}
	
	public DownloadTask findTask( ProductPackageType packageType, EdgeSimpleVersion packageVersion )
	{
		assert packageType != null : "packageType cannot be null";
		if (packageType == null)
			throw new IllegalArgumentException( "packageType is null" );
		
		assert packageVersion != null : "packageVersion cannot be null";
		if (packageVersion == null)
			throw new IllegalArgumentException( "packageVersion is null" );
		
		for (DownloadTask task : taskList)
		{
			if (task.getPackageType().equals( packageType ) &&
				(task.getPackageVersion().compareTo( packageVersion ) == 0))
				return task;
		}
		
		return null;
	}
	
	public void removeTask( String taskId )
	{
		if (taskId == null)
			return;
		
		DownloadTask toBeRemoved = null;
		
		for (DownloadTask task : taskList)
		{
			if (task.getTaskId().equals( taskId ))
			{
				toBeRemoved = task;
				break;
			}
		}
		
		if (toBeRemoved != null)
			taskList.remove( toBeRemoved );
	}
	
	public static String FILENAME = "DownloadingTasks.xml";
	
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
			logger.error( "DownloadingTasks.save(): Error saving DownloadingTasks.", t );
		}
	}
}
