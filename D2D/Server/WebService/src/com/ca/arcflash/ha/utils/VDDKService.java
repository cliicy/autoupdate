package com.ca.arcflash.ha.utils;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceException;

public class VDDKService {
	private final Logger log = Logger.getLogger(VDDKService.class);
	private final String cmdName = "ArcDrvInstall.exe";
	private final String logFileName = "Logs\\ArcDrvInstall.log";
	private final String vddkPath = "bin";
	private final String amd64VddkPath = "Bin\\VDDK64\\bin\\AMD64";
	
	private static VDDKService instance = new VDDKService();
	
	private VDDKService(){
		
	}
	
	public static VDDKService getInstance() {
		return instance;
	}
	
	private String getPath(String name){
		String path="";
		String d2dInstalledPath = CommonUtil.D2DInstallPath;
		if(d2dInstalledPath.endsWith("\\")){
			path = d2dInstalledPath + name;
		}
		else{
			path = d2dInstalledPath +"\\" +name;
		}
		return path;
	}
	
	private String getVDDKSysPath(){
		String path ="";
		boolean isAMD64= false;
		String vddkRootPath = CommonUtil.VDDKInstallPath;
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		 try {
			short cpuArch = facade.GetHostProcessorArchitectural();
			if(cpuArch==9){
				isAMD64 = true;
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			log.error("Failed to get the machine CPU type!");
			log.error(e);
			return "";
		}
		
		if(vddkRootPath.endsWith("\\")){
			if(isAMD64){
				path = vddkRootPath + amd64VddkPath;
			}
			else{
				path = vddkRootPath+ vddkPath;
			}
		}
		else{
			if(isAMD64){
				path = vddkRootPath +"\\"+ amd64VddkPath;
			}
			else{
				path = vddkRootPath+"\\"+ vddkPath;
			}
		}
		
		File file = new File(path);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".sys")) {
					return true;
				}
				return false;
			}
		};
		File[] sysFiles = file.listFiles(filter);
		if((sysFiles == null)||(sysFiles.length == 0)){
			log.error("Failed to get VDDK sys file!");
			return "";
		}
		
		if(sysFiles.length >1){
			for (File sysFile : sysFiles) {
				if(sysFile.getName().startsWith("vstor"))
					return file.getPath();
			}
		}
		
		return sysFiles[0].getPath();
		
	}
	
	public int install(){
		//String cmdPath = getPath(cmdName);
		String logPath = getPath(logFileName);
		String vddkSys = getVDDKSysPath();
		return execute(cmdName,logPath, vddkSys);
	}
	
	//VDDK installed path:C:\Program Files\Arcserve\Unified Data Protection\Engine\BIN\VDDK
	//ArcDrvInstall.exe -i vstor2-x64-1.2.1.sys -l 1.log -p 2
	//C:\Program Files\Arcserve\Unified Data Protection\Engine\BIN\VDDK\bin\vstor2-mntapi10-shared.sys
	//C:\Program Files\Arcserve\Unified Data Protection\Engine\BIN\VDDK\Bin\VDDK64\bin\AMD64\vstor2-mntapi10.sys
	private synchronized int execute(String cmdPath, String logPath, String vddkSys){
		
		String cmd= "cmd /c " + cmdPath + " -i \"" + vddkSys+ "\" -l \"" +logPath+"\" -p 2";
		Runtime rn = Runtime.getRuntime();   
	    Process process = null;   
	    try {
	    	log.info("ExeCMD:"+cmd);
	    	process = rn.exec(cmd);
	    	process.waitFor();
	    	int iResult = process.exitValue();
	    	log.info("ArcDrvInstall.exe exit code="+iResult);
	    	process.destroy();
	    	process=null;
	    	
	    	/*if((iResult>=1)&&(iResult<=4)){
	    		return true;
	    	}*/
	    	return iResult;
	    } catch (Exception e) {   
	    	log.error("Failed to install the VDDK service");
	        log.error(e);
	    }   
	    return -10;
	}
	
	
	public static void main(String[] args){
		VDDKService vddkService =new VDDKService();
		System.out.print(vddkService.execute("D:\\test\\32\\ArcDrvInstall.exe", "d:\\test\\32\\test.log", "d:\\test\\32\\vstor2-x32-1.2.1.sys"));
	}
}
