package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IRemoteProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DeployD2DSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.DeployStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductDeployPhase;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductImageInsurer.GettingProductImageInfoException;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductImageInsurer.ImageDownloadException;
import com.ca.arcserve.edge.webservice.jni.model.DeployD2DConstants;

public class RemoteProductDeployServiceImpl implements IRemoteProductDeployService{

	public final String DEPLOY_FOLDER = EdgeCommonUtil.EdgeInstallPath + "deployment\\";
	public final String DEPLOY_CONFIG_FOLDER = DEPLOY_FOLDER + "RemoteDeploy";
	
	private static final String INI_FILE_ENCODING = "UTF-16LE";
	private static final String STRING_NO = "No";
	private static final String STRING_YES = "Yes";
	
	private static final Logger logger = Logger.getLogger( RemoteProductDeployServiceImpl.class );
	private NativeFacade nativeFacade = new NativeFacadeImpl();
	private Map<String, Process> processCache = new HashMap<String, Process>();
	
	@Override
	public void startDeployProcess(DeployTargetDetail target)
			throws EdgeServiceFault {
		
		try {
			
			//1 create ini file
			generateINIFile(target);
			
			//2 start deployment thread
			EdgeExecutors.getCachedPool().submit( new DeploymentRunnable( target ) );
			
		}
		catch (Exception e)
		{
			logger.error("[RemoteProductDeployServiceImpl]: Start remote deploy", e);
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.REMOTEDEPLOY_FailedToStartDeployProcess,
				"Error starting deploy process." );
		}
	}
	
	private class DeploymentRunnable implements Runnable
	{
		private DeployTargetDetail target;
		
		public DeploymentRunnable( DeployTargetDetail target )
		{
			this.target = target;
		}
		
		@Override
		public void run()
		{
			try {
				
				//2 download product images from console
				downloadProductImages( target );
				
				//3 Lunch deploy.exe
				String confFilePath = getDeployFilePath(target.getServerName());
				Process process = launchDeployProcess(confFilePath);
				
				//4 cache process
				processCache.put(target.getServerName(), process);
				
			} catch (Exception e) {
				logger.error("[RemoteProductDeployServiceImpl]: Start remote deploy", e);
			}
		}
	}
	
	private void downloadProductImages( DeployTargetDetail target ) throws Exception
	{
		String logPrefix = this.getClass().getSimpleName() + ".downloadProductImages(): ";
		
		try
		{
			writeDeployStatus( target, DeployStatus.DEPLOY_DOWNLOADING_IMAGE, "" );
			
			List<String> waitingList = ProductImageInsurer.getInstance().downloadProductImages();
			if (waitingList.size() == 0)
			{
				logger.info( logPrefix + "No download tasks need to be wait, go to next step directly." );
			}
			else // has download tasks to wait
			{
				logger.info( logPrefix + "Wait for download tasks to be finished ..." );
				ProductImageInsurer.getInstance().waitForImagesReady( waitingList );
				logger.info( logPrefix + "All download tasks finished." );
			}
			
			writeDeployStatus( target, DeployStatus.DEPLOY_PREPARATION_DONE, "" );
		}
		catch (GettingProductImageInfoException e)
		{
			logger.error( logPrefix + " to get product image information from console.", e );
			String errorMessage = EdgeCMWebServiceMessages.getMessage( "downloadDeployImage_ErrorGettingProductImagesInfo" );
			writeDeployStatus( target, DeployStatus.DEPLOY_DOWNLOADING_IMAGE_FAILED, errorMessage );

			throw e;
		}
		catch (ImageDownloadException e)
		{
			logger.error( logPrefix + "Error downloading product images from console. Result: " + e.getResult().getResult(), e );
			String errorMessage = ProductImageInsurer.getErrorMessage( e.getResult().getResult() );
			writeDeployStatus( target, DeployStatus.DEPLOY_DOWNLOADING_IMAGE_FAILED, errorMessage );

			throw e;
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error downloading product images from console.", e );
			String errorMessage = EdgeCMWebServiceMessages.getMessage( "downloadDeployImage_InternalError" );
			writeDeployStatus( target, DeployStatus.DEPLOY_DOWNLOADING_IMAGE_FAILED, errorMessage );

			throw e;
		}
	}
	
	private void writeDeployStatus( DeployTargetDetail target, DeployStatus status, String errorMessage )
	{
		try
		{
			target.setStatus( status.value() );
			target.setProgressMessage( errorMessage );
			generateINIFile( target );
		}
		catch (Exception e)
		{
			logger.error(
				"Error writing deploy status. Node ID: " + target.getNodeID() + ", Status: " + status, e );
		}
	}

	@Override
	public DeployStatusInfo getDeployStatus(DeployTargetDetail target)
			throws EdgeServiceFault {
		DeployStatusInfo info = new DeployStatusInfo();
		DeployStatus deployStatus = DeployStatus.DEPLOY_NA;
		
		try {
			String filePath = getDeployFilePath(target.getServerName());
			Properties properties = loadResultFromFile(filePath);
			int statusCode = Integer.parseInt(properties.getProperty("Code", "0"));
			int phaseCode = Integer.parseInt(properties.getProperty("ErrorPhase","0"));
			int ErrorCode = Integer.parseInt(properties.getProperty("ErrorCode", "0"));
			int rebootRequired = Integer.parseInt(properties.getProperty("RebootRequired","-1"));
			String progressMessage = properties.getProperty("FailMessage", "");
			String warnningMessage = properties.getProperty("WarningMessage", "");
			
			int jDeployStatus = Integer.parseInt(
				properties.getProperty( "JDeployStatus", Integer.toString( DeployStatus.DEPLOY_NA.value() ) ) );
			
			if (jDeployStatus != DeployStatus.DEPLOY_PREPARATION_DONE.value())
			{
				deployStatus = DeployStatus.valueOf( jDeployStatus );
			}
			else
			{
				if (statusCode == 1 ||  haveInstalledTheLatestVersion(statusCode, phaseCode,  ErrorCode ) ) { // success
					deployStatus = rebootRequired==1?DeployStatus.DEPLOY_SUCCESS_NEEDREBOOT:DeployStatus.DEPLOY_SUCCESS_SUPPRESSREBOOT;
					progressMessage = "";
				}					
				else if (statusCode == 2 || statusCode == 3 || ErrorCode != 0 ){ // failed	
					if (phaseCode == ProductDeployPhase.Initialize.getValue() 
							|| phaseCode == ProductDeployPhase.Connect.getValue() 
							|| phaseCode == ProductDeployPhase.Verify.getValue()) {
						deployStatus = DeployStatus.DEPLOY_FAIL_ON_CONNECTING;
					}else if (phaseCode == ProductDeployPhase.CopyImage.getValue()){
						deployStatus = DeployStatus.DEPLOY_FAIL_ON_COPYING_IMAGE;
					}else if (phaseCode == ProductDeployPhase.Install.getValue()){
						deployStatus = DeployStatus.DEPLOY_FAIL_ON_INSTALLING;
					}else{
						deployStatus = DeployStatus.DEPLOY_FAILED;
					}
				}else{
					// in process
					if (phaseCode == ProductDeployPhase.Initialize.getValue() 
							|| phaseCode == ProductDeployPhase.Connect.getValue() 
							|| phaseCode == ProductDeployPhase.Verify.getValue()) {
						deployStatus = DeployStatus.DEPLOY_CONNECTING;
					}else if (phaseCode == ProductDeployPhase.CopyImage.getValue()){
						deployStatus = DeployStatus.DEPLOY_COPYING_IMAGE;
					}else if (phaseCode == ProductDeployPhase.Install.getValue()){
						deployStatus = DeployStatus.DEPLOY_INSTALLING;
					}else if (phaseCode == ProductDeployPhase.Reboot.getValue()){
						deployStatus = DeployStatus.DEPLOY_REBOOTING;
					}else if (phaseCode == ProductDeployPhase.Finish.getValue()){
						deployStatus = DeployStatus.DEPLOY_FINISHED;
					}
					else
					{
						// DeployStatus.DEPLOY_NA
					}
				}
			}
			info.setDeployStatus(deployStatus);
			info.setPrograssMessage(progressMessage);
			info.setWarnningMessage(warnningMessage);
			return info;
		} catch (Exception e) {
			logger.error("[RemoteProductDeployServiceImpl]:Load status from file failed for target: "+target.getServerName(), e);
//			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.REMOTEDEPLOY_FailedToGetDeployStatus,
//				"Error getting deployment status." );
			return info;
		}
	}
	
	private boolean haveInstalledTheLatestVersion(  int statusCode, int phase, int errorCode   ) {
		//error, verify phase ,  80032 error means version already exist; 80031 means new version exist
		if( statusCode ==2 && phase ==3 && ( errorCode == 80032 || errorCode == 80031 ))
			return true;
		else
			return false;
	}

	private Properties loadResultFromFile(String filePath) throws Exception {
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filePath), INI_FILE_ENCODING));
			String filtedString = filterEscapedChars(reader);
			StringReader filteredReader = new StringReader(filtedString);
			Properties properties = new Properties();
			properties.load(filteredReader);
			return properties;
		}finally{
			if (reader != null)
				reader.close();
		}
	}
	
	private String filterEscapedChars(BufferedReader reader)throws Exception{
		String str = null;
		StringBuilder resultBuilder = new StringBuilder();
	    while((str = reader.readLine()) != null){
	    	if(str.contains("\\")){
	    		str = str.replace("\\", "\\\\");
	    	}
	    	resultBuilder.append(str);
	    	resultBuilder.append('\n');
	    }
		return resultBuilder.toString();
	}
	
	private String getDeployFilePath(String hostName) {
		return DEPLOY_CONFIG_FOLDER + File.separator + hostName + ".ini";
	}
	
	private static final int INI_FILE_SIZE = 8192 - 4; // "/r/n" will take 4 bytes
	
	private void allocDiskSpaceForIniFile( File file ) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile( file, "rw" );
		for (int i = 0; i < INI_FILE_SIZE; i ++)
			raf.writeBytes( "\n" );
		raf.close();
	}
	
	private String generateINIFile(DeployTargetDetail target) throws IOException {
		String targetName = target.getNodeID()+target.getServerName()==null?"":target.getServerName();
		
		String confFilePath = getDeployFilePath(target.getServerName());
		
		File iniFile = new File( confFilePath );
		if (!iniFile.exists())
			allocDiskSpaceForIniFile( iniFile );
		
		//PrintWriter out = new PrintWriter(confFilePath,INI_FILE_ENCODING);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( INI_FILE_SIZE );
		OutputStreamWriter osWriter = new OutputStreamWriter( baos, INI_FILE_ENCODING );
		PrintWriter out = new PrintWriter( osWriter );
		
		out.print("\ufeff");
		// [Install]
		out.println("[Install]");
		out.println("Locale=" + getLanguageCode());
		out.println("OpenRemoteRegistryService=" + STRING_YES );// for now, always set this to Yes 
		out.println("AutoReboot="+STRING_NO);
		out.println();

		// [RemoteHost]
		out.println("[RemoteHost]");
		out.println("HostName=" + target.getServerName());
		out.println("UserName=" + target.getUsername());
		String pass = target.getPassword();
		///temp fix issue: password ="" and setup module don't give correct error code; if encryt "", setup modele decrypt error happens;
		///setup team say it's not setup team's matter;  but use our AFDecryptString() can decrypt "" correctly;
		if (pass == null ||pass.equals("") ) {
			pass = " "; 
			logger.info("[RemoteProductDeployServiceImpl]: Node " + targetName +" have no password!");
		}
		out.println("Password=" + nativeFacade.AFEncryptString(pass));
		out.println("Encrypted=Yes");// for CPM , it should always be Encrypted
		out.println();

		// [Agent]
		out.println("[Agent]");
		out.println("Deploy=Yes");// currently it should always yes , because server also depend on agent
		out.println("NodeID=" + target.getUuid());
		out.println("InstallDir=" + target.getInstallDirectory() + DeployD2DSettings.INSTALL_Inner_PATH_D2D );
		out.println("UseHttps=" + ((target.getProtocol()==Protocol.Https) ? STRING_YES : STRING_NO));
		out.println("PortNumber=" + target.getPort());
		out.println("InstallDriver=" + (target.isInstallDriver()? STRING_YES : STRING_NO));
		out.println("StopUA=" + STRING_NO);
		out.println();
		
		// [Server]
		out.println("[Server]");
		out.println("Deploy=" + (target.getProductType() == 
				Integer.parseInt(ProductType.ProductRPS) ? STRING_YES : STRING_NO));
		out.println();

		// [AgentUpdate]
		out.println("[AgentUpdate]");
		out.println("Deploy="+STRING_YES);// always Yes for CPM 
		out.println();
		
		// [Result]
		out.println( "[Result]" );
		out.println( "JDeployStatus=" + target.getStatus() );
		out.println( "FailMessage=" + target.getProgressMessage() );
		out.println();

		//out.close();
		
		// With UTF16, every character needs 2 bytes, this guarantees the
		// size of contents will not exceed INI_FILE_SIZE
		while (baos.size() < INI_FILE_SIZE)
		{
			out.println();
			out.flush();
		}
		
		out.close();
		
		byte[] contents = baos.toByteArray();
		RandomAccessFile raf = new RandomAccessFile( iniFile, "rw" );
		raf.seek( 0 );
		raf.write( contents );
		raf.setLength( contents.length );
		raf.close();
		
		return confFilePath;
		
	}
	
	private String getLanguageCode() {
		Locale locale = DataFormatUtil.getServerLocale();
		String country = locale.getCountry();
		String language = locale.getLanguage();
		if ("ja".equals(language))
			return "1041";
		else if ("fr".equals(language))
			return "1036";
		else if ("de".equals(language))
			return "1031";
		else if ("pt".equals(language))
			return "1046";
		else if ("es".equals(language))
			return "1034";
		else if ("it".equals(language))
			return "1040";
		else if (language.equals("zh")){
			if(country.equalsIgnoreCase("CN"))
				return "2052";
			else 
				return "1028";
		}else
			return "1033";
	}
	
	private Process launchDeployProcess( String confFilePath ) throws Exception
	{
		String command = DEPLOY_FOLDER + "Deploy.exe \"" + confFilePath + "\"";
		Process process = Runtime.getRuntime().exec( command, null, new File( DEPLOY_FOLDER) );
		logger.info( "[RemoteProductDeployServiceImpl]: Launch a deploy process. command:" + command);
		return process;
	}

	@Override
	public int getDeployProcessExitValue(DeployTargetDetail target)
			throws EdgeServiceFault {
		int retCode = DeployD2DConstants.DeployProcessIsNotRunning;
		try {
			String path = getDeployFilePath(target.getServerName());
			
			int tryCount = 0;
			while (tryCount < 600)//Wait 5 minutes to get the process result
			{
				retCode = nativeFacade.getD2DDeployProcessStatus(path);
				if ((retCode == DeployD2DConstants.DeployProcessIsRunning) ||
					(retCode == DeployD2DConstants.DeployProcessIsNotRunning))
					break;
				tryCount ++;
				Thread.sleep( 500 );
			}
		} catch (Exception e) {
			logger.error("[RemoteProductServiceImpl]: getDeployProcessExitValue failed For Node: "+target.getServerName(),e);
		}
		return retCode;
	}

	@Override
	public String getTargetUUID(DeployTargetDetail target){
		try {
			String filePath = getDeployFilePath(target.getServerName());
			Properties properties = loadResultFromFile(filePath);
			String uuid = properties.getProperty("NodeID", "");
			logger.info("[RemoteProductServiceImpl]: getTargetUUID() success, the node: "
					+target.getNodeID()+"_"+target.getServerName()+" uuid is: " + uuid + "");
			return uuid;
		} catch (Exception e) {
			logger.error("[RemoteProductServiceImpl]: getTargetUUID() failed for target: " + target.getNodeID()+"_"+target.getServerName(), e);
		}
		return null;
	}
}
