package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.ca.arcflash.ui.client.model.ArchiveSourceFilterModel;

public class ArchiveSourceInfoModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7845545567132218982L;
	
	private ArchiveSourceFilterModel[] ArchiveSourceFilters;
	//private ArchiveSourceCriteriaModel[] ArchiveSourceCriterias;
	
	public String getSourcePath() {
		return get("SourcePath");
	}
	public void setSourcePath(String SourcePath) {
		set("SourcePath",SourcePath);
	}	
	
	public String getDispalySourcePath() {
		return get("DisplaySourcePath");
	}
	public void setDisplaySourcePath(String DisplaySourcePath) {
		set("DisplaySourcePath",DisplaySourcePath);
	}
	
	public Boolean getArchiveFiles() {
		return (Boolean)get("ArchiveFiles");
	}

	public void setArchiveFiles(Boolean in_bArchiveFiles) {
		set("ArchiveFiles", in_bArchiveFiles);
	}
	
	public Boolean getCopyFiles() {
		return (Boolean)get("CopyFiles");
	}

	public void setCopyFiles(Boolean in_bCopyFiles) {
		set("CopyFiles", in_bCopyFiles);
	}
	
/*	public Boolean getArchiveItemsChecked()
	{
		return (Boolean) get("ArchiveItemsChecked");		
	}
	public void setArchiveItemsChecked(Boolean in_ArchiveItemsChecked)
	{
		set("ArchiveItemsChecked", in_ArchiveItemsChecked);
	}*/
	
/*	public Integer getMonthsOlder() {
		return (Integer) get("MonthsOlder");
	}

	public void setMonthsOlder(Integer in_MonthsOlder) {
		set("MonthsOlder", in_MonthsOlder);
	}
	
	public Integer getDaysOlder() {
		return (Integer) get("DaysOlder");
	}

	public void setDaysOlder(Integer in_DaysOlder) {
		set("DaysOlder", in_DaysOlder);
	}*/
		
	public ArchiveSourceFilterModel[] getArchiveSourceFilters()
	{
		return ArchiveSourceFilters;
	}
	
	public void setArchiveSourceFilters(ArchiveSourceFilterModel[] in_archiveSourceFilters)
	{
		this.ArchiveSourceFilters = in_archiveSourceFilters;
	}
	
/*	public ArchiveSourceCriteriaModel[] getArchiveSourceCriterias()
	{
		return ArchiveSourceCriterias;
	}
	
	public void setArchiveSourceCriterias(ArchiveSourceCriteriaModel[] in_archiveSourceCriterias)
	{
		this.ArchiveSourceCriterias = in_archiveSourceCriterias;
	}*/
	
	public Boolean IsArchiveSourceConfigured()
	{
		return (Boolean)get("ArchiveSourceConfigured");
	}
	
	public void setArchiveSourceConfigured(Boolean in_bArchiveSourceConfigured)
	{
		set("ArchiveSourceConfigured",in_bArchiveSourceConfigured);
	}
	
	/*public void setArchiveSourceFilters(ArchiveSourceFilterModel in_ArchiveSourceFilterModel)
	{
		ArchiveSourceFilters.add(in_ArchiveSourceFilterModel);
		//set("ArchiveFiltersModel",in_ArchiveSourceFilterModel);
	}*/
}
