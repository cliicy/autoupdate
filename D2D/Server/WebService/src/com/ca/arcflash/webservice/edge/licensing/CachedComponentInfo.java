/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author lijwe02
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class CachedComponentInfo {
	private long componentId;
	private String componentCode;
	private boolean reserved;
	private long cachedTime;
	private long checkResult;

	public CachedComponentInfo() {

	}

	public CachedComponentInfo(ComponentInfo componentInfo) {
		this.setComponentId(componentInfo.getComponentId());
		this.setComponentCode(componentInfo.getComponentCode());
		this.setReserved(componentInfo.isReserved());
		this.setCheckResult(componentInfo.getCheckResult());
		this.setCachedTime(System.currentTimeMillis());
	}

	public long getComponentId() {
		return componentId;
	}

	public void setComponentId(long componentId) {
		this.componentId = componentId;
	}

	public String getComponentCode() {
		return componentCode;
	}

	public void setComponentCode(String componentCode) {
		this.componentCode = componentCode;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public long getCachedTime() {
		return cachedTime;
	}

	public void setCachedTime(long cachedTime) {
		this.cachedTime = cachedTime;
	}

	public long getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(long checkResult) {
		this.checkResult = checkResult;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("[");
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			try {
				Object value = field.get(this);
				if (i != 0) {
					strBuf.append(", ");
				}
				strBuf.append(field.getName()).append("=").append(value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		strBuf.append("]");
		return strBuf.toString();
	}

}
