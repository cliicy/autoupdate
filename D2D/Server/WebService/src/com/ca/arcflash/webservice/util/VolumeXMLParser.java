package com.ca.arcflash.webservice.util;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.xml.XMLBeanMapper;
import com.ca.arcflash.webservice.data.browse.Disk;
import com.ca.arcflash.webservice.data.browse.Volume;

public class VolumeXMLParser {
	private static XMLBeanMapper<Volume> volumeMapper;
	private static XMLBeanMapper<Disk> diskMapper;
	
	static {
		try {
			volumeMapper = new XMLBeanMapper<Volume>(Volume.class);
			diskMapper = new XMLBeanMapper<Disk>(Disk.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Volume[] parseXML(Document xmlDocument) throws Exception{
		List<Volume> result = new LinkedList<Volume>();
		
		NodeList applicationNodeList = xmlDocument.getElementsByTagName("VolumeInfo");
		
		for(int index = 0; index < applicationNodeList.getLength(); index++){
			Node node = applicationNodeList.item(index);
			Volume volume = null;
			volume = volumeMapper.loadBean(node);
			
			if (volume!=null){
				List<Disk> diskList = new LinkedList<Disk>();
				NodeList diskNodeList = node.getChildNodes();
				for(int diskIndex = 0; diskIndex < diskNodeList.getLength(); diskIndex++){
					Node diskNode = diskNodeList.item(diskIndex);
					if (diskNode.getNodeType() == Node.ELEMENT_NODE){
						Disk disk = diskMapper.loadBean(diskNode);
						if (disk!=null)
							diskList.add(disk);
					}
				}
				
				volume.setDisks(diskList);
				result.add(volume);
			}
		}
		return result.toArray(new Volume[0]);
	}
}
