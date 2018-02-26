package com.ca.arcflash.common.modelmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.repository.RepositoryModel;



public class RepositoryUtil {
	private static final Logger log = Logger.getLogger(RepositoryUtil.class);
	private RepositoryModel repository = null;
	private static RepositoryUtil instance = null;
	//use CommonUtil.D2DHAInstallPath+"Configuration\\repository.xml" for Monitor
	//use CommonUtil.D2DInstallPath+"Configuration\\repository.xml" for Monitee
	private String repositoryXML = "";
	private InputStream repositoryStream = null;
	public RepositoryUtil(String repositoryXML){
		this.repositoryXML = repositoryXML;
	}
	
	private RepositoryUtil(InputStream stream) {
		this.repositoryStream = stream;
	}
	public synchronized static RepositoryUtil getInstance(String repositoryXML){
		if(instance == null) instance = new RepositoryUtil(repositoryXML);
		return instance;
	}
	
	public synchronized static RepositoryUtil getInstance(InputStream stream) {
		return new RepositoryUtil(stream);
	}
	public synchronized void saveProductionServerRoot(ProductionServerRoot serverRoot) throws HAException {
		log.debug("saveProductionServerRoot begin: "+serverRoot);
		
		log.info("Save production server root for node "+ serverRoot.getProductionServerAFUID());
		RepositoryModel repository = load();
		if(repository==null){
			repository = new RepositoryModel();
		}
		
		repository.getProductionServers().remove(serverRoot);
		repository.getProductionServers().add(serverRoot);
		
		try {
			save();
		} catch (Exception e) {
			log.error(e);
			throw new HAException(e.getMessage(),MonitorWebServiceErrorCode.Repository_Save_Failure);
		}
		log.debug("saveProductionServerRoot end");
	}
	
	public synchronized void saveProductionServerRoot() throws HAException {
		
		RepositoryModel repository = load();
		if(repository==null){
			repository = new RepositoryModel();
		}
		
		try {
			save();
		} catch (Exception e) {
			log.error(e);
			throw new HAException(e.getMessage(),MonitorWebServiceErrorCode.Repository_Save_Failure);
		}
		log.debug("saveProductionServerRoot end");
	}
	
	private  synchronized RepositoryModel load() {
		if( repository != null) return repository;
		try {
			if (repositoryStream!= null) {
				repository = CommonUtil.unmarshal(repositoryStream, RepositoryModel.class);
			}else {
				File f = new File(repositoryXML );
				repository = CommonUtil.unmarshal(f, RepositoryModel.class);
			}
			
		} catch (Exception e) {
			repository = new RepositoryModel();
			if(log.isDebugEnabled())
				log.debug("load: failed to load respository.xml. ", e);
		}
		return repository;
		
	}
	public  synchronized void removeProductionServerRoot(ProductionServerRoot serverRoot) throws HAException{
		log.debug("removeProductionServerRoot begin: "+serverRoot);
		RepositoryModel repository = load();
		if(repository==null){
			repository = new RepositoryModel();
		}
		repository.getProductionServers().remove(serverRoot);
		try {
			save();
		} catch (Exception e) {
			log.error(e);
			throw new HAException(e.getMessage(),MonitorWebServiceErrorCode.Repository_Save_Failure);
		}
		log.debug("removeProductionServerRoot end");
	}
	public  synchronized ProductionServerRoot getProductionServerRoot(String afguid) throws HAException{
		log.debug("getProductionServerRoot begin: "+afguid);
		RepositoryModel repository = load();
		if(repository==null){
			repository = new RepositoryModel();
		}
		ProductionServerRoot temp = new ProductionServerRoot();
		temp.setProductionServerAFUID(afguid);
		int indexOf = repository.getProductionServers().indexOf(temp);
		if(indexOf==-1) throw new HAException("No replication for server "+afguid,MonitorWebServiceErrorCode.Repository_GetRootPath_empty);
		ProductionServerRoot productionServerRoot = repository.getProductionServers().get(indexOf);
		log.debug("getProductionServerRoot end");
		return productionServerRoot;
		
	}
	
	public synchronized String getFirtRepDestination(String vmGuid) {
		RepositoryModel repository = load();
		if(repository==null){
			return null;
		}
		for(ProductionServerRoot serverRoot: repository.getProductionServers()) {
			if (serverRoot == null)
				continue;
			ReplicaRoot root = serverRoot.getReplicaRoot();
			if (root == null)
				continue;
			if (root.getVmuuid().equalsIgnoreCase(vmGuid)
					&& root instanceof TransServerReplicaRoot) {
				return ((TransServerReplicaRoot)root).getFirstReplicDest();
			}
		}
		return null;
	}
	
	public  synchronized void clearProductionServerRoot() throws HAException{
		log.debug("clearProductionServerRoot begin");
		repository = new RepositoryModel();
		try {
			save();
		} catch (Exception e) {
			log.error(e);
			throw new HAException(e.getMessage(),MonitorWebServiceErrorCode.Repository_Save_Failure);
		}
		log.debug("clearProductionServerRoot end");
	}
	public  synchronized RepositoryModel getRepository(){
		RepositoryModel repository = load();
		return repository;
	}
	private  synchronized void save() throws IOException{
		
			File f = new File(repositoryXML );
			FileOutputStream fos =null;
			try {
				f.createNewFile();
				fos = new FileOutputStream(f);
	
				JAXB.marshal(repository, fos);
			}finally{
				try{
					if(fos!=null){
					fos.close();
					}
				}catch(Exception e){}
			}
		}
		
}
