package com.ca.arcserve.edge.app.base.webservice.email;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;

public class EdgeEmailTemplate {
	private String content = null;
	private String productUrl;
	private boolean isHtml;
	private static final String HTMLHeaderSection;
	
	public boolean getHtmlFlag() {
		return isHtml;
	}
	public void setHtmlFlag(boolean isHtml) {
		this.isHtml = isHtml;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String url) {
		this.productUrl = url;
	}

	
	static
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
		
		HTMLHeaderSection = headerSection.toString();
	}
	
	
	public String getFormattedContent() {
		
		if (isHtml) {
			
			StringBuffer htmlTemplate = new StringBuffer();
			htmlTemplate.append("<HTML>");
			htmlTemplate.append(HTMLHeaderSection);
			htmlTemplate.append("	<BODY>");
			htmlTemplate.append("	<span>%s</span>");
			htmlTemplate.append("   <p/><p/>");
			htmlTemplate.append("	<br>%s");
			htmlTemplate.append("</BODY>");
			htmlTemplate.append("</HTML>");
					
		
			String linkStr = 
			    "<a href=\""+ productUrl + "\">" + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_LinkClickHere")
				+ "</a> " + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_LinkToMessage") ; 
			String htmlSrc = String.format(htmlTemplate.toString(), content, linkStr);
			return htmlSrc;
			
		} else {
			
			StringBuilder txtSrc = new StringBuilder(); 
			txtSrc.append(content);
			txtSrc.append("\n\n");
			txtSrc.append(EdgeCMWebServiceMessages.getMessage("EDGEMAIL_LinkClickHere"));
			txtSrc.append(String.format(EdgeCMWebServiceMessages.getMessage("EDGEMAIL_LinkFrom"), productUrl));
			txtSrc.append(EdgeCMWebServiceMessages.getMessage("EDGEMAIL_LinkToMessage"));
			return txtSrc.toString();
			
		}		
	}
	
}
