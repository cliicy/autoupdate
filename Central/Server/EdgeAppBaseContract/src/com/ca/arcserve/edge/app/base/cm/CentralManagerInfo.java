package com.ca.arcserve.edge.app.base.cm;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.jaxbadapter.CDataAdapter;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;

@XmlRootElement(name = "EdgeCentralManagerInfo")
public class CentralManagerInfo {
	
	public static final String INFO_FILE = "protection_manager_info.xml";
	private String host;
	private int port;
	private String protocol;
	private String userName;
	@NotPrintAttribute
	private String password;
	
	@XmlElement
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@XmlElement
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	@XmlElement
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	@XmlElement(nillable = true)
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public synchronized void saveInfo(String filePath)
			throws Exception {

		String password = getPassword();

		if (password != null) {
			setPassword(CommonUtil.encrypt(password));
		}

		String marshal = CommonUtil.marshal(this);
		CommonUtil.saveStringToFile(marshal, filePath);
	}
	
	public synchronized static CentralManagerInfo getInstance(String filePath)
			throws Exception {
		CentralManagerInfo info = null;

		if (info == null) {
			File f = new File(filePath);
			
			if (!f.exists()) {
				info = new CentralManagerInfo();
			} else {
				String readFileAsString = CommonUtil.readFileAsString(filePath);

				if (!StringUtil.isEmptyOrNull(readFileAsString)) {
					info = JAXB.unmarshal(new StringReader(readFileAsString),
							CentralManagerInfo.class);

					if (info.getPassword() != null) {
						info.setPassword(CommonUtil.decrypt(info.getPassword()));
					}
				}
			}
		}

		return info;
	}

}
