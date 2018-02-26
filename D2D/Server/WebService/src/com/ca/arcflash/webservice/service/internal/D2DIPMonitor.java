package com.ca.arcflash.webservice.service.internal;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.rps.SettingsService;

public class D2DIPMonitor {
	private static final Logger logger = Logger.getLogger(D2DIPMonitor.class);
	private static final String IPListFileName = "IPConfig.xml";
	private static final String ELEM_NETWORK_ADAPTER = "NetworkAdapter";
	private static final String ELEM_IPADDR = "IPAddress";
	private static List<String> D2DIPList;
	
	public static void startMonitorIP() {
		logger.info("Start D2D IP address monitor");
		Runnable monitorThread = new Runnable() {
			public void run() {
				monitorIPChange(ServiceContext.getInstance().getDataFolderPath());
			}
		};
//		monitorThread.setDaemon(true);
//		monitorThread.start();
		CommonService.getInstance().getUtilTheadPool().submit(monitorThread);
	}
	
	private static WatchService watcher;
	
	private static void monitorIPChange(String monitorFolder) {
		logger.info("Update IP after webservice restarted");
		updateIP(CommonService.getInstance().getNativeFacade().getD2DIPList());
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			File file = new File(monitorFolder);
			Path path = file.toPath();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, 
					StandardWatchEventKinds.ENTRY_CREATE);
			while(true){
				WatchKey key = watcher.take();
				for(WatchEvent<?> event : key.pollEvents()) {
					if(event.context() instanceof Path) {
						Path subFile = (Path)event.context();
						if(IPListFileName.equalsIgnoreCase(subFile.toFile().getName())){
							logger.info("D2D IP changed, update and notify");
							if(!monitorFolder.endsWith("\\")){
								monitorFolder += "\\";
							}
							ArrayList<String> ipList = readIP(new File(monitorFolder + subFile.toFile().getName()));
							updateIP(ipList);
						}
					}
				}
				key.reset();
			}
		} catch (Exception e) {
			logger.debug("Exception in IP file wather", e);
		}
	}
	
	public static WatchService getWatcher(){
		return watcher;
	}
	
	private static ArrayList<String> readIP(File file) {
		ArrayList<String> IPList = new ArrayList<String>();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(file);
			NodeList adapters = doc.getElementsByTagName(ELEM_NETWORK_ADAPTER);
			if(adapters == null)
				return null;
			for(int i = 0;i < adapters.getLength(); i ++){
				Node adapter = adapters.item(i);
				NamedNodeMap attrs = adapter.getAttributes();
				if(attrs != null){
					Node connected = attrs.getNamedItem("Connected");
					if(connected == null || connected.getTextContent().trim().equals("1")){
						NodeList ipLists = adapter.getChildNodes();
						Node ipList = null;
						for(int j = 0; j < ipLists.getLength(); j ++){
							Node node = ipLists.item(j);
							if("IPAddressList".equalsIgnoreCase(node.getNodeName())){
								ipList = node;
								break;
							}
						}
						if(ipList == null)
							continue;
						NodeList ips = ipList.getChildNodes();
						if(ips == null)
							continue;
						for(int j = 0; j < ips.getLength(); j ++){
							Node ip = ips.item(j);
							if(ELEM_IPADDR.equalsIgnoreCase(ip.getNodeName())){
								NamedNodeMap ipAttr = ip.getAttributes();
								IPList.add(ipAttr.getNamedItem("IP").getTextContent().trim());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Failed to parse IP list", e);
		}
		return IPList;
	}
	
	private static void updateIP(List<String> ipList) {
		if(ipList == null)
			return;
		
		boolean updated = false;
		if(D2DIPList != null) {
			for(String ip : ipList){
				if(!D2DIPList.contains(ip)){
					updated = true;
					break;
				}
			}
		}else {
			updated = true;
		}
		
		if(updated){
			D2DIPList = ipList;
			if(D2DIPList.size() > 0)
				SettingsService.instance().notifyRPS4IPChange(D2DIPList);
		}
	}
	
	public static void main(String[] args) {
		monitorIPChange("d:\\test");		
	}
}
