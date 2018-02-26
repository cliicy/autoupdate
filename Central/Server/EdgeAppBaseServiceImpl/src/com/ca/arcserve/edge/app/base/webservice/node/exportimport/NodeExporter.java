package com.ca.arcserve.edge.app.base.webservice.node.exportimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.common.SqlUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.CsvBuilder;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.AdEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.EsxEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.HypervEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.NodeExportEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.SiteEntity;

public class NodeExporter{
	private static Logger log = Logger.getLogger( NodeExporter.class );
	private static final String exportFileNamePrefix= "node_export_"; ///temp solution; no localization now
	public static final String exportAdName="export_ad";
	public static final String exportESsxName="export_esx";
	public static final String exportHypervName="export_hyperv";
	public static final String exportGatewayName="export_gateway";
	public static final String exportNodeName="export_node";
	private static SimpleDateFormat formatter = new SimpleDateFormat( MessageReader.getDateFormat("timeDateFormat") );
	private static SimpleDateFormat formatForFileName = new SimpleDateFormat( "yyyy_MM_dd__HH_mm_ss_SSS" ); 
	private IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeGatewayDao gatewayDao = DaoFactory.getDao(IEdgeGatewayDao.class);
	private IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private List<Integer> nodeIds;
	private File exportFolder; 
	private int nodePaggingCount = 50;
	private List<Integer> gateWayIds = new ArrayList<Integer>();
	private List<Integer> adIds = new ArrayList<Integer>();
	private List<Integer> esxIds = new ArrayList<Integer>();
	private List<Integer> hypervIds = new ArrayList<Integer>();
	
	public NodeExporter(List<Integer> nodeIds){
		this.nodeIds = nodeIds;
	}
	
	public String run() throws EdgeServiceFault{
		try {
			if(!createFolder()){
				throw new EdgeServiceFault("Failed to create node export folder", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.NodeExportFailed,
						""));
			}
			exportNodeToCsv();
			exportEsxToCsv();
			exportADToCsv();
			exportHypervToCsv();
			exportGatewayToCsv();
			if(!generateZip()){
				throw new EdgeServiceFault("Failed to create node export folder", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.NodeExportFailed,
						""));
			}
			return exportFolder.getName() + ".zip";
		} catch (Exception e) {
			throw new EdgeServiceFault("Failed to create node export folder", new EdgeServiceFaultBean(
					EdgeServiceErrorCode.NodeExportFailed,
					""));
		}finally{
			if( exportFolder !=null && exportFolder.exists() ){
				CommonUtil.recursiveDelFolder( exportFolder.getAbsolutePath() );
			}
		}
	}
	
	private void exportNodeToCsv(){
		if(nodeIds == null || nodeIds.isEmpty()){
			return;
		}
		
		CsvBuilder builder = new CsvBuilder();
		writeExportNodeTitle(builder);
		int nodeCount = nodeIds.size();
		int page = nodeCount/nodePaggingCount+1;
		for(int i = 0 ; i < page ; i++){
			List<Integer> splitList = new ArrayList<Integer>();
			for(int j = 0 ; j < nodePaggingCount ; j++){
				int nodeJ = i*nodePaggingCount+j;
				if(nodeJ >= nodeCount)
					break;
				splitList.add(this.nodeIds.get(nodeJ));
			}
			String hostIdArray = SqlUtil.marshal(splitList);
			List<NodeExportEntity> hosts = new ArrayList<NodeExportEntity>();
			hostMgrDao.as_edge_host_getNodeExportEntity(hostIdArray, hosts);
			for(NodeExportEntity entity : hosts){
				if(entity.getEsxId() > 0){
					if(!esxIds.contains(entity.getEsxId())){
						esxIds.add(entity.getEsxId());
					}
				}
				if(entity.getAdId() > 0){
					if(!adIds.contains(entity.getAdId())){
						adIds.add(entity.getAdId());
					}
				}
				if(entity.getHypervId() > 0){
					if(!hypervIds.contains(entity.getHypervId())){
						hypervIds.add(entity.getHypervId());
					}
				}
				if(entity.getGatewayId() > 0){
					if(!gateWayIds.contains(entity.getGatewayId())){
						gateWayIds.add(entity.getGatewayId());
					}
				}
				writeExportNodeRow(builder,entity);
			}
			writeCSVFile(builder, exportNodeName);
		}
	}
	
	private void exportADToCsv(){
		if(adIds.isEmpty()){
			return;
		}
		CsvBuilder builder = new CsvBuilder();
		writeAdTitle(builder);
		String adIdArray = SqlUtil.marshal(adIds);
		List<AdEntity> ads = new ArrayList<AdEntity>();
		adDao.as_edge_getADSByIds(adIdArray, ads);
		for(AdEntity ad : ads){
			writeAdRow(builder, ad);
			if(!gateWayIds.contains(ad.getGatewayId())){
				gateWayIds.add(ad.getGatewayId());
			}
		}
		writeCSVFile(builder, exportAdName);
	}
	
	private void exportEsxToCsv(){
		if(esxIds.isEmpty()){
			return;
		}
		CsvBuilder builder = new CsvBuilder();
		writeEsxTitle(builder);
		String esxIdArray = SqlUtil.marshal(esxIds);
		List<EsxEntity> esxs = new ArrayList<EsxEntity>();
		esxDao.as_edge_getEsxsByIds(esxIdArray, esxs);
		for(EsxEntity entity : esxs){
			writeEsxRow(builder,entity);
			if(!gateWayIds.contains(entity.getGatewayId())){
				gateWayIds.add(entity.getGatewayId());
			}
		}
		writeCSVFile(builder, exportESsxName);
	}
	
	private void exportHypervToCsv(){
		if(hypervIds.isEmpty()){
			return;
		}
		CsvBuilder builder = new CsvBuilder();
		writeHypervTitle(builder);
		String hypervIdArray = SqlUtil.marshal(hypervIds);
		List<HypervEntity> hypervs = new ArrayList<HypervEntity>();
		hyperVDao.as_edge_getHypervsByIds(hypervIdArray, hypervs);
		for(HypervEntity entity : hypervs){
			writeHypervRow(builder,entity);
			if(!gateWayIds.contains(entity.getGatewayId())){
				gateWayIds.add(entity.getGatewayId());
			}
		}
		writeCSVFile(builder, exportHypervName);
	}
	
	private void exportGatewayToCsv(){
		if(gateWayIds.isEmpty()){
			return;
		}
		CsvBuilder builder = new CsvBuilder();
		writeGatewayTitle(builder);
		String gatewayIdArray = SqlUtil.marshal(gateWayIds);
		List<SiteEntity> sites = new ArrayList<SiteEntity>();
		gatewayDao.as_edge_gateway_GetGateWayAndSite(gatewayIdArray, sites);
		for(SiteEntity siteEntity : sites){
			writeGatewayRow(builder,siteEntity);
		}
		writeCSVFile(builder, exportGatewayName);
	}
	
	private boolean createFolder(){	 
		String exportPath = CommonUtil.getReportFileTempDir(); // use exported file directory same as report export file path;
		String exportFolderName = exportFileNamePrefix + formatForFileName.format(new Date()); 
		exportFolder = new File ( exportPath + exportFolderName ); 
		try {
			if( exportFolder.exists() ){
				String nodeExportIdentifier = UUID.randomUUID().toString();
				exportFolder = new File ( exportPath + exportFolderName + "_" + nodeExportIdentifier.hashCode() ); 
				if( exportFolder.exists() ) {
					log.error( "[NodeExporter]: node export create folder " + exportFolder + " failed ! a folder with duplicated name exist ");
					return false;
				}
			}
			if( exportFolder.mkdir() ) {
				return true;
			}
			else {
				log.error( "[NodeExporter]: node export create folder " + exportFolder + " failed!");
				return false;
			}
		}
		catch( Exception e  ) {
			log.error( "[NodeExporter]: node export create folder " + exportFolder + " failed! a folder with duplicated name exist ");
			return false;
		}
	}
	
	private void writeCSVFile( CsvBuilder builder, String csvName) {
		FileOutputStream output = null; 
		String exportFileName = csvName +".csv" ;
		try {
			File exportFile = new File ( exportFolder.getAbsolutePath(), exportFileName );
			if( exportFile.exists() || exportFile.createNewFile() ) { 	
				log.info( "[NodeExporter]: write start: " + new Date()   );
			    output = new FileOutputStream(exportFile);
			    FileChannel channel = output.getChannel();

			    CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
			    CharBuffer buffer = CharBuffer.wrap( builder.getCsvContent().toCharArray() );
			    ByteBuffer byteBuffer = encoder.encode(buffer);
			    channel.write( ByteBuffer.wrap(
			    		new byte[] {(byte)0xEF , (byte)0xBB , (byte)0xBF}) ); 
			    channel.write( byteBuffer );
			    channel.close();
				log.info( "[NodeExporter]: write end: " + new Date()   );
			}
			else {
				log.error( "[NodeExporter]:  Failed to create csv file: "+exportFileName+" in disk!" );
			}
		}
		catch( IOException e ) {
			log.error( "[NodeExporter]: Failed to create csv file: "+exportFileName, e);
		}
		finally {
			if( output != null ) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private boolean generateZip() {
		File[] exportCsvs = exportFolder.listFiles();
		if( exportCsvs == null ) {
			log.error( "[NodeExporter]: genrate Zip failed! no csv files " );
			return false;
		}
		String exportPath = CommonUtil.getReportFileTempDir(); // use exported file directory same as report export file path;
		
		File zipFile = new File( exportPath + exportFolder.getName() + ".zip" ); 

		byte[] buf=new byte[32*1024];
		ZipOutputStream out = null;
		FileInputStream in  = null;
	    try {
	    	out = new ZipOutputStream(new FileOutputStream(zipFile));
	    	for( int i =0 ; i< exportCsvs.length; i++ ) {
	    		File toZip = exportCsvs[i];
	    		in = new FileInputStream( toZip );
	    		out.putNextEntry(new ZipEntry( toZip.getName()));
	    		int len;
	    		while((len=in.read(buf))>0) {
	    			out.write(buf,0,len);
	    		}
	    		out.closeEntry();
	    		in.close();
	    	}
	    	return true;
	    } 
	    catch (Exception e) {
	    	log.error( "[NodeExporter]: genrate Zip failed! "  ,e );
	      	return false;
	    }
	    finally {
	    	try {
	    		if( out !=null ) {
					out.close();
	    		}
	    		if( in !=null ) {
					in.close();
	    		}
			} catch (IOException e) {
			}
	    }
	}
	
	private void writeExportNodeTitle(CsvBuilder builder){
		List<String> rowStrings = new ArrayList<String>(); 
		
		rowStrings.add("nodeId");
		rowStrings.add("nodeDescription");
		rowStrings.add("lastUpdated");
		rowStrings.add("hostName");
		rowStrings.add("domainName");
		rowStrings.add("ipAddress");
		rowStrings.add("osDescription");
		rowStrings.add("osType");
		rowStrings.add("visible");
		rowStrings.add("appStatus");
		
		rowStrings.add("serverPrincipalName");
		rowStrings.add("hostType");
		rowStrings.add("timezone");
		rowStrings.add("jobPhase");
		rowStrings.add("protectionTypeBitmap");
		rowStrings.add("rawMachineType");
		rowStrings.add("userName");
		rowStrings.add("password");
		rowStrings.add("uuid");
		rowStrings.add("authUuid");
		
		rowStrings.add("protocol");
		rowStrings.add("port");
		rowStrings.add("type");
		rowStrings.add("majorVersion");
		rowStrings.add("minorVersion");
		rowStrings.add("buildNumber");
		rowStrings.add("updateNumber");
		rowStrings.add("status");
		rowStrings.add("managed");
		rowStrings.add("caUser");
		
		rowStrings.add("caPassword");
		rowStrings.add("authMode");
		rowStrings.add("arcserveProtocol");
		rowStrings.add("arcservePort");
		rowStrings.add("arcserveType");
		rowStrings.add("arcserveVersion");
		rowStrings.add("arcserveManaged");
		rowStrings.add("arcserveUuid");
		rowStrings.add("vmStatus");
		rowStrings.add("vmName");
		
		rowStrings.add("vmInstanceUUID");
		rowStrings.add("vmUUID");
		rowStrings.add("hypervisorHost");
		rowStrings.add("esxEssential");
		rowStrings.add("hypervisorSocketCount");
		rowStrings.add("vmXpath");
		rowStrings.add("vmGuestOS");
		rowStrings.add("adId");
		rowStrings.add("adStatus");
		rowStrings.add("esxId");
		
		rowStrings.add("hypervId");
		rowStrings.add("otherHypervisorHostName");
		rowStrings.add("otherHypervisorSocketCount");
		rowStrings.add("gatewayId");
		rowStrings.add("fqdnNames");
		
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeExportNodeRow(CsvBuilder builder, NodeExportEntity node){
		List<String> rowStrings = new ArrayList<String>(); 
		
		rowStrings.add(node.getNodeId()+"");
		rowStrings.add(node.getNodeDescription());
		String date = node.getLastUpdated()==null?"":formatter.format( node.getLastUpdated() );
		rowStrings.add(date);
		rowStrings.add(node.getHostName());
		rowStrings.add(node.getDomainName());
		rowStrings.add(node.getIpAddress());
		rowStrings.add(node.getOsDescription());
		rowStrings.add(node.getOsType());
		rowStrings.add(node.getVisible()+"");
		rowStrings.add(node.getAppStatus()+"");
		
		rowStrings.add(node.getServerPrincipalName());
		rowStrings.add(node.getHostType()+"");
		rowStrings.add(node.getTimezone()+"");
		rowStrings.add(node.getJobPhase()+"");
		rowStrings.add(node.getProtectionTypeBitmap()+"");
		rowStrings.add(node.getRawMachineType()+"");
		rowStrings.add(node.getUserName());
		rowStrings.add(BackupService.getInstance().getNativeFacade().encrypt(node.getPassword()));
		rowStrings.add(node.getUuid());
		rowStrings.add(node.getAuthUuid());
		
		rowStrings.add(node.getProtocol()+"");
		rowStrings.add(node.getPort()+"");
		rowStrings.add(node.getType()+"");
		rowStrings.add(node.getMajorVersion());
		rowStrings.add(node.getMinorVersion());
		rowStrings.add(node.getBuildNumber());
		rowStrings.add(node.getUpdateNumber());
		rowStrings.add(node.getStatus()+"");
		rowStrings.add(node.getManaged()+"");
		rowStrings.add(node.getCaUser());
		
		rowStrings.add(node.getCaPassword());
		rowStrings.add(node.getAuthMode()+"");
		rowStrings.add(node.getArcserveProtocol()+"");
		rowStrings.add(node.getArcservePort()+"");
		rowStrings.add(node.getArcserveType()+"");
		rowStrings.add(node.getArcserveVersion()+"");
		rowStrings.add(node.getArcserveManaged()+"");
		rowStrings.add(node.getArcserveUuid());
		if(node.getEsxId() > 0){
			rowStrings.add(node.getEsxVmStatus()+"");
			rowStrings.add(node.getEsxVmName());
			
			rowStrings.add(node.getEsxvmInstanceUuid());
			rowStrings.add(node.getEsxVmUuid());
			rowStrings.add(node.getEsxHost());
			rowStrings.add(node.getEsxEssential()+"");
			rowStrings.add(node.getEsxSocketCount()+"");
			rowStrings.add(node.getEsxVmXPath());
			rowStrings.add(node.getEsxVmGuestOS());
		}else {
			rowStrings.add(node.getHypervVmStatus()+"");
			rowStrings.add(node.getHypervVmName());
			rowStrings.add(node.getHypervVmInstanceUuid());
			rowStrings.add(node.getHypervVmUuid());
			rowStrings.add(node.getHypervHost());
			rowStrings.add("");
			rowStrings.add(node.getHypervSocketCount()+"");
			rowStrings.add("");
			rowStrings.add(node.getHypervVmGuestOS());
		}
		rowStrings.add(node.getAdId()+"");
		rowStrings.add(node.getAdStatus()+"");
		rowStrings.add(node.getEsxId()+"");
		
		rowStrings.add(node.getHypervId()+"");
		rowStrings.add(node.getOtherHypervisorHostName());
		rowStrings.add(node.getOtherHypervisorSocketCount()+"");
		rowStrings.add(node.getGatewayId()+"");
		rowStrings.add(node.getFqdnNames());
		
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeGatewayTitle(CsvBuilder builder){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add("gatewayId");
		rowStrings.add("gatewayName");
		rowStrings.add("gatewayUuid");
		rowStrings.add("gatewayHostUuid");
		rowStrings.add("gatewayHostName");
		rowStrings.add("isLocal");
		rowStrings.add("lastContactTime");
		rowStrings.add("heartBeatInterval");
		rowStrings.add("gatewayCreateTime");
		rowStrings.add("updateTime");
		rowStrings.add("consoleHostName");
		rowStrings.add("consoleProtocol");
		rowStrings.add("consolePort");
		rowStrings.add("gatewayProtocol");
		rowStrings.add("gatewayPort");
		rowStrings.add("gatewayUserName");
		rowStrings.add("gatewayPassword");
		rowStrings.add("registrationText");
		rowStrings.add("siteName");
		rowStrings.add("siteDescription");
		rowStrings.add("siteCreateTime");
		rowStrings.add("siteUpdateTime");
		rowStrings.add("address");
		rowStrings.add("email");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeGatewayRow(CsvBuilder builder, SiteEntity entity){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add(entity.getGatewayId()+"");
		rowStrings.add(entity.getGatewayName());
		rowStrings.add(entity.getGatewayUuid());
		rowStrings.add(entity.getGatewayHostUuid());
		rowStrings.add(entity.getGatewayHostName());
		rowStrings.add(entity.getIsLocal()+"");
		String date = entity.getLastContactTime() == null ? "" : formatter.format( entity.getLastContactTime() );
		rowStrings.add(date);
		rowStrings.add(entity.getHeartBeatInterval()+"");
		date = entity.getGatewayCreateTime() == null ? "" : formatter.format( entity.getGatewayCreateTime() );
		rowStrings.add(date);
		date = entity.getUpdateTime() == null ? "" : formatter.format( entity.getUpdateTime() );
		rowStrings.add(date);
		rowStrings.add(entity.getConsoleHostName());
		rowStrings.add(entity.getConsoleProtocol()+"");
		rowStrings.add(entity.getConsolePort()+"");
		rowStrings.add(entity.getGatewayProtocol()+"");
		rowStrings.add(entity.getGatewayPort()+"");
		rowStrings.add(entity.getGatewayUserName());
		rowStrings.add(BackupService.getInstance().getNativeFacade().encrypt( entity.getGatewayPassword() ));
		rowStrings.add(entity.getRegistrationText());
		rowStrings.add(entity.getSiteName());
		rowStrings.add(entity.getSiteDescription());
		date = entity.getSiteCreateTime()==null?"":formatter.format( entity.getSiteCreateTime() );
		rowStrings.add(date);
		date = entity.getSiteUpdateTime()==null ? "" : formatter.format( entity.getSiteUpdateTime() );
		rowStrings.add(date);
		rowStrings.add(entity.getAddress());
		rowStrings.add(entity.getEmail());
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeAdTitle(CsvBuilder builder){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add("id");
		rowStrings.add("username");
		rowStrings.add("password");
		rowStrings.add("filter");
		rowStrings.add("domainControler");
		rowStrings.add("gatewayId");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeAdRow(CsvBuilder builder, AdEntity entity){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add(entity.getId()+"");
		rowStrings.add(entity.getUsername());
		rowStrings.add(BackupService.getInstance().getNativeFacade().encrypt(entity.getPassword()));
		rowStrings.add(entity.getFilter());
		rowStrings.add(entity.getDomainControler());
		rowStrings.add(entity.getGatewayId()+"");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeEsxTitle(CsvBuilder builder){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add("id");
		rowStrings.add("hostName");
		rowStrings.add("userName");
		rowStrings.add("password");
		rowStrings.add("protocol");
		rowStrings.add("port");
		rowStrings.add("serverType");
		rowStrings.add("visible");
		rowStrings.add("essential");
		rowStrings.add("socketCount");
		rowStrings.add("description");
		rowStrings.add("uuid");
		rowStrings.add("gatewayId");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeEsxRow(CsvBuilder builder, EsxEntity entity){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add(entity.getId()+"");
		rowStrings.add(entity.getHostName());
		rowStrings.add(entity.getUserName());
		rowStrings.add(BackupService.getInstance().getNativeFacade().encrypt(entity.getPassword()));
		rowStrings.add(entity.getProtocol()+"");
		rowStrings.add(entity.getPort()+"");
		rowStrings.add(entity.getServerType()+"");
		rowStrings.add(entity.getVisible()+"");
		rowStrings.add(entity.getEssential()+"");
		rowStrings.add(entity.getSocketCount()+"");
		rowStrings.add(entity.getDescription());
		rowStrings.add(entity.getUuid());
		rowStrings.add(entity.getGatewayId()+"");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeHypervTitle(CsvBuilder builder){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add("id");
		rowStrings.add("hostName");
		rowStrings.add("userName");
		rowStrings.add("password");
		rowStrings.add("protocol");
		rowStrings.add("port");
		rowStrings.add("visible");
		rowStrings.add("socketCount");
		rowStrings.add("type");
		rowStrings.add("gatewayId");
		builder.addLineToBufferInstantly( rowStrings );
	}
	
	private void writeHypervRow(CsvBuilder builder, HypervEntity entity){
		List<String> rowStrings = new ArrayList<String>();
		rowStrings.add(entity.getId()+"");
		rowStrings.add(entity.getHostName());
		rowStrings.add(entity.getUserName());
		rowStrings.add(BackupService.getInstance().getNativeFacade().encrypt(entity.getPassword()));
		rowStrings.add(entity.getProtocol()+"");
		rowStrings.add(entity.getPort()+"");
		rowStrings.add(entity.getVisible()+"");
		rowStrings.add(entity.getSocketCount()+"");
		rowStrings.add(entity.getType()+"");
		rowStrings.add(entity.getGatewayId()+"");
		builder.addLineToBufferInstantly( rowStrings );
	}
}
