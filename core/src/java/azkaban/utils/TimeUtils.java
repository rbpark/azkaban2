package azkaban.utils;

import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 * A generally useless utils except that it parses duration
 */
public class TimeUtils {
	private TimeUtils() {
	}
	
	/**
	 * Parses a String and creates a ReadablePeriod 
	 * @param periodStr
	 * @return
	 */
	public static ReadablePeriod parsePeriodString(String periodStr) {
		ReadablePeriod period;
		char periodUnit = periodStr.charAt(periodStr.length() - 1);
		if (periodStr.equals("null") || periodUnit == 'n') {
			return null;
		}

		int periodInt = Integer.parseInt(periodStr.substring(0,
				periodStr.length() - 1));
		switch (periodUnit) {
		case 'y':
			period = Years.years(periodInt);
			break;
		case 'M':
			period = Months.months(periodInt);
			break;
		case 'w':
			period = Weeks.weeks(periodInt);
			break;
		case 'd':
			period = Days.days(periodInt);
			break;
		case 'h':
			period = Hours.hours(periodInt);
			break;
		case 'm':
			period = Minutes.minutes(periodInt);
			break;
		case 's':
			period = Seconds.seconds(periodInt);
			break;
		default:
			throw new IllegalArgumentException("Invalid schedule period unit '"
					+ periodUnit);
		}

		return period;
	}
	
	/**
	 * Creates a string from a readable period
	 * 
	 * @param period
	 * @return
	 */
	public static String createPeriodString(ReadablePeriod period) {
		String periodStr = "null";

		if (period == null) {
			return "null";
		}

		if (period.get(DurationFieldType.years()) > 0) {
			int years = period.get(DurationFieldType.years());
			periodStr = years + "y";
		} 
		else if (period.get(DurationFieldType.months()) > 0) {
			int months = period.get(DurationFieldType.months());
			periodStr = months + "M";
		} 
		else if (period.get(DurationFieldType.weeks()) > 0) {
			int weeks = period.get(DurationFieldType.weeks());
			periodStr = weeks + "w";
		} 
		else if (period.get(DurationFieldType.days()) > 0) {
			int days = period.get(DurationFieldType.days());
			periodStr = days + "d";
		} 
		else if (period.get(DurationFieldType.hours()) > 0) {
			int hours = period.get(DurationFieldType.hours());
			periodStr = hours + "h";
		} 
		else if (period.get(DurationFieldType.minutes()) > 0) {
			int minutes = period.get(DurationFieldType.minutes());
			periodStr = minutes + "m";
		} 
		else if (period.get(DurationFieldType.seconds()) > 0) {
			int seconds = period.get(DurationFieldType.seconds());
			periodStr = seconds + "s";
		}

		return periodStr;
	}
	
	/**
	 * Takes the start and end time, and returns a pretty print
	 * version of the duration for display purpose only.
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String formatDuration(long startTime, long endTime) {
		if (startTime == -1) {
			return "-";
		}
		
		long durationMS;
		if (endTime == -1) {
			durationMS = System.currentTimeMillis() - startTime;
		}
		else {
			durationMS = endTime - startTime;
		}
		
		long seconds = durationMS/1000;
		if (seconds < 60) {
			return seconds + " sec";
		}
		
		long minutes = seconds / 60;
		seconds %= 60;
		if (minutes < 60) {
			return minutes + "m " + seconds + "s";
		}
		
		long hours = minutes / 60;
		minutes %= 60;
		if (hours < 24) {
			return hours + "h " + minutes + "m " + seconds + "s";
		}
		
		long days = hours / 24;
		hours %= 24;
		return days + "d " + hours + "h " + minutes + "m";
	}
}
