package com.bit.core.utils;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
	public static YearMonth dateToYearMonth(Date date) {
		return YearMonth.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
	}
	
	public static Date yearMonthToDate(YearMonth yearMonth) {
		return Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
