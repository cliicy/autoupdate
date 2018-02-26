package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveFileVersionNode extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4295176681415578888L;

		public String getFileName()
		{
			return get("FileName");
		}
		
		public void setFileName(String in_strFileName)
		{
			set("FileName",in_strFileName);
		}
	
	   public Integer getVersion() {
			return (Integer)get("Version");
		}

		public void setVersion(Integer in_Version) {
			set("Version",in_Version);
		}
		
		public Long getFileSize()
		{
			return (Long)get("FileSize");
		}
		public void setFileSize(Long in_FileSize)
		{
			set("FileSize",in_FileSize);
		}

		public Date getModifiedTime() {
			return (Date)get("ModifiedTime");
		}

		public void setModifiedTime(Date in_ModifiedTime) {
			set("ModifiedTime",in_ModifiedTime);
		}
		
		public Date getArchivedTime()
		{
			return (Date)get("ArchivedTime");
		}
		public void setArchivedTime(Date in_ArchivedTime)
		{
			set("ArchivedTime",in_ArchivedTime);
		}

		public Integer getFileType() {
			return (Integer)get("FileType");
		}

		public void setFileType(Integer in_FileType) {
			set("FileType",in_FileType);
		}
		
		public Boolean getChecked()
		{
			return (Boolean) get("checked");
		}
		public void setChecked(Boolean check)
		{
			set("checked", check);
		}
		
		public Boolean getSelectable()
		{
			return (Boolean) get("Selectable");
		}
		public void setSelectable(Boolean in_Selectable)
		{
			set("Selectable", in_Selectable);
		}
		public Long getArchivedTZOffset() {
			return (Long)get("ArchivedTZOffset");
		}
		
		public void setArchivedTZOffset(Long offset) {
			set("ArchivedTZOffset", offset);
		}
		public Long getModifiedTZOffset() {
			return (Long)get("ModifiedTZOffset");
		}
		
		public void setModifiedTZOffset(Long offset) {
			set("ModifiedTZOffset", offset);
		}
}
