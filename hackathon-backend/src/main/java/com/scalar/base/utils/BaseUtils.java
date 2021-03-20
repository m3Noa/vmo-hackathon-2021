package com.scalar.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.scalar.base.BaseConstant;
import com.scalar.base.model.StatusSyncData;

public class BaseUtils {

	/**
	 * Validate is empty string
	 * @param str
	 * @return
	 */
	public static boolean isEmptyString(String str) {
		if(str != null && !str.trim().isEmpty()) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Turn on status change data
	 */
	public static void turnOnStatus() {
		StatusSyncData.statusId = UUID.randomUUID().toString();
	}
	
	/**
	 * Calculating days between two dates
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long caculatorDateDiff(String startDate, String endDate) {
		long dateBetween = 0;
		try {
			Date day1 = BaseUtils.stringToDate(startDate);
			Date day2 = BaseUtils.stringToDate(endDate);
			
			long millisecondsDiff = day2.getTime() - day1.getTime();
			dateBetween = TimeUnit.DAYS.convert(millisecondsDiff, TimeUnit.MILLISECONDS);
			return dateBetween;
		} catch (ParseException e) {
			return dateBetween;
		}
	}
	
	/**
	 * Convert date to string with custom format
	 * @param date
	 * @return String
	 */
	public static String dateToStringFormat(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Convert string with format [ApplianceConstant.DATE_FORMAT] to date
	 * @return Date
	 * @throws ParseException 
	 */
	public static Date stringToDate(String dateString) throws ParseException {
		SimpleDateFormat sDf = new SimpleDateFormat(BaseConstant.DATE_FORMAT);
		Date date = sDf.parse(dateString);
		return date;
	}
}
