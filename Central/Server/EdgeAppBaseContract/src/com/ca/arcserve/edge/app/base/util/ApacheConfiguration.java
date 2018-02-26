package com.ca.arcserve.edge.app.base.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;



public class ApacheConfiguration {
	

	public static void main(String[] args) throws Exception {
		
		modifyApacheConfiguration();

	}
	
	private static void modifyApacheConfiguration() throws Exception{
		
		
		String value = getInstallationPath();
		
		String serverRoot = "\"" + value + "Apache" + "\"";
		String documentRoot = "\"" + value + "Apache\\htdocs" + "\"";
		String cgiBin = "\"" + value + "Apache/cgi-bin/" + "\"";
		
		String httdFile = value + "Apache\\conf\\httpd.conf";
		String httdFileNew = value + "Apache\\conf\\httpd.conf.new";
		
		File fileObj = new File(httdFile);
		File fileNewObj = new File(httdFileNew);
		
		BufferedReader reader = new BufferedReader(new FileReader(fileObj));
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileNewObj));
		
		String line = null;
		while((line=reader.readLine()) != null){
			
			line = line.trim();
			
			if(line.startsWith("ServerRoot")){
				line = "ServerRoot " + serverRoot;
			}else if(line.startsWith("DocumentRoot")){
				line = "DocumentRoot " + documentRoot;
			}else if(line.startsWith("<Directory") && line.contains("htdocs")){
				line = "<Directory " + documentRoot +">";
			}else if(line.startsWith("ScriptAlias")){
				line = "ScriptAlias /cgi-bin/ " + cgiBin;
			}else if(line.startsWith("<Directory") && line.contains("cgi-bin")){			
				line = "<Directory " + cgiBin +">";	
			}else if(line.startsWith("Listen")){
				line = "Listen " + getApachePort();
			}else if(line.contains("rps_proxy_connect.so")){
				line = "#LoadModule proxy_connect_module modules/rps_proxy_connect.so";
			}		
			writer.write(line + "\r\n");
		}
		
		line = generateProxyPassForHTTP();
		
		writer.write(line);
		
		reader.close();
		writer.close();
		
		fileObj.delete();
		fileNewObj.renameTo(fileObj);
		
		
		String httpdSSLFile = value + "Apache\\conf\\extra\\httpd-ssl.conf";
		String httpdSSLFileNew = value + "Apache\\conf\\extra\\httpd-ssl.conf.new";
		
		File fileSSLObj = new File(httpdSSLFile);
		File fileSSLNewObj = new File(httpdSSLFileNew);
		
		BufferedReader sslreader = new BufferedReader(new FileReader(fileSSLObj));
		BufferedWriter sslwriter = new BufferedWriter(new FileWriter(fileSSLNewObj));
		
		while((line=sslreader.readLine()) != null){
			
			line = line.trim();
			
			if(line.startsWith("DocumentRoot")){
				line = "DocumentRoot " + documentRoot;
			}else if(line.startsWith("SSLCertificateFile")){
				line = "SSLCertificateFile " + "\"" + value + "BIN/RPSComm/cert/server.crt" +"\"";
			}else if(line.startsWith("SSLCertificateKeyFile")){
				line = "SSLCertificateKeyFile " + "\"" + value + "BIN/RPSComm/cert/server.key" +"\"";
			}else if(line.startsWith("SSLSessionCache") && line.contains("shmcb")){
				line = "SSLSessionCache       \"shmcb:" + value + "Apache/logs/ssl_scache(512000)\"";
			}else if(line.startsWith("<VirtualHost _default")){
				line = "<VirtualHost _default_:" + getApachePort() + ">";
			}else if(line.startsWith("ServerName")){
				line = "ServerName " + InetAddress.getLocalHost().getHostName(); 
			}else if(line.startsWith("ServerAlias")){
				line = "ServerAlias " + InetAddress.getLocalHost().getHostName();
			}else if(line.startsWith("ProxyPass") && line.contains("connectiontimeout")){
				line = "\r\n";
			}else if(line.startsWith("ProxyPassReverse") && line.contains("ajp")){
				line = generateProxyPassForHTTPS();
			}
			
			sslwriter.write(line + "\r\n");
		}
		
		sslreader.close();
		sslwriter.close();	
		
		fileSSLObj.delete();
		fileSSLNewObj.renameTo(fileSSLObj);
		
	}
	
	
	public static String generateProxyPassForHTTPS() throws Exception{
		
		String rtn = "\r\n";
		
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyPass  /samlsso  https://localhost:" + getWSo2Port() +"/samlsso    connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /samlsso  https://localhost:" + getWSo2Port() +"/samlsso").append(rtn)
		.append("ProxyPassReverse  /samlsso  https://localhost:" + getApachePort() +"/samlsso").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /authenticationendpoint  https://localhost:" + getWSo2Port() +"/authenticationendpoint  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /authenticationendpoint  https://localhost:" + getWSo2Port() +"/authenticationendpoint").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /commonauth  https://localhost:" + getWSo2Port() +"/commonauth  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /commonauth  https://localhost:" + getWSo2Port() +"/commonauth").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /carbon  https://localhost:" + getWSo2Port() +"/carbon  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /carbon  https://localhost:" + getWSo2Port() +"/carbon").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /management  https://localhost:" + getTomcatPort() +"/management  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /management  https://localhost:" + getTomcatPort() + "/management").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /EdgeWSO2FacadeWebService  https://localhost:" + getTomcatPort() + "/EdgeWSO2FacadeWebService  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /gateway  https://localhost:" + getTomcatPort() + "/gateway  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /broker  https://localhost:" + getMessageServicePort() + "/broker  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		//add webservice proxy
		builder.append("ProxyPass  /services  https://localhost:" + getTomcatPort() + "/management/services  connectiontimeout=300 timeout=900 retry=0").append(rtn);
		
		builder.append("ProxyPass  /UdpHttpService  https://localhost:" + getTomcatPort() + "/management/UdpHttpService  connectiontimeout=300 timeout=900 retry=0").append(rtn);
		
		builder.append("ProxyPass  /  https://localhost:" + getTomcatPort() + "/management  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPassReverseCookiePath /management/ /").append(rtn)
		.append(rtn);
		
		return builder.toString();
	}
	
	public static String generateProxyPassForHTTP() throws Exception{
		
		String rtn = "\r\n";
		
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyPass  /samlsso  http://localhost:" + getWSo2Port() +"/samlsso    connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /samlsso  http://localhost:" + getWSo2Port() +"/samlsso").append(rtn)
		.append("ProxyPassReverse  /samlsso  http://localhost:" + getApachePort() +"/samlsso").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /authenticationendpoint  http://localhost:" + getWSo2Port() +"/authenticationendpoint  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /authenticationendpoint  http://localhost:" + getWSo2Port() +"/authenticationendpoint").append(rtn)
		.append(rtn);
		
		
		builder.append("ProxyPass  /commonauth  http://localhost:" + getWSo2Port() +"/commonauth  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /commonauth  http://localhost:" + getWSo2Port() +"/commonauth").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /carbon  http://localhost:" + getWSo2Port() +"/carbon  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /carbon  http://localhost:" + getWSo2Port() +"/carbon").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /management  http://localhost:" + getTomcatPort() + "/management  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append("ProxyPassReverse  /management  http://localhost:" + getTomcatPort() + "/management").append(rtn)
		.append(rtn);
		
		
		builder.append("ProxyPass  /EdgeWSO2FacadeWebService  http://localhost:" + getTomcatPort() + "/EdgeWSO2FacadeWebService  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /gateway  http://localhost:" + getTomcatPort() + "/gateway  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPass  /broker  http://localhost:" + getMessageServicePort() +"/broker  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		//add webservice proxy
		builder.append("ProxyPass  /services  http://localhost:" + getTomcatPort() + "/management/services  connectiontimeout=300 timeout=900 retry=0").append(rtn);

		builder.append("ProxyPass  /UdpHttpService  http://localhost:" + getTomcatPort() + "/management/UdpHttpService  connectiontimeout=300 timeout=900 retry=0").append(rtn);

		builder.append("ProxyPass  /  http://localhost:" + getTomcatPort() + "/management  connectiontimeout=300 timeout=900 retry=0").append(rtn)
		.append(rtn);
		
		builder.append("ProxyPassReverseCookiePath /management/ /").append(rtn)
		.append(rtn);
		
		return builder.toString();
		
	}
	
	
	private static String getWSo2Port() throws Exception{
		
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\IdentityServer";
		String port = null;
		
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String url=  registry.getValue(handle, "URL");
				registry.closeKey(handle);
				if(url != null){
					String[] urlParts = url.split(":");
					port = urlParts[2];
				}				
			} catch (Exception e) {
				throw e;
			}
		}
		
		return port;
		
	}
	
	private static String getApachePort() throws Exception{
		
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\WebServer";
		String port = null;
		
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String url=  registry.getValue(handle, "URL");
				registry.closeKey(handle);
				if(url != null){
					String[] urlParts = url.split(":");
					port = urlParts[2];
					if(port.contains("management")){
						port = port.split("/")[0];
					}
				}				
			} catch (Exception e) {
				throw e;
			}
		}
		
		return port;
		
	}
	
	
	private static String getTomcatPort() throws Exception{
		
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\WebServer";
		String port = null;
		
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				port =  registry.getValue(handle, "TomcatPort");
				registry.closeKey(handle);
			} catch (Exception e) {
				throw e;
			}
		}
		
		return port;
		
	}
	
	private static String getMessageServicePort() throws Exception{
		
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\MessageService";
		String port = null;
		
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				port =  registry.getValue(handle, "Port");
				registry.closeKey(handle);
			} catch (Exception e) {
				throw e;
			}
		}
		
		return port;
		
	}
	
	
	
	
	private static String getInstallationPath() throws Exception{
		
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\Console";
		String installPath = null;
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				installPath=  registry.getValue(handle, "Path");
				registry.closeKey(handle);
			} catch (Exception e) {
				throw e;
			}
		}
		
		return installPath;
		
	}
	
	public static void enableApacheHTTPS() throws Exception{
		
		String value = getInstallationPath();
		
		String httdFile = value + "Apache\\conf\\httpd.conf";
		String httdFileNew = value + "Apache\\conf\\httpd.conf.new";
		
		File fileObj = new File(httdFile);
		File fileNewObj = new File(httdFileNew);
		
		BufferedReader reader = new BufferedReader(new FileReader(fileObj));
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileNewObj));
		
		String line = null;
		boolean bHasError400 = false;
		while((line=reader.readLine()) != null){
			
			line = line.trim();
			
			if(line.contains("Include conf/extra/httpd-ssl.conf")){
				line = "Include conf/extra/httpd-ssl.conf ";
			}else if(line.startsWith("ProxyPass")){
				continue;
			} else if (line.contains("ErrorDocument 400")) {
				line = "ErrorDocument 400 /management/bad_request.html";
				bHasError400 = true;
			}
			
			writer.write(line + "\r\n");
		}	
		if (!bHasError400) {
			writer.write("ErrorDocument 400 /management/bad_request.html\r\n");
		}
		
		reader.close();
		writer.close();
		
		fileObj.delete();
		fileNewObj.renameTo(fileObj);
		
			
	}
	
	public static void enableApacheHTTP() throws Exception{
		
		String value = getInstallationPath();
		
		String httdFile = value + "Apache\\conf\\httpd.conf";
		String httdFileNew = value + "Apache\\conf\\httpd.conf.new";
		
		File fileObj = new File(httdFile);
		File fileNewObj = new File(httdFileNew);
		
		BufferedReader reader = new BufferedReader(new FileReader(fileObj));
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileNewObj));
		
		String line = null;
		while((line=reader.readLine()) != null){
			
			line = line.trim();
			
			if(line.contains("Include conf/extra/httpd-ssl.conf")){
				line = "#Include conf/extra/httpd-ssl.conf ";
			}
			
			writer.write(line + "\r\n");
		}	
		
		line = generateProxyPassForHTTP();
		
		writer.write(line);
		
		reader.close();
		writer.close();
		
		fileObj.delete();
		fileNewObj.renameTo(fileObj);
		
			
	}
	
	
	
}
