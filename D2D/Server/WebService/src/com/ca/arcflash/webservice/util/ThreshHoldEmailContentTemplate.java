package com.ca.arcflash.webservice.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;


public class ThreshHoldEmailContentTemplate {
	
	private static final Logger logger = Logger
	.getLogger(ThreshHoldEmailContentTemplate.class);
	
	public static String getPlainTextContent(long unusedSpaceSize,long totalSpaceSize,
											 double threshHoldValveValue,Date executeTime, String URL){
		
		StringBuffer emailContent = new StringBuffer();
	
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Email_General_Description",
					ServiceContext.getInstance().getProductNameD2D()));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()));
		emailContent.append(": " + serverName);
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Total_Space"));
		emailContent.append(": " + (totalSpaceSize>>20));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Unused_Space"));
		emailContent.append(": " + (unusedSpaceSize>>20));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Unused_Space_In_Percentage"));
		emailContent.append(": " + calculateUnusedSpacePercentage(unusedSpaceSize,totalSpaceSize));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Email_Execution_Time"));
		emailContent.append(": " + executeTime);	
		//Alert Email PR
		emailContent.append("\n\n");
		String clickHere = WebServiceMessages.getResource("threshHoldClickHere_text",URL);
		emailContent.append(clickHere);
		
		return emailContent.toString();
	}
	
	public static String getHtmlContent(long unusedSpaceSize,long totalSpaceSize,
			 							double threshHoldValveValue,Date executeTime, String URL)
	{
		String template = "";
			
			//HTML format
		StringBuffer emailContent = new StringBuffer();
		emailContent.append("<HTML>");
		emailContent.append(getHTMLHeaderSection());
		emailContent.append("	<BODY>");
		emailContent.append("	<h1>%s</h1>");
		emailContent.append("   <p/><p/>");
		emailContent.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD COLSPAN=2><B>%s</B></TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("	</TABLE>");
			
		emailContent.append("<P/><P/>%s");
		emailContent.append("</BODY>");
		emailContent.append("</HTML>");
			
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		//Alert Email PR
		String clickHere = WebServiceMessages.getResource("threshHoldClickHere",URL);
		template = StringUtil.format(emailContent.toString(),
					WebServiceMessages.getResource("AlertEmailTitle", ServiceContext.getInstance().getProductNameD2D()), 
					WebServiceMessages.getResource("ThreshHold_Email_General_Description",
							ServiceContext.getInstance().getProductNameD2D()), 
					WebServiceMessages.getResource("EmailServerName", ServiceContext.getInstance().getProductNameD2D()), serverName,
					WebServiceMessages.getResource("ThreshHold_Total_Space"), (totalSpaceSize>>20),
					WebServiceMessages.getResource("ThreshHold_Unused_Space"), (unusedSpaceSize>>20), 
					WebServiceMessages.getResource("ThreshHold_Unused_Space_In_Percentage"), calculateUnusedSpacePercentage(unusedSpaceSize,totalSpaceSize), 
					WebServiceMessages.getResource("EmailExecutionTime"), BackupConverterUtil.dateToString(executeTime), clickHere);
			
		return template;
	}
	public static String getVSpherePlainTextContent(VMBackupConfiguration configuration,long unusedSpaceSize,long totalSpaceSize,
			double threshHoldValveValue,Date executeTime, String URL,List<VMBackupConfiguration> configList){
		
		StringBuffer VMs = new StringBuffer();
		for(VMBackupConfiguration config : configList){
			VMs.append(config.getBackupVM().getVmName()+",");
		}
		
		StringBuffer emailContent = new StringBuffer();
		
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Email_General_Description" ,
					ServiceContext.getInstance().getProductNameD2D()));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("VSphereEmailServerName"));
		emailContent.append(": " + serverName);
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("EmailVMName"));
		emailContent.append(": " + VMs.subSequence(0, VMs.length()-1));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Total_Space"));
		emailContent.append(": " + (totalSpaceSize>>20));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Unused_Space"));
		emailContent.append(": " + (unusedSpaceSize>>20));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Unused_Space_In_Percentage"));
		emailContent.append(": " + calculateUnusedSpacePercentage(unusedSpaceSize,totalSpaceSize));
		emailContent.append("\n");
		emailContent.append(WebServiceMessages.getResource("ThreshHold_Email_Execution_Time"));
		emailContent.append(": " + executeTime);	
		//Alert Email PR
		emailContent.append("\n\n");
		String clickHere = WebServiceMessages.getResource("threshHoldClickHere_text",URL);
		emailContent.append(clickHere);
		
		return emailContent.toString();
	}
	
	public static String getVSphereHtmlContent(VMBackupConfiguration configuration,long unusedSpaceSize,long totalSpaceSize,
			double threshHoldValveValue,Date executeTime, String URL,List<VMBackupConfiguration> configList)
	{
		StringBuffer VMs = new StringBuffer();
		for(VMBackupConfiguration config : configList){
			VMs.append(config.getBackupVM().getVmName()+",");
		}
		
		String template = "";
		
		//HTML format
		StringBuffer emailContent = new StringBuffer();
		emailContent.append("<HTML>");
		emailContent.append(getHTMLHeaderSection());
		emailContent.append("	<BODY>");
		emailContent.append("	<h1>%s</h1>");
		emailContent.append("   <p/><p/>");
		emailContent.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD COLSPAN=2><B>%s</B></TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		emailContent.append("	</TABLE>");
		
		emailContent.append("<P/><P/>%s");
		emailContent.append("</BODY>");
		emailContent.append("</HTML>");
		
		String serverName = ServiceContext.getInstance().getLocalMachineName();
		//Alert Email PR
		String clickHere = WebServiceMessages.getResource("threshHoldClickHere",URL);
		template = StringUtil.format(emailContent.toString(),
				WebServiceMessages.getResource("VSphereAlertEmailTitle"), 
				WebServiceMessages.getResource("ThreshHold_Email_General_Description",
							ServiceContext.getInstance().getProductNameD2D()), 
				WebServiceMessages.getResource("VSphereEmailServerName"), serverName,
				WebServiceMessages.getResource("EmailVMName"), VMs.subSequence(0, VMs.length()-1), 
				WebServiceMessages.getResource("ThreshHold_Total_Space"), (totalSpaceSize>>20),
				WebServiceMessages.getResource("ThreshHold_Unused_Space"), (unusedSpaceSize>>20), 
				WebServiceMessages.getResource("ThreshHold_Unused_Space_In_Percentage"), calculateUnusedSpacePercentage(unusedSpaceSize,totalSpaceSize), 
				WebServiceMessages.getResource("EmailExecutionTime"), BackupConverterUtil.dateToString(executeTime), clickHere);
		
		return template;
	}
	
	private static String getHTMLHeaderSection()
	{
		StringBuffer headerSection = new StringBuffer();
		headerSection.append("<head>");
		headerSection.append("<title></title>");
		headerSection.append("<style type=\"text/css\">");
	
		headerSection.append("body, p, th, td, h1 {	font-family: Verdana, Arial; font-size: 8.5pt; }");
		headerSection.append("h1 { font-size: 14pt;	}");
		headerSection.append(".data_table { border-width: 1px; border-style: solid; border-color: #000000; border-collapse: collapse; }");
	
		headerSection.append("</style>");
		headerSection.append("</head>");
	
		return headerSection.toString();
	}
	
	private static String calculateUnusedSpacePercentage(long unusedSpaceSize,long totalSpaceSize){
		if (totalSpaceSize <=0)
			return "0%";
		double unusedPercentage = (((double)(unusedSpaceSize>>20))/(totalSpaceSize>>20));
		unusedPercentage = new BigDecimal(unusedPercentage*100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return unusedPercentage+"%";
	}
}
