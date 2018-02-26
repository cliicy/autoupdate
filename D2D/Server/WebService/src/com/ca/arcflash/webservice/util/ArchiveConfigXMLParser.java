package com.ca.arcflash.webservice.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.xml.XMLXPathReader;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceFiltersConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;

public class ArchiveConfigXMLParser {
	
	private static final Logger logger = Logger.getLogger(ArchiveConfigXMLParser.class);

	private final int OPERATOR_LESSTHAN = 0;
	private final int OPERATOR_GREATERTHAN = 1;
	private final int OPERATOR_BETWEEN = 2;
	
	private final String FILTER_TYPE_INCLUDE = "0";
	private final String FILTER_TYPE_EXCLUDE = "1";
	
	private ArchiveConfigUtils archiveConfigUtils = new ArchiveConfigUtils();
	
	public ArchiveConfigXMLParser()
	{
	}

	public Document saveXML(ArchiveConfiguration archiveConfig) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document m_archiveXMLdoc = db.newDocument();

		if(m_archiveXMLdoc == null)
			return null;

        Element RootElement = m_archiveXMLdoc.createElement("ArchiveConfig");
        m_archiveXMLdoc.appendChild(RootElement);

        saveArchivePolicies(m_archiveXMLdoc, RootElement,archiveConfig.getArchiveSources());

        Element DestElement = null;
        if(archiveConfig.isbArchiveToDrive())
        {
        	DestElement = m_archiveXMLdoc.createElement("Destination");
        	DestElement.setAttribute("DestType", "4");
        	String strArchiveDestination = archiveConfig.getStrArchiveToDrivePath();
        	DestElement.setAttribute("Path", strArchiveDestination);

        	Element DestUserName = m_archiveXMLdoc.createElement("UserName");
        	DestUserName.setTextContent(archiveConfig.getStrArchiveDestinationUserName());
        	DestElement.appendChild(DestUserName);
        	Element DestPassword = m_archiveXMLdoc.createElement("Password");
        	String strPassword = archiveConfig.getStrArchiveDestinationPassword();
        	if(strPassword != null && strPassword.length() != 0)
        	{
        		DestPassword.setTextContent(strPassword != null ? CommonService.getInstance().getNativeFacade().encrypt(strPassword) : null);
        	}
        	DestElement.appendChild(DestPassword);

        }

        if(archiveConfig.isbArchiveToCloud())
        {
        	DestElement = m_archiveXMLdoc.createElement("Destination");
        	DestElement.setAttribute("DestType", "Cloud");
        	DestElement.setAttribute("Path", "");

        	ArchiveCloudDestInfo cloudConfig =  archiveConfig.getCloudConfig();

			if(cloudConfig != null)
			{
				DestElement = m_archiveXMLdoc.createElement("Destination");
				DestElement.setAttribute("DestType", cloudConfig.getcloudVendorType()+"");
				DestElement.setAttribute("Path", "");
				Element cloudVendorType = m_archiveXMLdoc.createElement("cloudVendorType");
				cloudVendorType.setTextContent(Long.toString(cloudConfig.getcloudVendorType()));
				DestElement.appendChild(cloudVendorType);
				
				Element cloudSubVendorType = m_archiveXMLdoc.createElement("cloudSubVendorType");
				cloudSubVendorType.setTextContent(Long.toString(cloudConfig.getCloudSubVendorType()));
				DestElement.appendChild(cloudSubVendorType);
				
				Element cloudVendorURL = m_archiveXMLdoc.createElement("VendorURL");
				cloudVendorURL.setTextContent(cloudConfig.getcloudVendorURL());
				DestElement.appendChild(cloudVendorURL);
				Element cloudVendorUsername = m_archiveXMLdoc.createElement("AccessKey");
				cloudVendorUsername.setTextContent(cloudConfig.getcloudVendorUserName());
				DestElement.appendChild(cloudVendorUsername);
				Element cloudVendorPassword = m_archiveXMLdoc.createElement("SecretKey");
				cloudVendorPassword.setTextContent(CommonService.getInstance().getNativeFacade().encrypt(cloudConfig.getcloudVendorPassword()));
				DestElement.appendChild(cloudVendorPassword);
				
	        	//rrsflag
				Element RrsFlag =  m_archiveXMLdoc.createElement("CloudDestinationProperty");
	        	RrsFlag.setTextContent(cloudConfig.getRRSFlag()+"");
				DestElement.appendChild(RrsFlag);

/*	        	Element cloudVendorCertificatePath= m_archiveXMLdoc.createElement("CertificatePath");
	        	cloudVendorCertificatePath.setTextContent(cloudConfig.getVendorCertificatePath());
	        	DestElement.appendChild(cloudVendorCertificatePath);
	        	Element cloudCertificatePassword = m_archiveXMLdoc.createElement("CertificatePassword");
	        	cloudCertificatePassword.setTextContent(CommonService.getInstance().getNativeFacade().encrypt(cloudConfig.getCertificatePassword()));
	        	DestElement.appendChild(cloudCertificatePassword);*/

	        	/*Element cloudVendorHostname= m_archiveXMLdoc.createElement("VendorHostname");
	        	cloudVendorHostname.setTextContent(cloudConfig.getVendorHostname());
	        	DestElement.appendChild(cloudVendorHostname);
	        	Element cloudVerdorPort = m_archiveXMLdoc.createElement("VendorPort");
	        	cloudVerdorPort.setTextContent(Integer.toString(cloudConfig.getVendorPort()));
	        	DestElement.appendChild(cloudVerdorPort);*/

	        	Element bucketName = m_archiveXMLdoc.createElement("BucketDisplayName");
	        	bucketName.setTextContent(cloudConfig.getcloudBucketName());
	        	DestElement.appendChild(bucketName);
	        	
	        	Element cloudBucketName = m_archiveXMLdoc.createElement("BucketName");
	        	if(cloudConfig.getEncodedCloudBucketName() != null && cloudConfig.getEncodedCloudBucketName().length() > 0)
	        	{
	        	cloudBucketName.setTextContent(cloudConfig.getEncodedCloudBucketName());
	        		
	        	}else   //migration scenario     		
	        	{
	        		cloudBucketName.setTextContent(cloudConfig.getcloudBucketName());	
	        	}
	        	DestElement.appendChild(cloudBucketName);
	        	
	        	Element cloudBucketRegionName = m_archiveXMLdoc.createElement("BucketRegionName");
	        	cloudBucketRegionName.setTextContent(cloudConfig.getcloudBucketRegionName());
	        	DestElement.appendChild(cloudBucketRegionName);

	        	//Proxy Details
	        	Element ProxyDetail = m_archiveXMLdoc.createElement("Proxy");
	        	ProxyDetail.setAttribute("Enabled", Boolean.toString(cloudConfig.iscloudUseProxy()));
	        	ProxyDetail.setAttribute("RequiresAuth", Boolean.toString(cloudConfig.iscloudProxyRequireAuth()));
	        	if(cloudConfig.iscloudUseProxy())
	        	{
	        		Element ProxyServerName = m_archiveXMLdoc.createElement("ServerName");
	        		ProxyServerName.setTextContent(cloudConfig.getcloudProxyServerName());
	        		ProxyDetail.appendChild(ProxyServerName);

	        		Element ProxyPort = m_archiveXMLdoc.createElement("Port");
	        		ProxyPort.setTextContent(Long.toString(cloudConfig.getcloudProxyPort()));
	        		ProxyDetail.appendChild(ProxyPort);

	        		if(cloudConfig.iscloudProxyRequireAuth())
	        		{
	        			Element ProxyUserName = m_archiveXMLdoc.createElement("UserName");
	        			ProxyUserName.setTextContent(cloudConfig.getcloudProxyUserName());
		        		ProxyDetail.appendChild(ProxyUserName);

		        		Element ProxyPassword = m_archiveXMLdoc.createElement("Password");
		        		ProxyPassword.setTextContent(CommonService.getInstance().getNativeFacade().encrypt(cloudConfig.getcloudProxyPassword()));
		        		ProxyDetail.appendChild(ProxyPassword);
	        		}
	        	}
	        	DestElement.appendChild(ProxyDetail);
        	}
        }

        if(DestElement != null)
        {
        	Element CompressionLevel = m_archiveXMLdoc.createElement("CompressionLevel");
        	CompressionLevel.setTextContent(Integer.toString(archiveConfig.getCompressionLevel()));
        	DestElement.appendChild(CompressionLevel);
        	//DestElement.setAttribute("FileVersionsRetentionCount", Integer.toString(archiveConfig.getArchivedFileVersionsRetentionCount()));

        	Element Encryption = m_archiveXMLdoc.createElement("Encryption");
        	Encryption.setAttribute("Enabled", Boolean.toString(archiveConfig.isbEncryption()));
        	if(archiveConfig.isbEncryption())
        		Encryption.setTextContent(archiveConfig.getEncryptionPassword() != null ? CommonService.getInstance().getNativeFacade().encrypt(archiveConfig.getEncryptionPassword()) : null);
        	DestElement.appendChild(Encryption);

        	Element MaxFileVersions = m_archiveXMLdoc.createElement("MaxFileVersions");
        	MaxFileVersions.setTextContent(Integer.toString(archiveConfig.getFileVersionRetentionCount()));
        	DestElement.appendChild(MaxFileVersions);

        	Element DestPurgePolicy = m_archiveXMLdoc.createElement("PurgePolicy");
        	Element Include = m_archiveXMLdoc.createElement("Include");

        	Element DestinationSpaceUtilization = m_archiveXMLdoc.createElement("SpaceUtilizationIndicator");
        	DestinationSpaceUtilization.setTextContent(Integer.toString(archiveConfig.getiSpaceUtilization()));
        	DestElement.appendChild(DestinationSpaceUtilization);

        	Element DestRetentionTime = m_archiveXMLdoc.createElement("RetentionTime");
        	DestRetentionTime.setTextContent(archiveConfig.getRetentiontime());
        	Include.appendChild(DestRetentionTime);
        	DestPurgePolicy.appendChild(Include);
        	DestElement.appendChild(DestPurgePolicy);
        	
        	Element FilesRetentionTime = m_archiveXMLdoc.createElement("FilesRetentionTime");
        	FilesRetentionTime.setTextContent(archiveConfig.getFilesRetentionTime());
        	DestElement.appendChild(FilesRetentionTime);
        	
        	DestElement.setAttribute("CatalogPath", archiveConfig.getStrCatalogPath() == null ? "" : archiveConfig.getStrCatalogPath());

        	RootElement.appendChild(DestElement);
        }

        //Scheduling
        boolean bArchiveSchedule = archiveConfig.isbArchiveAfterBackup();

        Element Schedule = m_archiveXMLdoc.createElement("Schedule");
        Schedule.setAttribute("Mode", StringUtil.isEmptyOrNull(archiveConfig.getStrScheduleMode()) ? ArchiveConfigurationConstants.SCHEDULE_MODE_SIMPLE : archiveConfig.getStrScheduleMode());
    	Element ArchiveSchedule = m_archiveXMLdoc.createElement("ArchiveSchedule");
    	ArchiveSchedule.setAttribute("Enabled", Boolean.toString(bArchiveSchedule));
    	ArchiveSchedule.setAttribute("AfterNBackups", Integer.toString(archiveConfig.getiArchiveAfterNBackups()));
    	Schedule.appendChild(ArchiveSchedule);

    	Element PurgeSchedule = m_archiveXMLdoc.createElement("PurgeSchedule");
    	PurgeSchedule.setAttribute("Enabled", Boolean.toString(archiveConfig.isbPurgeArchiveItems()));
    	PurgeSchedule.setAttribute("Interval", Integer.toString(archiveConfig.getiPurgeAfterDays()));
    	PurgeSchedule.setAttribute("StartTime", Long.toString(archiveConfig.getlPurgeStartTime()));
    	Schedule.appendChild(PurgeSchedule);   	
    	
    	if(ArchiveConfigurationConstants.SCHEDULE_MODE_ADVANCED.equalsIgnoreCase(archiveConfig.getStrScheduleMode())){
    		Element Daily = m_archiveXMLdoc.createElement("daily");
        	Daily.setTextContent(Boolean.toString(archiveConfig.isbDailyBackup()));
        	Element Weekly = m_archiveXMLdoc.createElement("weekly");
        	Weekly.setTextContent(Boolean.toString(archiveConfig.isbWeeklyBackup()));
        	Element Monthly = m_archiveXMLdoc.createElement("monthly");
        	Monthly.setTextContent(Boolean.toString(archiveConfig.isbMonthlyBackup()));
        	Element RecoveryPointToCopy = m_archiveXMLdoc.createElement("RecoveryPointToCopy");
        	RecoveryPointToCopy.appendChild(Daily);
        	RecoveryPointToCopy.appendChild(Weekly);
        	RecoveryPointToCopy.appendChild(Monthly);
        	Schedule.appendChild(RecoveryPointToCopy);
    	}
    	RootElement.appendChild(Schedule);

    	//Add AdvanceSchedule
    	if(ArchiveService.getInstance().hasEnabledAdvSchedule(archiveConfig.getAdvanceSchedule())){
    		Element advScheduleElement = AdvanceScheduleXMLParser.getElement(archiveConfig.getAdvanceSchedule(), m_archiveXMLdoc);
    		RootElement.appendChild(advScheduleElement);
    	}
    	
		return m_archiveXMLdoc;
	}

	private boolean saveArchivePolicies(Document m_archiveXMLdoc, Element rootElement,
			ArchiveSourceInfoConfiguration[] in_archiveSourceConfig) throws Exception
	{
		ArchiveSourceInfoConfiguration[] archiveConfigSourceList = in_archiveSourceConfig;
		
		int iSourceCount;
		
		if(archiveConfigSourceList==null)
			iSourceCount = 0;
		else 
			iSourceCount = archiveConfigSourceList.length;
		
        for(int iSourceIndex = 0;iSourceIndex<iSourceCount;iSourceIndex++)
        {
        	ArchiveSourceInfoConfiguration archiveSourceConfig = archiveConfigSourceList[iSourceIndex];
        	Element Policy = null;

        	if(archiveSourceConfig.isbArchiveFiles())
        	{
        		Policy = m_archiveXMLdoc.createElement("ArchivePolicy");
        	}
        	else if(archiveSourceConfig.isbCopyFiles())
        	{
        		Policy = m_archiveXMLdoc.createElement("FileCopyPolicy");
        	}

        	Policy.setAttribute("Id", Integer.toString(iSourceIndex));
        	Policy.setAttribute("Source", archiveSourceConfig.getStrSourcePath());
        	Policy.setAttribute("DisplaySource", archiveSourceConfig.getStrDisplaySourcePath());

        	Element IncludeFilters = m_archiveXMLdoc.createElement("Includes");
        	Element ExcludeFilters = m_archiveXMLdoc.createElement("Excludes");

        	ArchiveSourceFiltersConfiguration[] archiveSourceFiltersList = null;
        	archiveSourceFiltersList = archiveSourceConfig.getArchiveSourceFiltersConfig();

        	if(archiveSourceFiltersList != null)
        	{
	            int iSourceFiltersCount = archiveSourceFiltersList.length;

	//            File Filters
	            int iCriteria = 1;
	            for(int iSourceFiltersIndex = 0;iSourceFiltersIndex<iSourceFiltersCount;iSourceFiltersIndex++)
	            {
	            	ArchiveSourceFiltersConfiguration archiveFilterOrCriteria = archiveSourceFiltersList[iSourceFiltersIndex];

	            	Element FilterOrCriteriaElement = null;

	            	if(archiveFilterOrCriteria.isIsCriteria() == false)
	            	{
	            		switch(Integer.parseInt(archiveFilterOrCriteria.getFilterOrCriteriaName()))
        				{
	            		case 0:// file pattern
	            			FilterOrCriteriaElement = m_archiveXMLdoc.createElement("FileFilter");
	            			break;
	            		case 1://folder pattern
	            			FilterOrCriteriaElement = m_archiveXMLdoc.createElement("DirectoryFilter");
	            			break;
        				}
	            		
	            		String strFilterValue = archiveFilterOrCriteria.getFilterOrCriteriaLowerValue();
	            		String strFilterName =  "";
	            		String strFilterContent = archiveFilterOrCriteria.getFilterOrCriteriaLowerValue();
	            		
	            		if(archiveFilterOrCriteria.isIsDefaultFilter())
	            		{
		            		
		            		int iFilterLength = strFilterValue.length();
		            		
		            		
		            		int iOpenBraceIndex = strFilterValue.indexOf("(");
		            		if(iOpenBraceIndex != -1)
		            		{
		            			strFilterName = strFilterValue.substring(0, iOpenBraceIndex);
		            			strFilterContent = strFilterValue.substring(iOpenBraceIndex + 1,iFilterLength -1);
		            		}
	            		}
	            		if(strFilterName.length() != 0)
	            		{
	            			FilterOrCriteriaElement.setAttribute("Name", strFilterName);
	            		}

	            		FilterOrCriteriaElement.setAttribute("Pattern", strFilterContent);
	            		FilterOrCriteriaElement.setAttribute("LocalizedPattern", archiveFilterOrCriteria.getLocFilterOrCriteriaLowerValue());
	            	}
	            	else
	            	{
	            		logger.info("creating criteria element");
	            		FilterOrCriteriaElement = m_archiveXMLdoc.createElement("Criteria");
	            		FilterOrCriteriaElement.setAttribute("Id", Integer.toString(iCriteria));
	            		FilterOrCriteriaElement.setAttribute("Type", archiveFilterOrCriteria.getFilterOrCriteriaName());
	            		FilterOrCriteriaElement.setAttribute("Condition", archiveFilterOrCriteria.getCriteriaOperator());
	            		logger.info("Condition " + archiveFilterOrCriteria.getCriteriaOperator());

		            	//Element CriteriaValues = m_archiveXMLdoc.createElement("CriteriaValues");

		            	Element CriteriaLowerValue = m_archiveXMLdoc.createElement("CriteriaValue");
		            	CriteriaLowerValue.setTextContent(archiveFilterOrCriteria.getFilterOrCriteriaLowerValue());
		            	CriteriaLowerValue.setAttribute("Id", Integer.toString(1));
		            	FilterOrCriteriaElement.appendChild(CriteriaLowerValue);
		            	logger.info("between value "+Integer.toString(OPERATOR_BETWEEN)+ "\thigher value " + archiveFilterOrCriteria.getFilterOrCriteriaHigherValue());
	            		if(archiveFilterOrCriteria.getCriteriaOperator().compareToIgnoreCase(Integer.toString(OPERATOR_BETWEEN)) == 0)
	            		{
	            			Element CriteriaHigherValue = m_archiveXMLdoc.createElement("CriteriaValue");
	            			CriteriaHigherValue.setTextContent(archiveFilterOrCriteria.getFilterOrCriteriaHigherValue());
	            			CriteriaHigherValue.setAttribute("Id", Integer.toString(2));
	            			FilterOrCriteriaElement.appendChild(CriteriaHigherValue);
	            		}
	            		iCriteria++;
	            	}

	        		if(archiveFilterOrCriteria.getFilterOrCriteriaType().compareToIgnoreCase(FILTER_TYPE_INCLUDE) == 0)
	            	{//include file filters
	        			IncludeFilters.appendChild(FilterOrCriteriaElement);
	            	}
	            	else if(archiveFilterOrCriteria.getFilterOrCriteriaType().compareToIgnoreCase(FILTER_TYPE_EXCLUDE) == 0)
	            	{//exclude file filters
	            		ExcludeFilters.appendChild(FilterOrCriteriaElement);
	            	}
	            }
        	}

            Policy.appendChild(IncludeFilters);
            Policy.appendChild(ExcludeFilters);
            rootElement.appendChild(Policy);
        }
        return true;
	}
	
	public ArchiveConfiguration loadXML( Document xmlDocument )
	{
		XMLXPathReader archiveXMLXPathReader = new XMLXPathReader(xmlDocument);
		archiveXMLXPathReader.Initialise();

		return loadXML( archiveXMLXPathReader );
	}
	
	public ArchiveConfiguration loadXML(String in_archiveConfigurationFilePath)
	{
		XMLXPathReader archiveXMLXPathReader = new XMLXPathReader(in_archiveConfigurationFilePath);
		archiveXMLXPathReader.Initialise();

		return loadXML( archiveXMLXPathReader );
	}
	
	private ArchiveConfiguration loadXML(XMLXPathReader archiveXMLXPathReader)
	{
		ArchiveConfiguration archiveConfig = new ArchiveConfiguration();
		
		String sXPathArchivePolicy = "/ArchiveConfig/ArchivePolicy";
		NodeList ArchivePolicies = (NodeList) archiveXMLXPathReader.readXPath(sXPathArchivePolicy,XPathConstants.NODESET);
		int iArchivePolicies = ArchivePolicies.getLength();

		String sXPathFileCopyPolicy = "/ArchiveConfig/FileCopyPolicy";
		NodeList FileCopyPolicies = (NodeList) archiveXMLXPathReader.readXPath(sXPathFileCopyPolicy,XPathConstants.NODESET);
		int iFileCopyPolicies = FileCopyPolicies.getLength();

		ArchiveSourceInfoConfiguration[] archiveSources = new ArchiveSourceInfoConfiguration[iArchivePolicies+iFileCopyPolicies];
		int iPolicyIndex = 0;
		for(iPolicyIndex = 0; iPolicyIndex < iArchivePolicies;iPolicyIndex++)
		{
			ArchiveSourceInfoConfiguration sourceInfoConfig = new ArchiveSourceInfoConfiguration();
			Node archivePolicy = ArchivePolicies.item(iPolicyIndex);
			Node sourceAttr = archivePolicy.getAttributes().getNamedItem("Source");
			if(sourceAttr != null)
			{
				sourceInfoConfig.setStrSourcePath(sourceAttr.getNodeValue());
			}
			Node displaySourceAttr = archivePolicy.getAttributes().getNamedItem("DisplaySource");
			if(displaySourceAttr != null)
			{
				sourceInfoConfig.setStrDisplaySourcePath(displaySourceAttr.getNodeValue());
			}
			sourceInfoConfig.setbArchiveFiles(true);

			Node ArchiveIndexNode = archivePolicy.getAttributes().getNamedItem("Id");
			int iArchivePolicyIndex = Integer.parseInt(ArchiveIndexNode.getNodeValue());

			String sXPathArchive = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			Node ArchivePolicy = (Node) archiveXMLXPathReader.readXPath(sXPathArchivePolicy,XPathConstants.NODE);
			if(ArchivePolicy != null)
			{
				boolean bArchivePolicy = true;
			}

			String sXPathPolicyIncludeFileFilters = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']/Includes/FileFilter",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList IncludeFileFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyIncludeFileFilters,XPathConstants.NODESET);
			int iIncludeFileFiltersCount = 0;
			iIncludeFileFiltersCount = IncludeFileFilters.getLength();

			String sXPathPolicyIncludeFolderFilters = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']/Includes/DirectoryFilter",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList IncludeFolderFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyIncludeFolderFilters,XPathConstants.NODESET);
			int iIncludeFolderFiltersCount = 0;
			iIncludeFolderFiltersCount = IncludeFolderFilters.getLength();

			String sXPathPolicyExcludeFileFilters = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']/Excludes/FileFilter",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList ExcludeFileFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyExcludeFileFilters,XPathConstants.NODESET);
			int iExcludeFileFiltersCount = 0;
			iExcludeFileFiltersCount = ExcludeFileFilters.getLength();

			String sXPathPolicyExcludeFolderFilters = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']/Excludes/DirectoryFilter",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList ExcludeFolderFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyExcludeFolderFilters,XPathConstants.NODESET);
			int iExcludeFolderFiltersCount = 0;
			iExcludeFolderFiltersCount = ExcludeFolderFilters.getLength();

			String sXPathPolicyIncludeCriteria = StringUtil.enFormat("/ArchiveConfig/ArchivePolicy[@Id='%d' and @Source='%s']/Includes/Criteria",iArchivePolicyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList IncludeCriteria = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyIncludeCriteria,XPathConstants.NODESET);
			int iIncludeCriteriaCount = 0;
			iIncludeCriteriaCount = IncludeCriteria.getLength();

			int iFiltersCount = iExcludeFileFiltersCount + iExcludeFolderFiltersCount + iIncludeFileFiltersCount + iIncludeFolderFiltersCount + iIncludeCriteriaCount;

			ArchiveSourceFiltersConfiguration[] sourceFilters = new ArchiveSourceFiltersConfiguration[iFiltersCount];// allocating 1 less because lastaccess time is not required to be in filters list.

			int iFiltersCounter = 0;
			for(int iExcludeFileFilterIndex = 0;iExcludeFileFilterIndex < iExcludeFileFiltersCount;iExcludeFileFilterIndex++)
			{
				Node ExcludeFileFilter = ExcludeFileFilters.item(iExcludeFileFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_EXCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("0");

				String strActualFilterValue = ExcludeFileFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(ExcludeFileFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = ExcludeFileFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}

				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iExcludeFolderFilterIndex = 0;iExcludeFolderFilterIndex < iExcludeFolderFiltersCount;iExcludeFolderFilterIndex++)
			{
				Node ExcludeFolderFilter = ExcludeFolderFilters.item(iExcludeFolderFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_EXCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("1");

				String strActualFilterValue = ExcludeFolderFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(ExcludeFolderFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = ExcludeFolderFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}

				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iIncludeFileFilterIndex = 0;iIncludeFileFilterIndex < iIncludeFileFiltersCount;iIncludeFileFilterIndex++)
			{
				Node IncludeFileFilter = IncludeFileFilters.item(iIncludeFileFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_INCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("0");

				String strActualFilterValue = IncludeFileFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(IncludeFileFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = IncludeFileFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iIncludeFolderFilterIndex = 0;iIncludeFolderFilterIndex < iIncludeFolderFiltersCount;iIncludeFolderFilterIndex++)
			{
				Node IncludeFolderFilter = IncludeFolderFilters.item(iIncludeFolderFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_INCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("1");

				String strActualFilterValue = IncludeFolderFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(IncludeFolderFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = IncludeFolderFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFiltersConfig.setIsCriteria(false);
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iIncludeCriteriaIndex = 0;iIncludeCriteriaIndex < iIncludeCriteriaCount;iIncludeCriteriaIndex++)
			{
				Node IncludeCriteriaNode = IncludeCriteria.item(iIncludeCriteriaIndex);
				ArchiveSourceFiltersConfiguration sourceCriteriaConfig = new ArchiveSourceFiltersConfiguration();
				sourceCriteriaConfig.setFilterOrCriteriaType(FILTER_TYPE_INCLUDE);
				sourceCriteriaConfig.setCriteriaOperator(IncludeCriteriaNode.getAttributes().getNamedItem("Condition").getNodeValue());
				sourceCriteriaConfig.setIsCriteria(true);

				String strCriteriaID = IncludeCriteriaNode.getAttributes().getNamedItem("Id").getNodeValue();

				sourceCriteriaConfig.setFilterOrCriteriaName(IncludeCriteriaNode.getAttributes().getNamedItem("Type").getNodeValue());

				NodeList IncludeCriteriaValuesList = IncludeCriteriaNode.getChildNodes();

				for(int iIndex = 0;iIndex < IncludeCriteriaValuesList.getLength();iIndex++)
				{
					Node CriteriaValueNode = IncludeCriteriaValuesList.item(iIndex);
					switch(Integer.parseInt(CriteriaValueNode.getAttributes().getNamedItem("Id").getNodeValue()))
					{
					case 1:
						sourceCriteriaConfig.setFilterOrCriteriaLowerValue(CriteriaValueNode.getTextContent());

						break;
					case 2:
						sourceCriteriaConfig.setFilterOrCriteriaHigherValue(CriteriaValueNode.getTextContent());

						break;
					}
				}

				sourceFilters[iFiltersCounter++] = sourceCriteriaConfig;
			}
			sourceInfoConfig.setArchiveSourceFiltersConfig(sourceFilters);

			archiveSources[iPolicyIndex] = sourceInfoConfig;
		}

		//ArchiveSourceInfoConfiguration[] FileCopySources = new ArchiveSourceInfoConfiguration[iFileCopyPolicies];
		//int iFileCopyIndex = iPolicyIndex;
		for(int iFileCopyPolicyIndex = 0; iFileCopyPolicyIndex < iFileCopyPolicies;iFileCopyPolicyIndex++)
		{
			ArchiveSourceInfoConfiguration sourceInfoConfig = new ArchiveSourceInfoConfiguration();
			Node FileCopyPolicy = FileCopyPolicies.item(iFileCopyPolicyIndex);
			Node sourceAttr = FileCopyPolicy.getAttributes().getNamedItem("Source");
			if(sourceAttr != null)
			{
				sourceInfoConfig.setStrSourcePath(sourceAttr.getNodeValue());
			}
			
			Node displaySourceAttr = FileCopyPolicy.getAttributes().getNamedItem("DisplaySource");
			if(displaySourceAttr != null)
			{
				sourceInfoConfig.setStrDisplaySourcePath(displaySourceAttr.getNodeValue());
			}
			
			sourceInfoConfig.setbCopyFiles(true);

			Node FileCopyIndexNode = FileCopyPolicy.getAttributes().getNamedItem("Id");
			int iFileCopyIndex = Integer.parseInt(FileCopyIndexNode.getNodeValue());

			String sXPathPolicyIncludeFileFilters = StringUtil.enFormat("/ArchiveConfig/FileCopyPolicy[@Id='%d' and @Source='%s']/Includes/FileFilter",iFileCopyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList IncludeFileFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyIncludeFileFilters,XPathConstants.NODESET);
			int iIncludeFileFiltersCount = 0;
			iIncludeFileFiltersCount = IncludeFileFilters.getLength();

			String sXPathPolicyIncludeFolderFilters = StringUtil.enFormat("/ArchiveConfig/FileCopyPolicy[@Id='%d' and @Source='%s']/Includes/DirectoryFilter",iFileCopyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList IncludeFolderFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyIncludeFolderFilters,XPathConstants.NODESET);
			int iIncludeFolderFiltersCount = 0;
			iIncludeFolderFiltersCount = IncludeFolderFilters.getLength();

			String sXPathPolicyExcludeFileFilters = StringUtil.enFormat("/ArchiveConfig/FileCopyPolicy[@Id='%d' and @Source='%s']/Excludes/FileFilter",iFileCopyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList ExcludeFileFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyExcludeFileFilters,XPathConstants.NODESET);
			int iExcludeFileFiltersCount = 0;
			iExcludeFileFiltersCount = ExcludeFileFilters.getLength();

			String sXPathPolicyExcludeFolderFilters = StringUtil.enFormat("/ArchiveConfig/FileCopyPolicy[@Id='%d' and @Source='%s']/Excludes/DirectoryFilter",iFileCopyIndex,sourceInfoConfig.getStrSourcePath());
			NodeList ExcludeFolderFilters = (NodeList) archiveXMLXPathReader.readXPath(sXPathPolicyExcludeFolderFilters,XPathConstants.NODESET);
			int iExcludeFolderFiltersCount = 0;
			iExcludeFolderFiltersCount = ExcludeFolderFilters.getLength();

			//iFileCopyIndex++;//incrementing file copy index to access filters for next file copy policy

			int iFiltersCount = iExcludeFileFiltersCount + iExcludeFolderFiltersCount + iIncludeFileFiltersCount + iIncludeFolderFiltersCount;

			ArchiveSourceFiltersConfiguration[] sourceFilters = new ArchiveSourceFiltersConfiguration[iFiltersCount];// allocating 1 less because lastaccess time is not required to be in filters list.

			int iFiltersCounter = 0;
			for(int iExcludeFolderFilterIndex = 0;iExcludeFolderFilterIndex < iExcludeFolderFiltersCount;iExcludeFolderFilterIndex++)
			{
				Node ExcludeFolderFilter = ExcludeFolderFilters.item(iExcludeFolderFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_EXCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("1");
				String strActualFilterValue = ExcludeFolderFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(ExcludeFolderFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = ExcludeFolderFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iExcludeFileFilterIndex = 0;iExcludeFileFilterIndex < iExcludeFileFiltersCount;iExcludeFileFilterIndex++)
			{
				Node ExcludeFileFilter = ExcludeFileFilters.item(iExcludeFileFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_EXCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("0");

				String strActualFilterValue = ExcludeFileFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(ExcludeFileFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = ExcludeFileFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iIncludeFolderFilterIndex = 0;iIncludeFolderFilterIndex < iIncludeFolderFiltersCount;iIncludeFolderFilterIndex++)
			{
				Node IncludeFolderFilter = IncludeFolderFilters.item(iIncludeFolderFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_INCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("1");
				String strActualFilterValue = IncludeFolderFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(IncludeFolderFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = IncludeFolderFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			for(int iIncludeFileFilterIndex = 0;iIncludeFileFilterIndex < iIncludeFileFiltersCount;iIncludeFileFilterIndex++)
			{
				Node IncludeFileFilter = IncludeFileFilters.item(iIncludeFileFilterIndex);
				ArchiveSourceFiltersConfiguration sourceFiltersConfig = new ArchiveSourceFiltersConfiguration();
				sourceFiltersConfig.setFilterOrCriteriaType(FILTER_TYPE_INCLUDE);
				sourceFiltersConfig.setFilterOrCriteriaName("0");
				String strActualFilterValue = IncludeFileFilter.getAttributes().getNamedItem("Pattern").getNodeValue();
				sourceFiltersConfig.setLocFilterOrCriteriaLowerValue(IncludeFileFilter.getAttributes().getNamedItem("LocalizedPattern").getNodeValue());

				sourceFiltersConfig.setIsDefaultFilter(false);
				Node FilterValueName = IncludeFileFilter.getAttributes().getNamedItem("Name");
				if(FilterValueName != null)
				{
					strActualFilterValue = FilterValueName.getNodeValue() + "(" + strActualFilterValue + ")";
					sourceFiltersConfig.setIsDefaultFilter(true);
				}
				sourceFiltersConfig.setFilterOrCriteriaLowerValue(strActualFilterValue);
				sourceFiltersConfig.setIsCriteria(false);
				sourceFiltersConfig.setCriteriaOperator("");
				sourceFilters[iFiltersCounter++] = sourceFiltersConfig;
			}

			sourceInfoConfig.setArchiveSourceFiltersConfig(sourceFilters);
			archiveSources[iPolicyIndex++] = sourceInfoConfig;
		}
		archiveConfig.setArchiveSources(archiveSources);

		String sXPathDestinationType = "/ArchiveConfig/Destination/@DestType";
		Object DestinationType = archiveXMLXPathReader.readXPath(sXPathDestinationType, XPathConstants.STRING);

		if(DestinationType != null)
		{
			String strDestType = "";
			strDestType = DestinationType.toString();
			if(strDestType.compareToIgnoreCase("4") == 0)
			{
				archiveConfig.setbArchiveToDrive(true);
				archiveConfig.setbArchiveToCloud(false);

				String sXPathDestinationUserName = String.format("/ArchiveConfig/Destination[@DestType='%s']/UserName",strDestType);
				Object ObjDestinationUserName = null;
				ObjDestinationUserName = archiveXMLXPathReader.readXPath(sXPathDestinationUserName, XPathConstants.STRING);
				if(ObjDestinationUserName != null)
				{
					archiveConfig.setStrArchiveDestinationUserName(ObjDestinationUserName.toString());
				}

				String sXPathDestinationPassword = String.format("/ArchiveConfig/Destination[@DestType='%s']/Password",strDestType);
				Object ObjDestinationPassword = archiveXMLXPathReader.readXPath(sXPathDestinationPassword, XPathConstants.STRING);
				if(ObjDestinationPassword != null)
				{
					archiveConfig.setStrArchiveDestinationPassword((ObjDestinationPassword.toString().length() != 0) ? CommonService.getInstance().getNativeFacade().decrypt(ObjDestinationPassword.toString()):"");
				}
			}
			else if(DestinationType.toString().compareToIgnoreCase(0+"") == 0 || DestinationType.toString().compareToIgnoreCase(1+"")==0 || DestinationType.toString().compareToIgnoreCase(5+"")==0)
			{
				ArchiveCloudDestInfo cloudConfig = new ArchiveCloudDestInfo();
				archiveConfig.setbArchiveToDrive(false);
				archiveConfig.setbArchiveToCloud(true);

				String sXPathCloudVendorType = String.format("/ArchiveConfig/Destination[@DestType='%s']/cloudVendorType",strDestType);
				Object ObjDestinationVendorType = archiveXMLXPathReader.readXPath(sXPathCloudVendorType, XPathConstants.STRING);
				if(ObjDestinationVendorType != null)
				{
					cloudConfig.setcloudVendorType(Integer.parseInt(ObjDestinationVendorType.toString()));
				}
				
				String sXPathCloudSubVendorType = String.format("/ArchiveConfig/Destination[@DestType='%s']/cloudSubVendorType",strDestType);
				Object ObjDestinationSubVendorType = archiveXMLXPathReader.readXPath(sXPathCloudSubVendorType, XPathConstants.STRING);
				if(ObjDestinationSubVendorType != null && ObjDestinationSubVendorType.toString().length() > 0)
				{
					cloudConfig.setCloudSubVendorType(Integer.parseInt(ObjDestinationSubVendorType.toString()));
				}
				else
				{
					//set the Subvendor Type
					cloudConfig.setCloudSubVendorType(cloudConfig.getcloudVendorType());
					
				}

				String sXPathCloudVendorURL = String.format("/ArchiveConfig/Destination[@DestType='%s']/VendorURL",strDestType);
				Object ObjDestinationVendorURL = archiveXMLXPathReader.readXPath(sXPathCloudVendorURL, XPathConstants.STRING);
				if(ObjDestinationVendorURL != null)
				{
					cloudConfig.setcloudVendorURL(ObjDestinationVendorURL.toString());
				}

				String sXPathCloudVendorUsername = String.format("/ArchiveConfig/Destination[@DestType='%s']/AccessKey",strDestType);
				Object ObjDestinationVendorUsername = archiveXMLXPathReader.readXPath(sXPathCloudVendorUsername, XPathConstants.STRING);
				if(ObjDestinationVendorUsername != null)
				{
					cloudConfig.setcloudVendorUserName(ObjDestinationVendorUsername.toString());
				}

				String sXPathCloudVendorPassword = String.format("/ArchiveConfig/Destination[@DestType='%s']/SecretKey",strDestType);
				Object ObjDestinationVendorPassword = archiveXMLXPathReader.readXPath(sXPathCloudVendorPassword, XPathConstants.STRING);
				if(ObjDestinationVendorPassword != null)
				{
					cloudConfig.setcloudVendorPassword(CommonService.getInstance().getNativeFacade().decrypt(ObjDestinationVendorPassword.toString()));
				}
				
				String sRrsFlag = String.format("/ArchiveConfig/Destination[@DestType='%s']/CloudDestinationProperty",strDestType);
				Object ObjRrsFlag = archiveXMLXPathReader.readXPath(sRrsFlag, XPathConstants.STRING);
				if(ObjRrsFlag != null)
				{ 
					 String rrsFlagType = ObjRrsFlag.toString();
					if(rrsFlagType.equalsIgnoreCase("1"))
					{
					 cloudConfig.setRRSFlag(1L);
					}
					else
					{
					  cloudConfig.setRRSFlag(0L);
					}
				}

				/*String sXPathCloudVendorCertificatePath = String.format("/ArchiveConfig/Destination[@DestType='%s']/CertificatePath",strDestType);
				Object ObjDestinationVendorCertificatePath = archiveXMLXPathReader.readXPath(sXPathCloudVendorCertificatePath, XPathConstants.STRING);
				if(ObjDestinationVendorCertificatePath != null)
				{
					cloudConfig.setVendorCertificatePath(ObjDestinationVendorCertificatePath.toString());
				}

				String sXPathCloudCertificatePassword= String.format("/ArchiveConfig/Destination[@DestType='%s']/CertificatePassword",strDestType);
				Object ObjDestinationCertificatePassword = archiveXMLXPathReader.readXPath(sXPathCloudCertificatePassword, XPathConstants.STRING);
				if(ObjDestinationCertificatePassword != null)
				{
					cloudConfig.setCertificatePassword(CommonService.getInstance().getNativeFacade().decrypt(ObjDestinationCertificatePassword.toString()));
				}

				String sXPathCloudVendorHostname = String.format("/ArchiveConfig/Destination[@DestType='%s']/VendorHostname",strDestType);
				Object ObjDestinationVendorHostname = archiveXMLXPathReader.readXPath(sXPathCloudVendorHostname, XPathConstants.STRING);
				if(ObjDestinationVendorHostname != null)
				{
					cloudConfig.setVendorHostname(ObjDestinationVendorHostname.toString());
				}

				String sXPathCloudVendorPort = String.format("/ArchiveConfig/Destination[@DestType='%s']/VendorPort",strDestType);
				Object ObjDestinationVendorPort = archiveXMLXPathReader.readXPath(sXPathCloudVendorPort, XPathConstants.STRING);
				if(ObjDestinationVendorPort != null)
				{
					cloudConfig.setVendorPort(Integer.parseInt(ObjDestinationVendorPort.toString()));
				}*/
				
				
				String sXPathEncodedCloudBuckeName = String.format("/ArchiveConfig/Destination[@DestType='%s']/BucketName",strDestType);
				Object ObjDestinationEncodedBucketName = archiveXMLXPathReader.readXPath(sXPathEncodedCloudBuckeName, XPathConstants.STRING);
				if(ObjDestinationEncodedBucketName != null)
				{
					cloudConfig.setEncodedCloudBucketName(ObjDestinationEncodedBucketName.toString());
				}
				
				String sXPathCloudBuckeName = String.format("/ArchiveConfig/Destination[@DestType='%s']/BucketDisplayName",strDestType);
				Object ObjDestinationBucketName = archiveXMLXPathReader.readXPath(sXPathCloudBuckeName, XPathConstants.STRING);
				if(ObjDestinationBucketName != null && ObjDestinationBucketName.toString().length() > 0  )
				{
					cloudConfig.setcloudBucketName(ObjDestinationBucketName.toString());
				}
				else //For Migration Scenario 
				{
					if(ObjDestinationEncodedBucketName != null)
					   cloudConfig.setcloudBucketName(ObjDestinationEncodedBucketName.toString());
				}				
				

				if(DestinationType.toString().compareToIgnoreCase(1+"") != 0)
				{
					//BucketRegionName
					String sXPathCloudBucketRegionName = String.format("/ArchiveConfig/Destination[@DestType='%s']/BucketRegionName",strDestType);
					Object ObjDestinationBucketRegionName = archiveXMLXPathReader.readXPath(sXPathCloudBucketRegionName, XPathConstants.STRING);
					if(ObjDestinationBucketRegionName != null)
					{
						cloudConfig.setcloudBucketRegionName(ObjDestinationBucketRegionName.toString());
					}
				}

				String sXPathCloudProxyDetail = String.format("/ArchiveConfig/Destination[@DestType='%s']/Proxy",strDestType);
				Node ObjDestinationProxy = (Node) archiveXMLXPathReader.readXPath(sXPathCloudProxyDetail, XPathConstants.NODE);
				if(ObjDestinationProxy != null)
				{
					String ProxyEnabled = ObjDestinationProxy.getAttributes().getNamedItem("Enabled").getTextContent();
					cloudConfig.setcloudUseProxy(ProxyEnabled.compareToIgnoreCase("true") == 0 ? true : false);

					String ProxyRequiresAuth = ObjDestinationProxy.getAttributes().getNamedItem("RequiresAuth").getTextContent();
					cloudConfig.setcloudProxyRequireAuth(ProxyRequiresAuth.compareToIgnoreCase("true") == 0 ? true : false);
				}

				if(cloudConfig.iscloudUseProxy())
				{
					String sXPathProxyServer = String.format("/ArchiveConfig/Destination[@DestType='%s']/Proxy/ServerName",strDestType);
					Object ObjProxyServerName = archiveXMLXPathReader.readXPath(sXPathProxyServer, XPathConstants.STRING);
					if(ObjProxyServerName != null)
					{
						cloudConfig.setcloudProxyServerName(ObjProxyServerName.toString());
					}

					String sXPathProxyPort = String.format("/ArchiveConfig/Destination[@DestType='%s']/Proxy/Port",strDestType);
					Object ObjProxyPort = archiveXMLXPathReader.readXPath(sXPathProxyPort, XPathConstants.STRING);
					if(ObjProxyPort != null)
					{
						cloudConfig.setcloudProxyPort(Long.parseLong(ObjProxyPort.toString()));
					}

					if(cloudConfig.iscloudProxyRequireAuth())
					{
						String sXPathProxyUserName = String.format("/ArchiveConfig/Destination[@DestType='%s']/Proxy/UserName",strDestType);
						Object ObjProxyUserName = archiveXMLXPathReader.readXPath(sXPathProxyUserName, XPathConstants.STRING);
						if(ObjProxyUserName != null)
						{
							cloudConfig.setcloudProxyUserName(ObjProxyUserName.toString());
						}

						String sXPathProxyPassword = String.format("/ArchiveConfig/Destination[@DestType='%s']/Proxy/Password",strDestType);
						Object ObjProxyPassword = archiveXMLXPathReader.readXPath(sXPathProxyPassword, XPathConstants.STRING);
						if(ObjProxyPassword != null)
						{
							cloudConfig.setcloudProxyPassword(CommonService.getInstance().getNativeFacade().decrypt(ObjProxyPassword.toString()));
						}
					}
				}
				archiveConfig.setCloudConfig(cloudConfig);
			}

			String sXPathDestinationPath = String.format("/ArchiveConfig/Destination[@DestType='%s']/@Path",strDestType);
			Object ObjDestinationPath = archiveXMLXPathReader.readXPath(sXPathDestinationPath, XPathConstants.STRING);
			if(ObjDestinationPath != null)
			{
				archiveConfig.setStrArchiveToDrivePath(ObjDestinationPath.toString());
			}
			
			String sXPathDestinationCatalogPath = String.format("/ArchiveConfig/Destination[@DestType='%s']/@CatalogPath",strDestType);
			Object ObjDestinationCatalogPath = archiveXMLXPathReader.readXPath(sXPathDestinationCatalogPath, XPathConstants.STRING);
			if(ObjDestinationCatalogPath != null)
			{
				archiveConfig.setStrCatalogPath(ObjDestinationCatalogPath.toString());
			}

			String sXPathCompressionLevel = String.format("/ArchiveConfig/Destination[@DestType='%s']/CompressionLevel",strDestType);
			Object ObjCompressionLevel = archiveXMLXPathReader.readXPath(sXPathCompressionLevel, XPathConstants.STRING);
			if(ObjCompressionLevel != null)
			{
				archiveConfig.setCompressionLevel(Integer.parseInt(ObjCompressionLevel.toString()));
			}

			String sXPathSpaceUtilizationIndicator = String.format("/ArchiveConfig/Destination[@DestType='%s']/SpaceUtilizationIndicator",strDestType);
			Object ObjSpaceUtilizationIndicator = archiveXMLXPathReader.readXPath(sXPathSpaceUtilizationIndicator, XPathConstants.STRING);
			if(ObjSpaceUtilizationIndicator != null)
			{
				archiveConfig.setiSpaceUtilization(Integer.parseInt(ObjSpaceUtilizationIndicator.toString()));
			}

			String sXPathEncryption = String.format("/ArchiveConfig/Destination[@DestType='%s']/Encryption",strDestType);
			Node ObjEncryptionLevel = (Node)archiveXMLXPathReader.readXPath(sXPathEncryption, XPathConstants.NODE);
			if(ObjEncryptionLevel != null)
			{
				String EncryptionEnabled = ObjEncryptionLevel.getAttributes().getNamedItem("Enabled").getTextContent();
				archiveConfig.setbEncryption(EncryptionEnabled.compareToIgnoreCase("true") == 0 ? true : false);

				if(archiveConfig.isbEncryption())
					archiveConfig.setEncryptionPassword((ObjEncryptionLevel.getTextContent().length() != 0) ? CommonService.getInstance().getNativeFacade().decrypt(ObjEncryptionLevel.getTextContent()):"");
			}

			String sXPathFileVersions = String.format("/ArchiveConfig/Destination[@DestType='%s']/MaxFileVersions",strDestType);
			Object FileVersionsCount = archiveXMLXPathReader.readXPath(sXPathFileVersions, XPathConstants.STRING);
			if(FileVersionsCount != null)
			{
				archiveConfig.setFileVersionRetentionCount(Integer.parseInt(FileVersionsCount.toString()));
			}

			String sXPathRetentionTime = String.format("/ArchiveConfig/Destination[@DestType='%s']/PurgePolicy/Include/RetentionTime",strDestType);
			Object ObjRetentionTime = archiveXMLXPathReader.readXPath(sXPathRetentionTime, XPathConstants.STRING);
			if(ObjRetentionTime != null)
			{
				archiveConfig.setRetentiontime(ObjRetentionTime.toString());
			}
			
			String sXPathFilesRetentionTime = String.format("/ArchiveConfig/Destination[@DestType='%s']/FilesRetentionTime",strDestType);
			Object ObjFilesRetentionTime = archiveXMLXPathReader.readXPath(sXPathFilesRetentionTime, XPathConstants.STRING);
			if(ObjFilesRetentionTime != null){
				archiveConfig.setFilesRetentionTime(ObjFilesRetentionTime.toString());
			}
		}

		String sXPathArchiveSchedule = "/ArchiveConfig/Schedule/ArchiveSchedule/@Enabled";
		Object ObjArchiveSchedule = archiveXMLXPathReader.readXPath(sXPathArchiveSchedule, XPathConstants.STRING);
		if(ObjArchiveSchedule != null)
		{
			archiveConfig.setbArchiveAfterBackup(ObjArchiveSchedule.toString().compareToIgnoreCase("true") == 0 ? true : false);
		}

		String sXPathArchiveAfterNBackups = "/ArchiveConfig/Schedule/ArchiveSchedule/@AfterNBackups";
		Object ObjArchiveAfterNBackups = archiveXMLXPathReader.readXPath(sXPathArchiveAfterNBackups, XPathConstants.STRING);
		if(ObjArchiveAfterNBackups != null)
		{
			archiveConfig.setiArchiveAfterNBackups(Integer.parseInt(ObjArchiveAfterNBackups.toString()));
		}

		String sXPathPurgeSchedule = "/ArchiveConfig/Schedule/PurgeSchedule";
		Node ObjPurgeSchedule = (Node)archiveXMLXPathReader.readXPath(sXPathPurgeSchedule, XPathConstants.NODE);
		if(ObjPurgeSchedule != null)
		{
			archiveConfig.setbPurgeScheduleAvailable(true);
			archiveConfig.setbPurgeArchiveItems(ObjPurgeSchedule.getAttributes().getNamedItem("Enabled").getNodeValue().compareToIgnoreCase("true") == 0 ? true : false);
			archiveConfig.setiPurgeAfterDays(Integer.parseInt(ObjPurgeSchedule.getAttributes().getNamedItem("Interval").getNodeValue()));
			archiveConfig.setlPurgeStartTime(Long.parseLong(ObjPurgeSchedule.getAttributes().getNamedItem("StartTime").getNodeValue()));
		}
		else
		{
			archiveConfig.setbPurgeScheduleAvailable(false);
		}
		
		boolean needSeparate = false;
		String sXPathScheduleMode = "/ArchiveConfig/Schedule/@Mode";
		String objScheduleMode = (String) archiveXMLXPathReader.readXPath(sXPathScheduleMode, XPathConstants.STRING);
		if(StringUtil.isEmptyOrNull(objScheduleMode)){
			needSeparate = true;
			archiveConfig.setStrScheduleMode(ArchiveConfigurationConstants.SCHEDULE_MODE_SIMPLE);
		}else{
			needSeparate = false;
			archiveConfig.setStrScheduleMode(objScheduleMode);
		}
		if(ArchiveConfigurationConstants.SCHEDULE_MODE_ADVANCED.equalsIgnoreCase(archiveConfig.getStrScheduleMode())){
			String sXPathRecoveryPointToCopy = "/ArchiveConfig/Schedule/RecoveryPointToCopy";
			Node objRecoveryPointToCoyp = (Node) archiveXMLXPathReader.readXPath(sXPathRecoveryPointToCopy, XPathConstants.NODE);
			if(objRecoveryPointToCoyp != null){
				String sXPathDaily = "/ArchiveConfig/Schedule/RecoveryPointToCopy/daily";
				Node objDaily = (Node) archiveXMLXPathReader.readXPath(sXPathDaily, XPathConstants.NODE);
				if(objDaily != null){
					archiveConfig.setbDailyBackup(objDaily.getTextContent().compareToIgnoreCase("true") == 0 ? true : false);
				}
				
				String sXPathWeekly = "/ArchiveConfig/Schedule/RecoveryPointToCopy/weekly";
				Node objWeekly = (Node) archiveXMLXPathReader.readXPath(sXPathWeekly, XPathConstants.NODE);
				if(objWeekly != null){
					archiveConfig.setbWeeklyBackup(objWeekly.getTextContent().compareToIgnoreCase("true") == 0 ? true : false);
				}
				
				String sXPathMonthly = "/ArchiveConfig/Schedule/RecoveryPointToCopy/monthly";
				Node objMonthly = (Node) archiveXMLXPathReader.readXPath(sXPathMonthly, XPathConstants.NODE);
				if(objMonthly != null){
					archiveConfig.setbMonthlyBackup(objMonthly.getTextContent().compareToIgnoreCase("true") == 0 ? true : false);
				}
			}
		}
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(ServiceContext.getInstance().getArchiveConfigurationFilePath()));
			AdvanceSchedule advanceSchedule = AdvanceScheduleXMLParser.getAdvanceScheduleFromXML(doc);
			archiveConfig.setAdvanceSchedule(advanceSchedule);
		} catch (Exception e) {
			logger.error("load advance schedule failed with message : " + e.getMessage());
		}
		
		if(needSeparate)
			archiveConfigUtils.separateConfiguration(archiveConfig);
		
		return archiveConfig;
	}
	
	public Document saveArchiveSourcesXML(
			ArchiveSourceInfoConfiguration[] inArchiveSourceInfo) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document archiveXMLdoc = db.newDocument();

		if(archiveXMLdoc == null)
			return null;

        Element RootElement = archiveXMLdoc.createElement("ArchiveConfig");
        archiveXMLdoc.appendChild(RootElement);

        saveArchivePolicies(archiveXMLdoc, RootElement,inArchiveSourceInfo);
		return archiveXMLdoc;
	}

}
