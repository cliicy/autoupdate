package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Timestamp;
import java.util.Calendar;

public class EdgeDaoJavaDBUDFs {
	public static int BitwiseAnd(int a, int b) {
		return a & b;
	}

	public static int BitwiseOr(int a, int b) {
		return a | b;
	}

	public static Timestamp UDFDATEADD(String tickType, int tickcount,
			Timestamp internalTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(internalTime);

		if(tickType.compareToIgnoreCase("hour") == 0)
			c.add(Calendar.HOUR_OF_DAY, tickcount);
		else if(tickType.compareToIgnoreCase("minute") == 0)
			c.add(Calendar.MINUTE, tickcount);
		else if(tickType.compareToIgnoreCase("second")== 0)
			c.add(Calendar.SECOND, tickcount);
		else if(tickType.compareToIgnoreCase("day") == 0)
			c.add(Calendar.DAY_OF_MONTH, tickcount);
		else if(tickType.compareToIgnoreCase("month") == 0)
			c.add(Calendar.MONTH, tickcount);
		else if(tickType.compareToIgnoreCase("year") == 0)
			c.add(Calendar.YEAR, tickcount);
		
		return new Timestamp(c.getTime().getTime());		
	}
	
	public static Timestamp GetCurrentUTCTime(){
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(gc.getTimeInMillis() - java.util.TimeZone.getDefault().getRawOffset());
		
		return new Timestamp(gc.getTimeInMillis());
	}
}
