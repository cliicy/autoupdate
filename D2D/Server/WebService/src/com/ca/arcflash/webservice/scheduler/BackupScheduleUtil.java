package com.ca.arcflash.webservice.scheduler;

import java.util.Date;

import org.quartz.Calendar;
import org.quartz.Trigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupScheduleIntervalUnit;
import com.ca.arcflash.webservice.util.ScheduleUtils;

public class BackupScheduleUtil {
	  public static Trigger generateQuartzTrigger(String name, String groupName, Date startTime, final BackupSchedule schedule, final BackupSchedule firstPrioritySchedule,
				final Trigger firstPriority, final BackupSchedule secondPrioritySchedule, final Trigger secondPriority) {

			SimpleTriggerImpl trigger = null;

			if (secondPriority == null) {
				if (firstPriority == null) {
					trigger = new DSTSimpleTrigger() {
						private static final long serialVersionUID = 1L;
						private final long TIME_IN_MILLIS_FOR_HOUR = 60 * 60 * 1000;

						@Override
						public Date computeFirstFireTime(Calendar calendar) {
							Date date = super.computeFirstFireTime(calendar);
							this.updateAfterMisfire(null);
							return date;
						}

						@Override
						public Date getFireTimeAfter(Date afterTime) {
							Date nextFireTime = super.getFireTimeAfter(afterTime);
							java.util.Calendar nextTime = java.util.Calendar.getInstance();
							nextTime.setTime(nextFireTime);

							if (isInDSTSetBackTime(nextTime)) {
								nextTime.setTimeInMillis(nextTime.getTimeInMillis() + TIME_IN_MILLIS_FOR_HOUR);
							}

							return nextTime.getTime();
						}

						private boolean isInDSTSetBackTime(java.util.Calendar c) {
							java.util.Calendar nextC = java.util.Calendar.getInstance();
							nextC.setTimeInMillis(c.getTimeInMillis() + TIME_IN_MILLIS_FOR_HOUR);
							if (c.get(java.util.Calendar.HOUR_OF_DAY) == nextC.get(java.util.Calendar.HOUR_OF_DAY))
								return true;
							else
								return false;
						}
					};
				} else {
					trigger = new DSTSimpleTrigger() {
						private static final long serialVersionUID = 8101191355215099299L;

						@Override
						public Date computeFirstFireTime(Calendar calendar) {
							Date estimatedDate = super.computeFirstFireTime(calendar);
							this.updateAfterMisfire(null);
							estimatedDate = getRealExecTime(firstPriority, firstPrioritySchedule, this.getNextFireTime(), schedule);

							setNextFireTime(estimatedDate);
							return estimatedDate;
						}

						private Date getRealExecTime(Trigger highPriorityTrgr, BackupSchedule highPriority, Date estimatedDate, BackupSchedule lowPriority) {
							if (estimatedDate == null)
								return null;

							// Even in multi thread environment, there
							Date time1 = highPriorityTrgr.getNextFireTime() == null ? highPriorityTrgr.getStartTime() : highPriorityTrgr.getNextFireTime();

							if (estimatedDate.before(time1))
								return estimatedDate;

							if (estimatedDate.equals(time1)) {
								if (isAlwaysCollapse(highPriority, lowPriority)) {
									return null;
								} else {
									estimatedDate = getFireTimeAfter(estimatedDate);
								}
							}

							while (estimatedDate.after(time1)) {
								time1 = highPriorityTrgr.getFireTimeAfter(time1);

								if (estimatedDate.equals(time1)) {
									if (isAlwaysCollapse(highPriority, lowPriority)) {
										return null;
									} else {
										estimatedDate = getFireTimeAfter(estimatedDate);
									}
								}
							}

							return estimatedDate;
						}

						private boolean isAlwaysCollapse(BackupSchedule highSched, BackupSchedule lowSched) {
							int step1 = stepsInMinutes(highSched);
							int step2 = stepsInMinutes(lowSched);

							if (step1 == 0)
								return false;
							return step2 % step1 == 0;
						}

						private int stepsInMinutes(BackupSchedule schedule) {
							if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Hour)
								return schedule.getInterval() * 60;
							else if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Day)
								return schedule.getInterval() * 24 * 60;
							else
								return schedule.getInterval();
						}

						@Override
						public void triggered(Calendar calendar) {
							super.triggered(calendar);

							Date nextTime = getNextFireTime();
							nextTime = getRealExecTime(firstPriority, firstPrioritySchedule, nextTime, schedule);
							setNextFireTime(nextTime);
						}
					};
				}
			} else {
				trigger = new DSTSimpleTrigger() {
					private static final long serialVersionUID = 3986148756060307051L;

					@Override
					public Date computeFirstFireTime(Calendar calendar) {
						Date estimatedDate = super.computeFirstFireTime(calendar);
						this.updateAfterMisfire(null);
						estimatedDate = getRealExecTime(firstPriority, firstPrioritySchedule, secondPriority, secondPrioritySchedule, this.getNextFireTime(),
								schedule);

						setNextFireTime(estimatedDate);
						return estimatedDate;
					}

					private Date getRealExecTime(Trigger firstPriorityTrgr, BackupSchedule firstPrioritySched, Trigger secondPriorityTrgr,
							BackupSchedule secondPrioritySched, Date estimatedDate, BackupSchedule lowPrioritySched) {
						if (estimatedDate == null)
							return null;

						// Even in multi thread environment, there
						Date time1 = firstPriorityTrgr.getNextFireTime() == null ? firstPriorityTrgr.getStartTime() : firstPriorityTrgr.getNextFireTime();
						Date time2 = secondPriorityTrgr.getNextFireTime() == null ? secondPriorityTrgr.getStartTime() : secondPriorityTrgr.getNextFireTime();

						if (estimatedDate.before(time1) && estimatedDate.before(time2))
							return estimatedDate;

						estimatedDate = processEquals(time1, firstPrioritySched, time2, secondPrioritySched, estimatedDate, lowPrioritySched);
						if (estimatedDate == null) {
							return null;
						}
						
						while (estimatedDate.after(time1) || estimatedDate.after(time2)) {
							if (estimatedDate.after(time1))
								time1 = firstPriorityTrgr.getFireTimeAfter(time1);
							if (estimatedDate.after(time2))
								time2 = secondPriorityTrgr.getFireTimeAfter(time2);

							estimatedDate = processEquals(time1, firstPrioritySchedule, time2, secondPrioritySchedule, estimatedDate, lowPrioritySched);
						}

						return estimatedDate;
					}

					private Date processEquals(Date time1, BackupSchedule firstPrioritySched, Date time2, BackupSchedule secondPrioritySched, Date estimatedDate,
							BackupSchedule lowPrioritySched) {
						if (estimatedDate.equals(time1)) {
							if (isAlwaysCollapse(firstPrioritySched, lowPrioritySched)) {
								return null;
							} else {
								estimatedDate = getFireTimeAfter(estimatedDate);
							}
						}

						if (estimatedDate.equals(time2)) {
							if (isAlwaysCollapse(secondPrioritySched, lowPrioritySched)) {
								return null;
							} else {
								estimatedDate = getFireTimeAfter(estimatedDate);
							}
						}
						if (estimatedDate.equals(time2) || estimatedDate.equals(time1))
							return processEquals(time1, firstPrioritySchedule, time2, secondPrioritySchedule, estimatedDate, lowPrioritySched);

						return estimatedDate;
					}

					private boolean isAlwaysCollapse(BackupSchedule highSched, BackupSchedule lowSched) {
						int step1 = stepsInMinutes(highSched);
						int step2 = stepsInMinutes(lowSched);

						if (step1 == 0)
							return false;
						return step2 % step1 == 0;
					}

					private int stepsInMinutes(BackupSchedule schedule) {
						if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Hour)
							return schedule.getInterval() * 60;
						else if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Day)
							return schedule.getInterval() * 24 * 60;
						else
							return schedule.getInterval();
					}

					@Override
					public void triggered(Calendar calendar) {
						super.triggered(calendar);

						Date estimatedDate = getNextFireTime();
						estimatedDate = getRealExecTime(firstPriority, firstPrioritySchedule, secondPriority, secondPrioritySchedule, estimatedDate, schedule);
						setNextFireTime(estimatedDate);
					}
				};
			}

			if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Minute)
				trigger.setRepeatInterval(schedule.getInterval() * ScheduleUtils.MILLISECONDS_IN_MINUTE);
			else if (schedule.getIntervalUnit() == BackupScheduleIntervalUnit.Hour)
				trigger.setRepeatInterval(schedule.getInterval() * ScheduleUtils.MILLISECONDS_IN_HOUR);
			else
				trigger.setRepeatInterval(schedule.getInterval() * 24 * ScheduleUtils.MILLISECONDS_IN_HOUR);

			trigger.setGroup(groupName);
			trigger.setStartTime(startTime);
			trigger.setRepeatCount(-1);
			trigger.setName(name);

			return trigger;
		} 
	 
}
