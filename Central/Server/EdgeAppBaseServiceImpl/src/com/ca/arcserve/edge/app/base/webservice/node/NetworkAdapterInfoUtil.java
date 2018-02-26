/**
 * Created on Jan 6, 2013 4:02:21 PM
 */
package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.jobscript.failover.Gateway;
import com.ca.arcflash.jobscript.failover.IPAddressInfo;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcserve.edge.app.base.appdaos.EdgeNetworkConfiguration;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DNSForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.GatewayForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.IPAddressInfoForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.IPSettingForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SourceMachineNetworkAdapterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.WinsForADR;

/**
 * @author lijwe02
 * 
 */
public class NetworkAdapterInfoUtil {
	public static final String SEMICOLON = ";";
	public static final String COLON = ":";

	private static final Logger logger = Logger.getLogger(NetworkAdapterInfoUtil.class);

	public static List<EdgeNetworkConfiguration> getEdgeNetworkConfigurationFromADRConfigure(ADRConfigure adrConfigInfo) {
		List<EdgeNetworkConfiguration> edgeNetworkConfigurationList = new ArrayList<EdgeNetworkConfiguration>();

		if (adrConfigInfo != null) {
			for (NetworkAdapter adapterInfo : adrConfigInfo.getNetadapters()) {
				StringBuilder ipStr = new StringBuilder();
				StringBuilder gatewayStr = new StringBuilder();
				StringBuilder dnsStr = new StringBuilder();
				StringBuilder winsStr = new StringBuilder();
				IPSetting ipSetting = new IPSetting();
				if (adrConfigInfo.isNetworkAdapterInfoFromPolicy()) {
					ipSetting.setDhcp(true);
					adapterInfo.setAdapterDesc(adapterInfo.getAdapterName());
				}
				if (adapterInfo.getIpSettings() != null && adapterInfo.getIpSettings().size() > 0) {
					ipSetting = adapterInfo.getIpSettings().get(0);
					if (ipSetting.getIpAddresses() != null) {
						int ipSize = ipSetting.getIpAddresses().size();
						for (int i = 0; i < ipSize; i++) {
							IPAddressInfo ip = ipSetting.getIpAddresses().get(i);
							ipStr.append(ip.getIp()).append(COLON).append(ip.getSubnet());
							if (i < ipSize - 1) {
								ipStr.append(SEMICOLON);
							}
						}
					}

					if (ipSetting.getGateways() != null && !ipSetting.isDhcp()) {
						int gatewaySize = ipSetting.getGateways().size();
						for (int j = 0; j < gatewaySize; j++) {
							Gateway gateway = ipSetting.getGateways().get(j);
							gatewayStr.append(gateway.getGatewayAddress());
							if (j < gatewaySize - 1) {
								gatewayStr.append(SEMICOLON);
							}
						}
					}

					if (ipSetting.getDnses() != null && !ipSetting.isAutoDNS()) {
						int dnsSize = ipSetting.getDnses().size();
						for (int m = 0; m < dnsSize; m++) {
							String dns = ipSetting.getDnses().get(m);
							dnsStr.append(dns);
							if (m < dnsSize - 1) {
								dnsStr.append(SEMICOLON);
							}
						}
					}

					if (ipSetting.getWins() != null && !ipSetting.isAutoWins()) {
						int winsSize = ipSetting.getWins().size();
						for (int n = 0; n < winsSize; n++) {
							String wins = ipSetting.getWins().get(n);
							winsStr.append(wins);
							if (n < winsSize - 1) {
								winsStr.append(SEMICOLON);
							}
						}
					}
				}

				EdgeNetworkConfiguration edgeNetworkConfiguration = new EdgeNetworkConfiguration();
				edgeNetworkConfiguration.setAdapterDesc(adapterInfo.getAdapterDesc());
				edgeNetworkConfiguration.setMacAddress(adapterInfo.getMACAddress());
				edgeNetworkConfiguration.setIsDHCP(ipSetting.isDhcp() ? 0 : 1);
				edgeNetworkConfiguration.setIpStr(ipStr.toString());
				edgeNetworkConfiguration.setGatewayStr(gatewayStr.toString());
				edgeNetworkConfiguration.setDnsStr(dnsStr.toString());
				edgeNetworkConfiguration.setWinsStr(winsStr.toString());
				edgeNetworkConfigurationList.add(edgeNetworkConfiguration);
			}
		} else {
			logger.error("The adrconfigure object is null.");
		}

		return edgeNetworkConfigurationList;
	}

	public static SourceMachineNetworkAdapterInfo convertEdgeNetworkConfigurationToSourceMachineNetworkAdapterInfo(
			EdgeNetworkConfiguration adapter) {
		if (adapter == null) {
			return null;
		}
		SourceMachineNetworkAdapterInfo sourceAdapter = new SourceMachineNetworkAdapterInfo();
		sourceAdapter.setAdapterDescription(adapter.getAdapterDesc());
		sourceAdapter.setMacAddress(adapter.getMacAddress());
		List<IPSettingForADR> ipForADRList = new ArrayList<IPSettingForADR>();
		IPSettingForADR ipSetting = new IPSettingForADR();
		ipSetting.setDhcp(adapter.getIsDHCP() == 0 ? true : false);
		if (!StringUtil.isEmptyOrNull(adapter.getIpStr())) {
			List<IPAddressInfoForADR> ipList = new ArrayList<IPAddressInfoForADR>();
			for (String ipStr : adapter.getIpStr().split(SEMICOLON)) {
				IPAddressInfoForADR ip = new IPAddressInfoForADR();
				String[] ipAddr = ipStr.split(COLON);
				ip.setIp(ipAddr[0]);
				ip.setSubnet(ipAddr[1]);
				ipList.add(ip);
			}
			ipSetting.setIps(ipList);
		}
		if (!StringUtil.isEmptyOrNull(adapter.getDnsStr())) {
			List<DNSForADR> dnsList = new ArrayList<DNSForADR>();
			for (String dns : adapter.getDnsStr().split(SEMICOLON)) {
				DNSForADR dnsADR = new DNSForADR();
				dnsADR.setDns(dns);
				dnsList.add(dnsADR);
			}
			ipSetting.setDnses(dnsList);
		}
		if (!StringUtil.isEmptyOrNull(adapter.getGatewayStr())) {
			List<GatewayForADR> gatewayList = new ArrayList<GatewayForADR>();
			for (String gatewayStr : adapter.getGatewayStr().split(SEMICOLON)) {
				GatewayForADR gateway = new GatewayForADR();
				gateway.setGatewayAddress(gatewayStr);
				gatewayList.add(gateway);
			}
			ipSetting.setGateways(gatewayList);
		}
		if (!StringUtil.isEmptyOrNull(adapter.getWinsStr())) {
			List<WinsForADR> winsList = new ArrayList<WinsForADR>();
			for (String win : adapter.getWinsStr().split(SEMICOLON)) {
				WinsForADR winADR = new WinsForADR();
				winADR.setWins(win);
				winsList.add(winADR);
			}
			ipSetting.setWins(winsList);
		}
		ipForADRList.add(ipSetting);
		sourceAdapter.setIpSettings(ipForADRList);
		return sourceAdapter;
	}
}
