package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeSRMArchiveSetting {
	private int type;
	private int action;
	private int year;
	private int month;
	private int week;
	private int seconds;
	
	public enum SrmArchiveAction {
		WEEKLY, MONTHLY, YEARLY
	}
	
	public enum SrmArchiveType {
		PKI_TRENDING, APP_TRENDING, VOLUME_TRENDING
	}
	
	
	public int getType() {
		return type;
	}
	
	public void setType(int iType) {
		type = iType;
	}
	
	public static SrmArchiveType MapIntegerToType(int type) {
		switch(type) {
		case 1:
			return SrmArchiveType.PKI_TRENDING;
		case 2:
			return SrmArchiveType.APP_TRENDING;
		case 3:
			return SrmArchiveType.VOLUME_TRENDING;
		default:
			return null;
		}
	}
	
	public static int MapTypeToInteger(SrmArchiveType type) {
		switch(type) {
		case PKI_TRENDING:
			return 1;
		case APP_TRENDING:
			return 2;
		case VOLUME_TRENDING:
			return 3;
		default:
			return 0;
		}
	}
	
	public int getAction() {
		return action;
	}
	
	public void setAction(int iAction) {
		action = iAction;
	}
	
	public static SrmArchiveAction MapIntegerToAction(int iAction) {
		switch (iAction) {
		case 1:
			return SrmArchiveAction.WEEKLY;
		case 2:
			return SrmArchiveAction.MONTHLY;
		case 3:
			return SrmArchiveAction.YEARLY;
		default:
			return null;
		}
	}
	
	public static int MapActionToInteger(SrmArchiveAction action) {
		switch(action) {
		case WEEKLY:
			return 1;
		case MONTHLY:
			return 2;
		case YEARLY:
			return 3;
		default:
			return 0;
		}
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int iYear) {
		year = iYear;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int iMonth) {
		month = iMonth;
	}
	
	public int getWeek() {
		return week;
	}
	
	public void setWeek(int iWeek) {
		week = iWeek;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public void setSeconds(int iSeconds) {
		seconds = iSeconds;
	}
	
	
	
}
