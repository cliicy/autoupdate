package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.arcserve.edge.util.FileSystemUtils;
import com.ca.arcserve.edge.app.base.common.udpapplication.GatewayApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImageDownloadInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImagesInfo;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry.PackageInfo;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductPackageRegistry.ProductPackageState;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadResults;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadResults.DownloadResult;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadResults.DownloadResult.DownloadResultValue;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadingTasks;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.download.DownloadingTasks.DownloadTask;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult.ErrorCategory;
import com.ca.arcserve.edge.webservice.jni.model.IHttpDownloadCallback;

public class ProductImageInsurer
{
	private static Logger logger = Logger.getLogger( ProductImageInsurer.class );
	private static ProductImageInsurer instance = new ProductImageInsurer();
	
	private ProductPackageRegistry packageRegistry = ProductPackageRegistry.load();
	private DownloadingTasks downloadingTasks;
	private DownloadResults downlondResults;
	private Object lockObject = new Object();
	
	@SuppressWarnings( "serial" )
	public static class GettingProductImageInfoException extends Exception
	{
		public GettingProductImageInfoException( Throwable cause )
		{
			super( cause );
		}
	}
	
	private static Map<DownloadResultValue, String> errorMsgMap = new HashMap<>();
	
	static
	{
		errorMsgMap.put( DownloadResultValue.InternalError,					"downloadDeployImage_InternalError" );
		errorMsgMap.put( DownloadResultValue.FailedToCreateDestFolder,		"downloadDeployImage_FailedToCreateDestFolder" );
		errorMsgMap.put( DownloadResultValue.FailedToConnect,				"downloadDeployImage_FailedToConnect" );
		errorMsgMap.put( DownloadResultValue.FailedToRequestData,			"downloadDeployImage_FailedToRequestData" );
		errorMsgMap.put( DownloadResultValue.FailedToRecieveData,			"downloadDeployImage_FailedToRecieveData" );
		errorMsgMap.put( DownloadResultValue.FailedToSaveData,				"downloadDeployImage_FailedToSaveData" );
		errorMsgMap.put( DownloadResultValue.FailedToDownloadMD5File,		"downloadDeployImage_FailedToDownloadMD5File" );
		errorMsgMap.put( DownloadResultValue.FailedToVerifyMD5,				"downloadDeployImage_FailedToVerifyMD5" );
		errorMsgMap.put( DownloadResultValue.FailedToUnpackData,			"downloadDeployImage_FailedToUnpackData" );
		errorMsgMap.put( DownloadResultValue.FailedToCheckSizeOrWrongSize,	"downloadDeployImage_FailedToCheckSizeOrWrongSize" );
	}
	
	private ProductImageInsurer()
	{
		this.downloadingTasks = new DownloadingTasks();
		this.downloadingTasks.save();
		
		this.downlondResults = new DownloadResults();
		this.downlondResults.save();
	}
	
	public static ProductImageInsurer getInstance()
	{
		return instance;
	}
	
	/**
	 * Try to download required product images. The function will return immediately
	 * if all require images are ready. Otherwise new downloads will be issued and
	 * a their URL will be put into the waiting list and return. Caller who requires
	 * those images should invoke waitForImagesReady() with the waiting list. That
	 * function will check the downloading status and return until all the required
	 * images are ready.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> downloadProductImages() throws Exception
	{
		String logPrefix = this.getClass().getSimpleName() + ".downloadProductImages(): ";
		logger.info( logPrefix + "Try to download required images." );
		
		ProductImagesInfo imagesInfo = null;
		List<String> taskWaitingList = new ArrayList<>();
		
		try
		{
			IProductDeployService deployService = EdgeFactory.getBean( IProductDeployService.class );
			imagesInfo = deployService.getProductImagesInfo();
		}
		catch (Exception e)
		{
			logger.error( "Error getting product images information.", e );
			throw new GettingProductImageInfoException( e );
		}
		
		String taskId = downloadFile( imagesInfo.getGmImageDownloadInfo(), ProductPackageType.GMPackage,
			imagesInfo.getConsoleVersionInfo() );
		if (taskId != null)
			taskWaitingList.add( taskId );
			
		if (parseInt( imagesInfo.getConsoleVersionInfo().getUpdateNumber() ) > 0) // has updates
		{
			taskId = downloadFile( imagesInfo.getUpdateImageDownloadInfo(), ProductPackageType.UpdatePackage,
				imagesInfo.getConsoleVersionInfo() );
			if (taskId != null)
				taskWaitingList.add( taskId );
		}
		
		return taskWaitingList;
	}
	
	private int parseInt( String string )
	{
		return (string == null) ? 0 : Integer.parseInt( string );
	}
	
	/**
	 * 
	 * @param	downloadInfo
	 * @return	ID of the download task, null if no need to download
	 */
	private String downloadFile(
		ProductImageDownloadInfo downloadInfo, ProductPackageType packageType, EdgeVersionInfo consoleVersion )
	{
		String logPrefix = this.getClass().getSimpleName() + ".downloadFile(): ";
		
		synchronized (lockObject)
		{
			EdgeSimpleVersion packageVersion = new EdgeSimpleVersion();
			packageVersion.setMajorVersion( consoleVersion.getMajorVersion() );
			packageVersion.setMinorVersion( consoleVersion.getMinorVersion() );
			packageVersion.setBuildNumber( consoleVersion.getBuildNumber() );
			if (packageType == ProductPackageType.UpdatePackage)
			{
				packageVersion.setUpdateNumber( parseInt( consoleVersion.getUpdateNumber() ) );
				packageVersion.setUpdateBuildNumber( parseInt( consoleVersion.getUpdateBuildNumber() ) );
			}
			
			PackageInfo packageInfo = this.packageRegistry.getPackageInfo( packageType );
			if ((packageInfo.getPackageVersion().compareTo( packageVersion ) == 0) &&
				(packageInfo.getPackageState() == ProductPackageState.Ready))
			{
				logger.info( logPrefix +
					"Package is already available in local, no need to download. Package type: " +
					packageType + ", Package version: " + packageVersion );
				return null;
			}
			
			DownloadTask task = this.downloadingTasks.findTask( packageType, packageVersion );
			if (task == null)
			{
				logger.info( logPrefix +
					"The package is NOT under downloading. Will submit a new download task. Package type: " +
					packageType + ", Package version: " + packageVersion );
				
				// clear current contents
				
				ProductPackageState newState = packageInfo.getPackageState();
				if (newState == ProductPackageState.Ready) // it should be the version problem, so need to be re-downloaded
					newState = ProductPackageState.Undownloaded;
				
				clearLocalRepository( packageType, newState );
				
				packageInfo.setPackageState( newState );
				packageInfo.getPackageVersion().copy( packageVersion );
				packageInfo.setStateTime( new Date() );
				packageRegistry.setPackageInfo( packageType, packageInfo );
				packageRegistry.save();
				
				// submit a task
				
				task = new DownloadTask();
				task.setTaskId( UUID.randomUUID().toString() );
				task.setPackageType( packageType );
				task.setPackageVersion( packageVersion );
				task.setCurrentState( newState );
				task.setUrl( downloadInfo.getDownloadUrl() );
				task.setSaveAs( downloadInfo.getLocalPath() );
				task.setMd5Url( downloadInfo.getMd5DownloadUrl() );
				task.setMd5SaveAs( downloadInfo.getMd5LocalPath() );
				task.setStartTime( new Date() );
				task.setTotalBytes( downloadInfo.getDataSize() );
				task.setDownloadedBytes( 0 );
				this.downloadingTasks.getTaskList().add( task );
				this.downloadingTasks.save();
				
				EdgeExecutors.getCachedPool().submit(
					new DownloadImagesRunnable( task, downloadingTasks, downlondResults, packageRegistry, lockObject ) );
				
				logger.info( logPrefix + "Package download task submitted. Task: " + task );
			}
			else
			{
				logger.info( logPrefix + "The package is under downloading. Task: " + task );
			}
			
			return task.getTaskId();
		}
	}
	
	private void clearLocalRepository( ProductPackageType packageType, ProductPackageState currentState )
	{
		String logPrefix = this.getClass().getSimpleName() + ".clearLocalRepository(): ";
		logger.info( logPrefix + "Clear local repository. Package type: " + packageType );
		
		if (packageType == ProductPackageType.GMPackage)
		{
			String consoleHome = CommonUtil.BaseEdgeInstallPath;
			String downloadedPath = FileSystemUtils.combinePath( consoleHome, "Deployment\\Agent" );
			String unpackedPath = FileSystemUtils.combinePath( consoleHome, "Deployment\\D2D" );
			String configIniPath = FileSystemUtils.combinePath( consoleHome, "Deployment\\D2D\\Config.ini" );
			
			if (currentState == ProductPackageState.Undownloaded)
			{
				FileSystemUtils.deleteDirectory( downloadedPath, null );
				logger.info( logPrefix + "Finish deleting directory: " + downloadedPath );
			}
	
			if ((currentState == ProductPackageState.Undownloaded) ||
				(currentState == ProductPackageState.NotUnpacked))
			{
				List<String> exceptionList = new ArrayList<>();
				exceptionList.add( configIniPath );
				FileSystemUtils.deleteDirectory( unpackedPath, exceptionList );
				logger.info( logPrefix + "Finish deleting directory: " + unpackedPath );
			}
		}
		else if (packageType == ProductPackageType.UpdatePackage)
		{
			String udpHome = CommonUtil.udpHome;
			String gmPackagePath = FileSystemUtils.combinePath( udpHome, "Update Manager\\FullUpdates" );
			String updatePackagePath = FileSystemUtils.combinePath( udpHome, "Update Manager\\EngineUpdates" );
			
			FileSystemUtils.deleteDirectory( gmPackagePath, null );
			logger.info( logPrefix + "Finish deleting directory: " + gmPackagePath );
			
			FileSystemUtils.deleteDirectory( updatePackagePath, null );
			logger.info( logPrefix + "Finish deleting directory: " + updatePackagePath );
		}
	}
	
//	private boolean isGMPackageAvailable()
//	{
//		String filePath = CommonUtil.BaseEdgeInstallPath;
//		if (!filePath.endsWith( "\\" ))
//			filePath += "\\";
//		filePath += "Deployment\\D2D\\Install\\MasterSetup.exe";
//		File file = new File( filePath );
//		return file.exists();
//	}
	
	public static class ImageDownloadException extends Exception
	{
		private static final long serialVersionUID = -1563539387908944823L;

		private DownloadResult result;
		
		public ImageDownloadException( DownloadResult result )
		{
			this.result = result;
		}

		public DownloadResult getResult()
		{
			return result;
		}
	}
	
	/**
	 * Wait for the specified images to become ready. This function will block
	 * the caller's thread and check the downloading status and will not return
	 * until all specified images are ready.
	 * 
	 * @param	taskIds
	 * 			The list of URLs of those images the caller wants to wait for.
	 */
	public void waitForImagesReady( List<String> taskIds ) throws ImageDownloadException
	{
		String logPrefix = this.getClass().getSimpleName() + ".waitForImagesReady(): ";
		logger.info( logPrefix + "Wait for all download tasks to complete. Tasks: " +
			Arrays.toString( taskIds.toArray() ) );
		
		List<String> watchTasks = new ArrayList<>();
		watchTasks.addAll( taskIds );
		
		for (;;)
		{
			synchronized (lockObject)
			{
				List<String> completions = new ArrayList<>();
				for (String taskId : watchTasks)
				{
					DownloadResult result = downlondResults.findResult( taskId );
					if (result != null)
					{
						logger.info( logPrefix + "One download task completed. Result: " + result );
						
						completions.add( taskId );
						if (result.getResult() != DownloadResultValue.Succeeded)
							throw new ImageDownloadException( result );
					}
				}
				watchTasks.removeAll( completions );
			}
			
			if (watchTasks.size() == 0)
			{
				logger.info( logPrefix + "All download tasks are completed" );
				break;
			}
			
			try { Thread.sleep( 5000 ); }
			catch (InterruptedException e) { break; }
		}
	}
	
	public static DownloadResultValue httpDownloadResultToResultValue( HttpDownloadResult result )
	{
		DownloadResultValue resultValue;
		switch (ErrorCategory.fromValue( result.getErrorCategory() ))
		{
		case BADPARAMETER:
		case OPENSESSION:
			resultValue = DownloadResultValue.InternalError;
			break;
			
		case CONNECT:
			resultValue = DownloadResultValue.FailedToConnect;
			break;
			
		case SENDREQUEST:
		case ERRORHTTPSTATUS:
			resultValue = DownloadResultValue.FailedToRequestData;
			break;
			
		case CREATEFILE:
			resultValue = DownloadResultValue.FailedToSaveData;
			break;
			
		case RECVDATA:
			resultValue = DownloadResultValue.FailedToRecieveData;
			break;
			
		case WRITEFILE:
			resultValue = DownloadResultValue.FailedToSaveData;
			break;
			
		case CHECKSIZE:
			resultValue = DownloadResultValue.FailedToCheckSizeOrWrongSize;
			break;
			
		default:
			resultValue = DownloadResultValue.InternalError;
			break;
		}
		
		return resultValue;
	}
	
	public static String getErrorMessage( DownloadResultValue downloadResult )
	{
		String key = errorMsgMap.get( downloadResult );
		if (key == null)
			key = "downloadDeployImage_InternalError";
		
		return EdgeCMWebServiceMessages.getMessage( key );
	}
	
	private static class DownloadImagesRunnable implements Runnable
	{
		private static NativeFacade nativeFacade = new NativeFacadeImpl();
		
		private DownloadTask task;
		private DownloadingTasks downloadingTasks;
		private DownloadResults downlondResults;
		private ProductPackageRegistry packageRegistry;
		private Object lockObject = new Object();
		
		public DownloadImagesRunnable( DownloadTask task,
			DownloadingTasks downloadingTasks, DownloadResults downlondResults,
			ProductPackageRegistry packageRegistry, Object lockObject )
		{
			this.task = task;
			this.downloadingTasks = downloadingTasks;
			this.downlondResults = downlondResults;
			this.packageRegistry = packageRegistry;
			this.lockObject = lockObject;
		}
		
		@Override
		public void run()
		{
			DownloadResultValue resultValue = DownloadResultValue.Succeeded;
			
			String logPrefix = this.getClass().getSimpleName() + ".run(): ";

			if (this.task == null)
			{
				logger.error( logPrefix + "Task is null, return immediately." );
				resultValue = DownloadResultValue.InternalError;
				return;
			}
			
			try
			{
				logger.info( logPrefix + "Begin to do download task. Task: " + this.task );
				
				if (this.task.getCurrentState() == ProductPackageState.Undownloaded)
				{
					// download
					
					logger.info( logPrefix + "Begin to download " + this.task.getUrl() );
					resultValue = this.downloadFile( this.task.getUrl(), this.task.getSaveAs(), true );
					if (resultValue != DownloadResultValue.Succeeded)
						return;
					
					String md5Url = this.task.getMd5Url();
					if ((md5Url != null) && !md5Url.trim().isEmpty())
					{
						logger.info( logPrefix + "Begin to download " + this.task.getMd5Url() );
						resultValue = this.downloadFile( this.task.getMd5Url(), this.task.getMd5SaveAs(), false );
						if (resultValue != DownloadResultValue.Succeeded)
						{
							resultValue = DownloadResultValue.FailedToDownloadMD5File;
							return;
						}
						
						// verify
						
						try
						{
							if (!verifyDownloadedFile( this.task ))
							{
								resultValue = DownloadResultValue.FailedToVerifyMD5;
								throw new Exception( "Contents of downloaded file is invalid." );
							}
						}
						catch (Exception e)
						{
							logger.error( logPrefix + "Error verifying downloaded package.", e );
							resultValue = DownloadResultValue.FailedToVerifyMD5;
							return;
						}
					}
				}
					
				packageRegistry.setPackageStateAndSave(
					this.task.getPackageType(), ProductPackageState.NotUnpacked );
				
				this.task.setCurrentState( ProductPackageState.NotUnpacked );
			
				// unpack
				
				if (this.task.getCurrentState() == ProductPackageState.NotUnpacked)
				{
					if (task.getPackageType() == ProductPackageType.GMPackage)
					{
						try
						{
							logger.info( logPrefix + "The package is a GM package, begin to unpack it." );
							unpackGMPackage( this.task );
							logger.info( logPrefix + "The GM package was unpacked successfully." );
						}
						catch (Exception e)
						{
							logger.error( logPrefix + "Error unpacking GM package.", e );
							resultValue = DownloadResultValue.FailedToUnpackData;
							return;
						}
					}
				}
				
				packageRegistry.setPackageStateAndSave(
					this.task.getPackageType(), ProductPackageState.Ready );
				
				this.task.setCurrentState( ProductPackageState.Ready );
			}
			finally
			{
				logger.info( logPrefix + "Write download control data." );
				
				synchronized (lockObject)
				{
					Date completionTime = new Date();
					
					downloadingTasks.removeTask( this.task.getTaskId() );
					downloadingTasks.save();
					
					DownloadResult result = new DownloadResult();
					result.setTaskId( this.task.getTaskId() );
					result.setPackageType( this.task.getPackageType() );
					result.setPackageVersion( this.task.getPackageVersion() );
					result.setUrl( this.task.getUrl() );
					result.setSaveAs( this.task.getSaveAs() );
					result.setStartTime( this.task.getStartTime() );
					result.setCompletionTime( completionTime );
					result.setTotalBytes( this.task.getTotalBytes() );
					result.setDownloadedBytes( this.task.getDownloadedBytes() );
					result.setResult( resultValue );
					downlondResults.getResultList().add( result );
					downlondResults.save();
				}
				
				logger.info( logPrefix + "Download task completed." );
			}
		}
		
		private DownloadResultValue downloadFile( String url, String saveAs, final boolean writeProgress )
		{
			String logPrefix = this.getClass().getSimpleName() + ".downloadFile(): ";
			
			GatewayApplication gatewayApp = (GatewayApplication) UDPApplication.getInstance();
			String host = gatewayApp.getMessageServiceSettings().getConsoleHost();
			int port = gatewayApp.getMessageServiceSettings().getConsolePort();
			String protocol = gatewayApp.getMessageServiceSettings().getConsoleProtocol();
			boolean isHttps = protocol.equalsIgnoreCase( "https" );
			
//			String udpHome = CommonUtil.BaseEdgeInstallPath;
//			String localPath = FileSystemUtils.combinePath( udpHome, saveAs );
			
			String localPath = ProductDeployServiceImpl.instantiateTemplate( saveAs );
			
			try
			{
				logger.info( logPrefix + "Create required folders for downloading. Local path: " + localPath );
				
				File localFile = new File( localPath );
				Files.createDirectories( Paths.get( localFile.getParent() ) );
			}
			catch (IOException e)
			{
				logger.error(
					logPrefix + "Error creating required folders for downloading. Local path: " + localPath, e );
				return DownloadResultValue.FailedToCreateDestFolder;
			}
			
			boolean bReturn = false;
			HttpDownloadResult result = new HttpDownloadResult();
			
			try
			{
				logger.info( logPrefix + "Invoke native API urlDownloadToFile() to do download." );
				
				bReturn = nativeFacade.httpDownload( host, port, isHttps, url, localPath,
					new IHttpDownloadCallback()
					{
						@Override
						public int onProgress( long totalDownloadedBytes )
						{
							if (!writeProgress)
								return 0;
							
							task.setDownloadedBytes( totalDownloadedBytes );;
							task.setLastUpdateTime( new Date() );
							
							synchronized (lockObject)
							{
								downloadingTasks.save();
							}
								
							return 0;
						}
					},
					result );
//				returnCode = nativeFacade.urlDownloadToFile(
//					downloadUrl, localPath, new IDownloadStatusCallback()
//					{
//						@Override
//						public void onProgress( long progress, long progressMax,
//							long status, String statusText )
//						{
//							if (!writeProgress)
//								return;
//							
//							task.setProgress( progress );
//							task.setProgressMax( progressMax );
//							task.setLastUpdateTime( new Date() );
//							
//							synchronized (lockObject)
//							{
//								downloadingTasks.save();
//							}
//						}
//					}
//				);
			}
			catch (Exception e)
			{
				logger.error(
					logPrefix + "Error invoking native API urlDownloadToFile() to do download.", e );
				return DownloadResultValue.InternalError;
			}
			
			if (!bReturn)
			{
				logger.error(
					logPrefix + "Native API urlDownloadToFile() failed. Return code: " + bReturn );
				return DownloadResultValue.InternalError;
			}
			
			if (result.getErrorCategory() != HttpDownloadResult.ErrorCategory.NOERROR.getValue())
			{
				logger.error(
					logPrefix + "Native API urlDownloadToFile() failed. Result: " + result );
				
				return ProductImageInsurer.httpDownloadResultToResultValue( result );
			}
			
			logger.info( logPrefix + "Downloading file succeed." );
			
			return DownloadResultValue.Succeeded;
		}
		
		private String generateMD5ForFile( String filePath ) throws NoSuchAlgorithmException, IOException
		{
			byte[] fileReadBuffer = new byte[512];
			int readBytes = 0;
			
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			
			FileInputStream stream = new FileInputStream( filePath );
			while ((readBytes = stream.read( fileReadBuffer )) != -1)
				md.update( fileReadBuffer, 0, readBytes );
			stream.close();
			
			byte[] mdBytes = md.digest();
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mdBytes.length; i ++)
			{
				String hexStr = String.format( "%02x", mdBytes[i] );
				sb.append( hexStr );
			}
			
			return sb.toString();
		}
		
		private boolean verifyDownloadedFile( DownloadTask task ) throws NoSuchAlgorithmException, IOException
		{
			String logPrefix = this.getClass().getSimpleName() + ".verifyDownloadedFile(): ";
			
			logger.info( logPrefix + "Begin to verify downloaded file." );
			
//			String udpHome = CommonUtil.BaseEdgeInstallPath;
//			String packagePath = FileSystemUtils.combinePath( udpHome, task.getSaveAs() );
//			String md5FilePath = FileSystemUtils.combinePath( udpHome, task.getMd5SaveAs() );
			
			String packagePath = ProductDeployServiceImpl.instantiateTemplate( task.getSaveAs() );
			String md5FilePath = ProductDeployServiceImpl.instantiateTemplate( task.getMd5SaveAs() );
			
			String md5Str = generateMD5ForFile( packagePath );
			
			BufferedReader reader = new BufferedReader( new FileReader( md5FilePath ) );
			String md5FileContent = reader.readLine();
			reader.close();
			
			if (!md5FileContent.equals( md5Str ))
			{
				logger.error( logPrefix +
					"MD5 of downloaded file is not identical to the MD5 of the original file. File: " + task.getUrl() +
					", Original MD5: " + md5FileContent + ", MD5 of downloaded file: " + md5Str );
				return false;
			}
			
			logger.info( logPrefix + "Downloaded file verified." );
			
			return true;
		}
		
		private void unpackGMPackage( DownloadTask task ) throws Exception
		{
			String udpHome = CommonUtil.BaseEdgeInstallPath;
			String unpackToolPath = FileSystemUtils.combinePath( udpHome, "Setup\\asz.exe" );
			String unpackTargetPath = FileSystemUtils.combinePath( udpHome, "Deployment" );
			String packagePath = ProductDeployServiceImpl.instantiateTemplate( task.getSaveAs() );
			String md5Path = ProductDeployServiceImpl.instantiateTemplate( task.getMd5SaveAs() );
			String workingDir = FileSystemUtils.combinePath( udpHome, "Deployment" );
			
			String[] cmdArray = new String[] {
				unpackToolPath,
				"-u",
				"-Source:" + packagePath,
				"-Target:" + unpackTargetPath,
				};
			Process process = Runtime.getRuntime().exec( cmdArray, null, new File( workingDir ) );
			int returnValue = process.waitFor();
			if (returnValue != 0)
				throw new Exception( "Error unpack GM package. Return value: " +
					returnValue + ", Command: " + Arrays.toString( cmdArray ) );
			
			Files.delete( (new File( packagePath )).toPath() );
			Files.delete( (new File( md5Path )).toPath() );
			
			Date now = new Date();
			File packageFile = new File( packagePath );
			PrintWriter writer = new PrintWriter( FileSystemUtils.combinePath( packageFile.getParent(), "File_unpacked.txt" ) );
			writer.println( "File unpacked at " + now );
			writer.close();
		}
	}
}
