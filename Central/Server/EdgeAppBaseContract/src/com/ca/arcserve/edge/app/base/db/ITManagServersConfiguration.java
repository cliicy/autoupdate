package com.ca.arcserve.edge.app.base.db;


import java.io.File;



import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ca.arcserve.edge.app.base.cm.CentralManagerInfo;
import com.ca.arcserve.edge.app.base.dao.ServersConfiguration;
import com.ca.arcserve.edge.app.base.jaxbadapter.CDataAdapter;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import javax.xml.datatype.*;

@XmlRootElement(name = "ITManagServersConfiguration")
public class ITManagServersConfiguration {	
	
	public static final String SERVER_CONFIGURATION_FILE = "Servers_configuration.xml";
	private static ITManagServersConfiguration info = null;
	private ITManagServersConfiguration()
	{
		
	}
    
	private  List<ServersConfiguration> server = new ArrayList<ServersConfiguration>();
	

	public void setServer(List<ServersConfiguration> server) 
	{
		this.server = server;
	}
	
	@XmlElementWrapper(name="Servers")
	public List<ServersConfiguration> getServer()
	{
		return server ;
	}
	
	
	public synchronized void saveConfiguration(String filepath)throws Exception 
	{
	     String marshal = CommonUtil.marshal(this);
	     CommonUtil.saveStringToFile(marshal, filepath);


           }
	
	
	public synchronized ITManagServersConfiguration LoadConfiguartions(String filePath)
	throws Exception
	{		
	
		//ITManagServersConfiguration info = null;
		if (info != null) 
		{
			File f = new File(filePath);

			if (f.exists()) 
			{
				String readFileAsString = CommonUtil.readFileAsString(filePath);

				if (!StringUtil.isEmptyOrNull(readFileAsString)) 
				{
					info = JAXB.unmarshal(new StringReader(readFileAsString),
							ITManagServersConfiguration.class);

				}
			}
			else
			{
				this.server.clear();
				info.setServer(server);				
			
			}
			return info;
		}
		else
		{
			return getInstance();
		}

	
	}
	
	public static synchronized ITManagServersConfiguration getInstance()
	{
		if(info == null)
		{
			info = new ITManagServersConfiguration();
			
		}
		return info;
}
}



