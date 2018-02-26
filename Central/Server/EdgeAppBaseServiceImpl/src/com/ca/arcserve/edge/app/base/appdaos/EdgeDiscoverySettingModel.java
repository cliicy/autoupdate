package com.ca.arcserve.edge.app.base.appdaos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.SchedulerHelp;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting.ASBUSettingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EdgeDiscoverySetting")
public class EdgeDiscoverySettingModel {
	private static final Logger logger = Logger.getLogger(EdgeDiscoverySettingModel.class);
	private ASBUSettingStatus status = ASBUSettingStatus.disabled;
	private ASBUSettingStatus uDPStatus = ASBUSettingStatus.disabled;
	private ASBUSettingStatus aDStatus = ASBUSettingStatus.disabled;
	private List<DiscoveryOption> options;

	public ASBUSettingStatus getStatus() {
		return status;
	}

	public void setStatus(ASBUSettingStatus status) {
		this.status = status;
	}

	public ASBUSettingStatus getuDPStatus() {
		return uDPStatus;
	}

	public void setuDPStatus(ASBUSettingStatus uDPStatus) {
		this.uDPStatus = uDPStatus;
	}

	public ASBUSettingStatus getaDStatus() {
		return aDStatus;
	}

	public void setaDStatus(ASBUSettingStatus aDStatus) {
		this.aDStatus = aDStatus;
	}


	public List<DiscoveryOption> getOptions() {
		return options;
	}

	public void setOptions(List<DiscoveryOption> options) {
		this.options = options;
	}

	public static AutoDiscoverySetting getUISetting(AutoDiscoverySetting.SettingType settingType) {
		AutoDiscoverySetting auto = new AutoDiscoverySetting();

		List<EdgeDiscoverySetting> settings = new ArrayList<EdgeDiscoverySetting>();
		List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
		IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
		edao.as_edge_discovery_setting_get(settingType.ordinal(), settings, schedules);
		if (settings.size() > 0 && schedules.size() > 0) {
			EdgeDiscoverySettingModel model;
			try {
				model = EdgeDiscoverySetting.getModel(settings.get(0)
						.getXMLContent());
				ScheduleData scheduleData = SchedulerHelp
						.getDataFromScheduleItem(schedules.get(0));
				auto.setaDStatus(model.getaDStatus());
				auto.setOptions(model.getOptions());
				auto.setStatus(model.getStatus());
				auto.setSchedule(scheduleData);
			} catch (JAXBException e) {
				logger.error(e.getMessage(), e);
			}

		}

		return auto;
	}

	public static EdgeDiscoverySettingModel getModel(
			AutoDiscoverySetting setting) {
		EdgeDiscoverySettingModel model = new EdgeDiscoverySettingModel();
		model.setaDStatus(setting.getaDStatus());
		model.setOptions(setting.getOptions());
		model.setStatus(setting.getStatus());
		return model;
	}
}
