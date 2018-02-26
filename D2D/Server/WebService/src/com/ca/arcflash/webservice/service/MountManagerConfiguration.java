package com.ca.arcflash.webservice.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;


import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.ca.arcflash.webservice.data.MountNode;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;

@XmlRootElement(name = "MountManagerConfiguration")
public class MountManagerConfiguration {
	public static final String MOUNT_CONFIGURATION_FILE = "mount_configuration.xml";
	private static MountManagerConfiguration info = null;
	
	private MountManagerConfiguration()
	{
		
	}
	
	private  List<MountNode> node = new ArrayList<MountNode>();
	
	public void setNode(List<MountNode> node) 
	{
		this.node = node;
	}
	
	@XmlElementWrapper(name="Nodes")
	public List<MountNode> getNode()
	{
		return node ;
	}
	
	public synchronized void saveConfiguration(String filepath)throws Exception 
	{
		 
	     String marshal = CommonUtil.marshal(this);
	     saveStringToFile(marshal, filepath);
    }
	
	private static void saveStringToFile(String source, String filePath)
	throws Exception {
	BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bos.write(source.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null)
				bos.close();
		}
	}
	
	public synchronized MountManagerConfiguration LoadConfiguartions(String filePath)
	throws Exception
	{		
		if (info != null) 
		{
			File f = new File(filePath);

			if (f.exists()) 
			{
				String readFileAsString = CommonUtil.readFileAsString(filePath);
				if (!StringUtil.isEmptyOrNull(readFileAsString)) 
				{
					info = JAXB.unmarshal(new StringReader(readFileAsString),
							MountManagerConfiguration.class);

				}
			}
			else
			{
				this.node.clear();
				info.setNode(node);				
			
			}
			return info;
		}
		else
		{
			return getInstance();
		}

	
	}
	
	public static synchronized MountManagerConfiguration getInstance()
	{
		if(info == null)
		{
			info = new MountManagerConfiguration();
			
		}
		return info;
	}
	
	

}
