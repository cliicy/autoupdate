package com.ca.arcserve.edge.app.base.schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeScheduler_Schedule;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSchedulerDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData.RepeatMethodData;

public class SchedulerHelp {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	private static IEdgeSchedulerDao scheduleDao = DaoFactory
			.getDao(IEdgeSchedulerDao.class);

	public static List<ScheduleData> getSchedules(List<Integer> scheduleIDs) {
		List<ScheduleData> scheduleDatas = new ArrayList<ScheduleData>();
		if (scheduleIDs.isEmpty())
			return scheduleDatas;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("(");
		for (int id : scheduleIDs) {
			if (!first)
				sb.append(",");
			sb.append(id);
			first = false;
		}
		sb.append(")");
		List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();

		scheduleDao.as_edge_schedules_list(sb.toString(), schedules);
		for (EdgeScheduler_Schedule schedule : schedules) {
			ScheduleData scheduleData = getDataFromScheduleItem(schedule);
			scheduleDatas.add(scheduleData);
		}
		return scheduleDatas;
	}

	public static ScheduleData getDataFromScheduleItem(
			EdgeScheduler_Schedule schedule) {
		ScheduleData scheduleData = new ScheduleData();

		scheduleData.setScheduleID(schedule.getID());
		scheduleData.setActedTimes(schedule.getActedTimes());
		scheduleData.setScheduleName(schedule.getName());
		scheduleData.setScheduleDescription(schedule.getDescription());
		scheduleData.setScheduleStatus(ScheduleData.ScheduleStatus
				.parseInt(schedule.getActionType()));
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(schedule
						.getScheduleType()));
		String repeatMethodParameter = schedule.getScheduleParam();
		setRepeartData( repeatMethodParameter ,scheduleData);

		scheduleData.setScheduleTime(schedule.getActionTime());

		scheduleData.setStartFromDate(schedule.getRepeatFrom());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType
				.parseInt(schedule.getRepeatType()));
		String repeatUntilParameter = schedule.getRepeatParam();
		if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.endDate) {
			try {
				scheduleData.setEndDate(dateFormat.parse(
						parseScheduleParameter(repeatUntilParameter)));
			} catch (ParseException e) {
				scheduleData.setEndDate((new Date()));
			}
		} else if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.numberOfTimes) {
			scheduleData
					.setNumberOfTimes(Integer
							.parseInt(parseScheduleParameter(repeatUntilParameter)));
		}
		return scheduleData;
	}

	public static void setRepeartData(
			String repeatMethodParameter,ScheduleData scheduleData) {
		if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfDays) {
			scheduleData
					.getRepeatMethodData()
					.setEveryDays(
							Integer
									.parseInt(parseScheduleParameter(repeatMethodParameter)));
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfHours) {
			scheduleData
					.getRepeatMethodData()
					.setEveryHours(
							Integer
									.parseInt(parseScheduleParameter(repeatMethodParameter)));
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfMins) {
			scheduleData
					.getRepeatMethodData()
					.setEveryMins(
							Integer
									.parseInt(parseScheduleParameter(repeatMethodParameter)));
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfSecs) {
			scheduleData
					.getRepeatMethodData()
					.setEverySeconds(
							Integer
									.parseInt(parseScheduleParameter(repeatMethodParameter)));
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everySelectedDaysOfWeek) {
			if (repeatMethodParameter.contains("Monday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatMonday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatMonday(false);
			}
			if (repeatMethodParameter.contains("Tuesday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatTuesday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatTuesday(false);
			}
			if (repeatMethodParameter.contains("Wednesday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatWednesday(true);
			} else {
				scheduleData.getRepeatMethodData()
						.setRepeatWednesday(false);
			}
			if (repeatMethodParameter.contains("Thursday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatThursday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatThursday(false);
			}
			if (repeatMethodParameter.contains("Friday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatFriday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatFriday(false);
			}
			if (repeatMethodParameter.contains("Saturday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatSaturday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatSaturday(false);
			}
			if (repeatMethodParameter.contains("Sunday=\"Yes\"")) {
				scheduleData.getRepeatMethodData().setRepeatSunday(true);
			} else {
				scheduleData.getRepeatMethodData().setRepeatSunday(false);
			}
		} else {
			String[] itemStrings = repeatMethodParameter.split(",");
			for (String itemString : itemStrings) {
				if (itemString.startsWith("CountFrom")) {
					if (parseScheduleParameter(itemString).equals(
							"Beginning")) {
						scheduleData.getRepeatMethodData().setFromBegin(
								true);
					} else {
						scheduleData.getRepeatMethodData().setFromBegin(
								false);
					}

				} else if (itemString.startsWith("DayCount")) {
					scheduleData
							.getRepeatMethodData()
							.setDayNumber(
									Integer
											.parseInt(parseScheduleParameter(itemString)));
				}
			}
		}
	}

	public static void saveSchedule(ScheduleData scheduleData) {
		String repeatMethodParameter = createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = createRepeatUntilParameter(scheduleData);
		scheduleDao.as_edge_schedule_update_for_ui(
				scheduleData.getScheduleID(), scheduleData.getScheduleName(),
				scheduleData.getScheduleDescription(), scheduleData
						.getScheduleStatus().getValue(),
				scheduleData.getRepeatMethodData().getRepeatMethodType()
						.getValue(), repeatMethodParameter, scheduleData.getScheduleTime(), 
						scheduleData.getStartFromDate(), scheduleData.getRepeatUntilType()
						.getValue(), repeatUntilParameter);
	}

	public static String createRepeatMethodParameter(ScheduleData scheduleData) {
		String paraString = "";
		final String yesString = "=\"Yes\",";
		if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfDays) {
			paraString = "Interval=\""
					+ scheduleData.getRepeatMethodData().getEveryDays() + "\"";
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfHours) {
			paraString = "Interval=\""
					+ scheduleData.getRepeatMethodData().getEveryHours() + "\"";
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfMins) {
			paraString = "Interval=\""
					+ scheduleData.getRepeatMethodData().getEveryMins() + "\"";
		} else if (

		scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everyNumberOfSecs) {
			paraString = "Interval=\""
					+ scheduleData.getRepeatMethodData().getEverySeconds()
					+ "\"";
		} else if (scheduleData.getRepeatMethodData().getRepeatMethodType() == ScheduleData.RepeatMethodType.everySelectedDaysOfWeek) {
			if (scheduleData.getRepeatMethodData().isRepeatMonday()) {
				paraString = ("Monday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatTuesday()) {
				paraString += ("Tuesday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatWednesday()) {
				paraString += ("Wednesday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatThursday()) {
				paraString += ("Thursday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatFriday()) {
				paraString += ("Friday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatSaturday()) {
				paraString += ("Saturday" + yesString);
			}
			if (scheduleData.getRepeatMethodData().isRepeatSunday()) {
				paraString += ("Sunday" + yesString);
			}

			if (paraString.lastIndexOf(",") == paraString.length() - 1) {
				paraString = paraString.substring(0, paraString.length() - 1);
			}
		} else {
			if (scheduleData.getRepeatMethodData().isFromBegin()) {
				paraString = "CountFrom=\"Beginning\",";
			} else {
				paraString = "CountFrom=\"End\",";
			}

			paraString += "DayCount=\""
					+ scheduleData.getRepeatMethodData().getDayNumber() + "\"";
		}

		return paraString;
	}

	public static String createRepeatUntilParameter(ScheduleData scheduleData) {
		String paraString = "";

		if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.endDate) {
			paraString = "EndDate=\""
					+ dateFormat.format(scheduleData.getEndDate())
					+ "\"";
		} else if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.numberOfTimes) {
			paraString = "TimesCount=\"" + scheduleData.getNumberOfTimes()
					+ "\"";
		}

		return paraString;
	}

	private static String parseScheduleParameter(String parameter) {
		String value = "";
		int equalIndex = parameter.indexOf("=");
		value = parameter.substring(equalIndex + 1, parameter.length());
		value = value.substring(1, value.length() - 1);

		return value;
	}

	
	public static void setRepeatUntilParameterRelatedValues(String repeatUntilParameter, ScheduleData scheduleData) {
		if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.endDate) {			
			try {
				scheduleData.setEndDate(dateFormat.parse(parseScheduleParameter(repeatUntilParameter)));
			} catch (ParseException e) {
				scheduleData.setEndDate((new Date()));
			}
		} else if (scheduleData.getRepeatUntilType() == ScheduleData.RepeatUnitlType.numberOfTimes) {
			scheduleData.setNumberOfTimes(Integer
					.parseInt(parseScheduleParameter(repeatUntilParameter)));
		}
	}
	



	public static ScheduleData mergeGlobalIntoScheduleData(
			ScheduleData globalScheduleData, ScheduleData scheduleData) {
		ScheduleData newData = new ScheduleData();

		newData.setScheduleID(scheduleData.getScheduleID());
		newData.setEndDate(globalScheduleData.getEndDate());

		newData.setNumberOfTimes(globalScheduleData.getNumberOfTimes());
		ScheduleData.RepeatMethodData methodData = new ScheduleData.RepeatMethodData();
		RepeatMethodData repeatMethodData = globalScheduleData
				.getRepeatMethodData();

		methodData.setDayNumber(repeatMethodData.getDayNumber());
		methodData.setEveryDays(repeatMethodData.getEveryDays());

		methodData.setEveryHours(repeatMethodData.getEveryHours());
		methodData.setEveryMins(repeatMethodData.getEveryMins());
		methodData.setEverySeconds(repeatMethodData.getEverySeconds());

		methodData.setFromBegin(repeatMethodData.isFromBegin());
		methodData.setRepeatFriday(repeatMethodData.isRepeatFriday());
		methodData.setRepeatMethodType(repeatMethodData.getRepeatMethodType());

		methodData.setRepeatMonday(repeatMethodData.isRepeatMonday());
		methodData.setRepeatSaturday(repeatMethodData.isRepeatSaturday());
		methodData.setRepeatSunday(repeatMethodData.isRepeatSunday());

		methodData.setRepeatThursday(repeatMethodData.isRepeatThursday());
		methodData.setRepeatTuesday(repeatMethodData.isRepeatTuesday());
		methodData.setRepeatWednesday(repeatMethodData.isRepeatWednesday());

		newData.setRepeatMethodData(methodData);

		newData.setRepeatUntilType(globalScheduleData.getRepeatUntilType());
		newData.setScheduleDescription(globalScheduleData
				.getScheduleDescription());
		newData.setScheduleName(globalScheduleData.getScheduleName());

		newData.setScheduleStatus(globalScheduleData.getScheduleStatus());
		newData.setScheduleTime(globalScheduleData.getScheduleTime());
		newData.setStartFromDate(globalScheduleData.getStartFromDate());

		return newData;
	}

}
