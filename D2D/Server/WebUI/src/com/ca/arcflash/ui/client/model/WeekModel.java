package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class WeekModel extends BaseModelData {
	public WeekModel() {
	}

	public WeekModel(Integer day) {
		setDay(day);
	}
	

	private static final long serialVersionUID = 423260444389389557L;
	
	/**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Sunday.
     */
    public final static int SUNDAY = 1;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Monday.
     */
    public final static int MONDAY = 2;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Tuesday.
     */
    public final static int TUESDAY = 3;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Wednesday.
     */
    public final static int WEDNESDAY = 4;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Thursday.
     */
    public final static int THURSDAY = 5;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Friday.
     */
    public final static int FRIDAY = 6;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Saturday.
     */
    public final static int SATURDAY = 7;
	
	public void setDay(Integer day) {
		set("day", day);
		setName(Utils.getDayofWeek(getDay()));
	}
	
	public Integer getDay(){
		return (Integer)get("day");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public String getName() {
		return get("name");
	}
}
