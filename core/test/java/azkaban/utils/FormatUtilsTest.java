package azkaban.utils;
import static org.junit.Assert.*;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.junit.Test;

public class FormatUtilsTest {
	@Test
	public void testConvertToDouble() {
		assertEquals(new Double(1.0d), FormatUtils.convertToDouble("1"));
		assertEquals(new Double(1.0d), FormatUtils.convertToDouble(1));
		assertEquals(new Double(1.0d), FormatUtils.convertToDouble(1L));
		assertEquals(new Double(1.0d), FormatUtils.convertToDouble(1.0));
		assertEquals(new Double(1.0d), FormatUtils.convertToDouble(1.0d));
		
		assertEquals(new Double(Double.NaN), FormatUtils.convertToDouble("NaN"));
	}
	
	@Test
	public void testConvertToLong() {
		assertEquals(new Long(1l), FormatUtils.convertToLong("1"));
		assertEquals(new Long(1l), FormatUtils.convertToLong(1));
		assertEquals(new Long(1l), FormatUtils.convertToLong(1.0));
		assertEquals(new Long(1l), FormatUtils.convertToLong(1.0d));
	}
	
	@Test
	public void testParsePeriodString() {
		ReadablePeriod yPeriod = FormatUtils.parsePeriodString("10y");
		assertEquals(Years.years(10), yPeriod);
		
		ReadablePeriod MPeriod = FormatUtils.parsePeriodString("101M");
		assertEquals(Months.months(101), MPeriod);
		
		ReadablePeriod dPeriod = FormatUtils.parsePeriodString("2d");
		assertEquals(Days.days(2), dPeriod);
		
		ReadablePeriod hPeriod = FormatUtils.parsePeriodString("16h");
		assertEquals(Hours.hours(16), hPeriod);
		
		ReadablePeriod mPeriod = FormatUtils.parsePeriodString("55m");
		assertEquals(Minutes.minutes(55), mPeriod);
		
		ReadablePeriod sPeriod = FormatUtils.parsePeriodString("45s");
		assertEquals(Seconds.seconds(45), sPeriod);
		
		// Test NULL
		ReadablePeriod period = FormatUtils.parsePeriodString("null");
		assertNull(period);
		period = FormatUtils.parsePeriodString("n");
		assertNull(period);
	}
	
	@Test
	public void testCreatePeriodString() {
		assertEquals("10y", FormatUtils.createPeriodString(Years.years(10)));
		assertEquals("101M", FormatUtils.createPeriodString(Months.months(101)));
		assertEquals("2d", FormatUtils.createPeriodString(Days.days(2)));
		assertEquals("16h", FormatUtils.createPeriodString(Hours.hours(16)));
		assertEquals("55m", FormatUtils.createPeriodString(Minutes.minutes(55)));
		assertEquals("45s", FormatUtils.createPeriodString(Seconds.seconds(45)));
	}
	
	@Test
	public void testDurationString() {
		long secMs = 1000;
		long minMs = 60*secMs;
		long hourMs = 60*minMs;
		long dayMs = 24*hourMs;
		
		assertEquals("0 sec", FormatUtils.formatDuration(0, 55));
		assertEquals("4 sec", FormatUtils.formatDuration(0, 4*secMs + 100));
		assertEquals("10m 1s", FormatUtils.formatDuration(0, 10*minMs + secMs + 250));
		
		assertEquals("23h 10m 55s", 
				FormatUtils.formatDuration(0, 23*hourMs + 10*minMs + 55*secMs + 250));
		
		assertEquals("23h 0m 55s", 
				FormatUtils.formatDuration(0, 23*hourMs + 0*minMs + 55*secMs + 250));
		
		assertEquals("5d 3h 4m", 
				FormatUtils.formatDuration(0, 5*dayMs + 3*hourMs + 4*minMs + 55*secMs + 250));
		
		assertEquals("15d 3h 14m", 
				FormatUtils.formatDuration(0, 15*dayMs + 3*hourMs + 14*minMs + 55*secMs + 250));
		
		assertEquals("150d 3h 4m", 
				FormatUtils.formatDuration(0, 150*dayMs + 3*hourMs + 4*minMs + 55*secMs + 250));
	}
}
