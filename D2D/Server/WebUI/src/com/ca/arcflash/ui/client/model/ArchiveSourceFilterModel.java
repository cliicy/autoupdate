package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveSourceFilterModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297463736871521111L;
	
	private String filterType; // String value of FilterOrCriteriaType
	
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	
	public String getFilterType() {
		return filterType;
	}
	
	public ArchiveSourceFilterModel()
	{
		set("IsDefaultFilter",false); 
	}
	
	public String getFilterOrCriteriaName() {
		return get("FilterOrCriteriaName");
	}
	public void setFilterOrCriteriaName(String in_FilterOrCriteriaName) {
		set("FilterOrCriteriaName",in_FilterOrCriteriaName);
	}
	
	public String getFilterOrCriteriaType() {
		return get("FilterOrCriteriaType");
	}
	public void setFilterOrCriteriaType(String in_FilterOrCriteriaType) {
		set("FilterOrCriteriaType",in_FilterOrCriteriaType);
	}
	
	public String getFilterOrCriteriaLowerValue() {
		return get("FilterOrCriteriaLowerValue");
	}
	public void setFilterOrCriteriaLowerValue(String in_FilterValue) {
		set("FilterOrCriteriaLowerValue",in_FilterValue);
	}
	
	public String getLocFilterOrCriteriaLowerValue() {
		return get("LocFilterOrCriteriaLowerValue");
	}
	public void setLocFilterOrCriteriaLowerValue(String in_FilterValue) {
		set("LocFilterOrCriteriaLowerValue",in_FilterValue);
	}
	
	public String getFilterOrCriteriaHigherValue() {
		return get("FilterOrCriteriaHigherValue");
	}
	public void setFilterOrCriteriaHigherValue(String in_FilterValue) {
		set("FilterOrCriteriaHigherValue",in_FilterValue);
	}
	
	public String getCriteriaOperator() {
		return get("CriteriaOperator");
	}
	public void setCriteriaOperator(String in_CriteriaOperator) {
		set("CriteriaOperator",in_CriteriaOperator);
	}
	
	public Boolean getIsCriteria()
	{
		return (Boolean)get("IsCriteria");
	}
	
	public void setIsCriteria(Boolean in_bCriteria)
	{
		set("IsCriteria",in_bCriteria);
	}
	
	public Boolean getIsDefaultFilter()
	{
		return (Boolean)get("IsDefaultFilter");
	}
	
	public void setIsDefaultFilter(Boolean in_bDefaultFilter)
	{
		set("IsDefaultFilter",in_bDefaultFilter);
	}
}
