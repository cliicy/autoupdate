package com.ca.arcflash.ui.client.backup.schedule;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DailyScheduleDetailItemModel extends BaseModelData {
	private static final long serialVersionUID = 1483396229545686343L;
	public int dayOfweek;
	public  List<ScheduleDetailItemModel> scheduleDetailItemModels;
	public List<ThrottleModel> throttleModels;
	public List<MergeDetailItemModel> mergeModels;	
}
