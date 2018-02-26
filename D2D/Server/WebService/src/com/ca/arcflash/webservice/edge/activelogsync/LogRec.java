package com.ca.arcflash.webservice.edge.activelogsync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogRec", propOrder = {
	"oper",
    "uniqueID",
    "strTime",
    "flags",
    "jobNo",
    "strLog"
})

@XmlRootElement(name = "LogRec")
public class LogRec {
	@XmlElement(name = "Oper", required = true)
    protected String oper;
    
	@XmlElement(name = "UniqueID", required = true)
    protected String uniqueID;
	
    @XmlElement(name = "StrTime", required = true)
    protected String strTime;
    
    @XmlElement(name = "Flags", required = true)
    protected long flags;
    
    @XmlElement(name = "JobNo", required = true)
    protected long jobNo;
    
    @XmlElement(name = "StrLog", required = true)
    protected String strLog;
    
	public void setOper(String value)
	{
		this.oper = value;
	}
	
	public String getOper()
	{
		return oper;
	}
	
	public void setUniqueID(String value)
	{
		this.uniqueID = value;
	}
	
	public String getUniqueID()
	{
		return uniqueID;
	}
	
	public void setStrTime(String value)
	{
		this.strTime = value;
	}
	
	public String getStrTime()
	{
		return strTime;
	}
	
	public void setFlags(long value)
	{
		this.flags = value;
	}
	
	public long getFlags()
	{
		return flags;
	}
	
	public void setJobNo(long value)
	{
		this.jobNo = value;
	}
	
	public long getJobNo()
	{
		return jobNo;
	}
	
	public void setStrLog(String value)
	{
		this.strLog = value;
	}
	
	public String getStrLog()
	{
		return strLog;
	}
}
