package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class ComponentInformation implements Serializable,
		BeanModelTag {

	private static final long serialVersionUID = 4727147144928527145L;
	/**
	 * ID or componentName should can uniquely identify a component.
	 */
	private String id;
	private String componentName;
	private LicenseType type;
	private long installTime;
	private List<LicenseKey> keys = new ArrayList<LicenseKey>();
	private LicenseStatusInformation status = new LicenseStatusInformation();

	public LicenseStatusInformation getStatus() {
		return status;
	}

	public long getInstallTime() {
		return installTime;
	}

	public void setInstallTime(long installTime) {
		this.installTime = installTime;
	}

	public void setStatus(LicenseStatusInformation status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<LicenseKey> getKeys() {
		return keys;
	}

	public void setKeys(List<LicenseKey> keys) {
		this.keys = keys;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public LicenseType getType() {
		return type;
	}

	public void setType(LicenseType type) {
		this.type = type;
	}

	public ComponentInformation() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ComponentInformation copy(){
		String id = this.getId();
		String componentName = this.getComponentName();
		LicenseType type = this.getType();
		ComponentInformation copy = new ComponentInformation();

		copy.id = id;
		copy.componentName = componentName;
		copy.type = type;
		{
			copy.keys = new ArrayList<LicenseKey>();
			for (LicenseKey k : this.keys){
				copy.keys.add(k.copy());
			}
		}
		copy.setStatus(getStatus().copy());
		copy.setInstallTime(getInstallTime());
		return copy;
	}

}
