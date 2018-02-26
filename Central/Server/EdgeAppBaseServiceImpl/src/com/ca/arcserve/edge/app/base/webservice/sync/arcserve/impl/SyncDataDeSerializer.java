package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.ChangeRecord.ChangeActionType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;



//@SuppressWarnings("deprecation")
public class SyncDataDeSerializer {
	
	public   List<DeSerializeItem> m_dataList= new LinkedList<DeSerializeItem>();
    private  Timestamp m_timeBase;
    public   int m_rowCount;
    
	public SyncDataDeSerializer()
    {
    	m_timeBase = Timestamp.valueOf("1970-01-01 00:00:00");
    }
    
    private byte[] DeCodeBase64String(@NotPrintAttribute String str)
    {
    	BASE64Decoder base64Decoder = new BASE64Decoder();
    	byte[] buffer = null;
		try {
			buffer = base64Decoder.decodeBuffer(str);
		} catch (IOException e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		}
    	return buffer;
    }
    
    public Boolean DeSerializationRow(@NotPrintAttribute String str)
    {
    	ByteBuffer buffer = ByteBuffer.wrap(DeCodeBase64String(str));
    	if(buffer.array().length == 0 || buffer.getShort() != SerializationFlag.FILE_FLAG)
    		return false;
    	
    	int rowLength = this.m_rowCount = buffer.getInt();
    	for(int i = 0; i < rowLength; i++)
    	{
    		DeSerializeItem item = new DeSerializeItem();
    		item.m_typeFlag = buffer.get();
    		if(item.m_typeFlag == SerializationFlag.TC_BOOL){
    			item.m_data = new Boolean((buffer.get()&0x01)==1?true:false);
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_BYTE_ARRAY){
    			item.m_length = buffer.getInt();
    			item.m_data = new byte[item.m_length];
    			buffer.get((byte[])item.m_data, buffer.arrayOffset(), item.m_length);
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_DOUBLE){
    			double dValue = buffer.getDouble(); 
    			item.m_data = new Double(dValue);
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_FLOAT){
    			float fValue = buffer.getFloat();
    			item.m_data = new Float(fValue);
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_INT)
    		{
    			item.m_data = new Integer(buffer.getInt());
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_LONG)
    		{
    			item.m_data = new Long(buffer.getLong());
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_STRING 
    				|| item.m_typeFlag == SerializationFlag.TC_NSTRING)
    		{
    			item.m_length = buffer.getInt();
    			byte[] strBuffer = new byte[item.m_length];
    			buffer.get(strBuffer,buffer.arrayOffset(), item.m_length);
    			item.m_data = new String(strBuffer);
    		}
    		else if(item.m_typeFlag == SerializationFlag.TC_DATE)
    		{
    			long millSeconde = buffer.getLong();
    			millSeconde += m_timeBase.getTime();
    			item.m_data = new Timestamp(millSeconde);
    		}
    		else if(item.m_typeFlag != SerializationFlag.TC_NULL)
    		{
    			continue;
    		}
    		m_dataList.add(item);
    	}
    	
    	return true;
    }
    
    public ChangeRecord DeSerializationXmlRow(String str){
    	
    	ByteArrayInputStream streamXml = new ByteArrayInputStream(str.getBytes());
    	ChangeRecord changeRecords = new ChangeRecord();
    	
    	try {
    		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document docContents = docBuilder.parse(streamXml);
			
			NodeList nodes = docContents.getElementsByTagName("Table");
			for (int i = 0; i < nodes.getLength(); i++) {
				changeRecords.TableName = ConfigurationOperator._PrefixTableName
						+ nodes.item(i).getAttributes().getNamedItem(
								"tableName").getTextContent();
				changeRecords.ID = Integer.parseInt(nodes.item(i)
						.getAttributes().getNamedItem("ID").getTextContent());
				changeRecords.Type = ChangeActionType.valueOf(nodes.item(i)
						.getAttributes().getNamedItem("Type").getTextContent());
			}
			
			nodes = docContents.getElementsByTagName("row");
			for(int i = 0; i < nodes.getLength(); i ++)
			{
				NamedNodeMap rowNodes = nodes.item(i).getAttributes();
				for(int j = 0; j < rowNodes.getLength();j++)
				{
					changeRecords.Columns.add(rowNodes.item(j).getNodeName());
					changeRecords.Values.add(rowNodes.item(j).getNodeValue());
				}
			}
	
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		}
    	
    	return changeRecords;
    }

}
