package com.ca.arcflash.ui.client.backup.schedule;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class PeriodScheduleModel  extends BaseModelData {	
	private static final long serialVersionUID = -1942969494037798960L;
	public EveryDayScheduleModel dayScheduleModel;	
	public EveryWeekScheduleModel weekScheduleModel;
	public EveryMonthScheduleModel monthScheduleModel;
}
