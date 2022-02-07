package com.teamayka.smsadminsuper.utils

import android.text.format.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object CalendarUtils {

    private const val DAY_FORMAT = "dd"
    const val MONTH_FORMAT = "MM"
    const val YEAR_FORMAT = "yyyy"
    const val DATE_SEPARATOR_FORMAT = "-"
    const val DATE_FORMAT =
        "$DAY_FORMAT$DATE_SEPARATOR_FORMAT$MONTH_FORMAT$DATE_SEPARATOR_FORMAT$YEAR_FORMAT"

    private const val HOUR_FORMAT = "hh"
    private const val MINUTE_FORMAT = "mm"
    private const val SECOND_FORMAT = "ss"
    private const val AM_PM_FORMAT = "a"
    private const val TIME_SEPARATOR_FORMAT = ":"
    const val TIME_FORMAT = "$HOUR_FORMAT$TIME_SEPARATOR_FORMAT$MINUTE_FORMAT $AM_PM_FORMAT"

    fun getCurrentDateInMillis(): String {
        return System.currentTimeMillis().toString()
    }

    fun getCurrentDateInFormat(): String {
        val currentDate = Calendar.getInstance().time
        val df = SimpleDateFormat(DATE_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        return df.format(currentDate)
    }

    fun getCurrentMonthIndex(): Int {
        val currentDate = Calendar.getInstance().time
        val df = SimpleDateFormat(MONTH_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        return df.format(currentDate).toInt()
    }

    fun getCurrentYear(): Int {
        val currentDate = Calendar.getInstance().time
        val df = SimpleDateFormat(YEAR_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        return df.format(currentDate).toInt()
    }


    fun dateToMillis(dateString: String): Long {
        val df = SimpleDateFormat(DATE_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val date = df.parse(dateString)
        return date.time
    }

    fun millisToDate(dateInMillis: String): String {
        val df = SimpleDateFormat(DATE_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis.toLong()
        return df.format(calendar.time)
    }

    fun timeToMillis(dateString: String): Long {
        val df = SimpleDateFormat(TIME_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val date = df.parse(dateString)
        return date.time
    }

    fun millisToMonth(dateInMillis: String): String {
        val df = SimpleDateFormat(MONTH_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis.toLong()
        return df.format(calendar.time)
    }

    fun millisToYear(dateInMillis: String): String {
        val df = SimpleDateFormat(YEAR_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis.toLong()
        return df.format(calendar.time)
    }

    fun millisToTime(dateInMillis: String): String {
        val df = SimpleDateFormat(TIME_FORMAT, Locale.US)
//        df.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis.toLong()
        return df.format(calendar.time)
    }

    fun getCurrentTimeInFormat(): String {
        val currentDate = Calendar.getInstance().time
        val df = SimpleDateFormat(TIME_FORMAT, Locale.US)
//        df.timeZone = TimeZone.getDefault() // this does not provide proper result
        return df.format(currentDate)
    }

    fun addOneDay(d: String): String {
        val df = SimpleDateFormat(DATE_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance()
        calendar.time = df.parse(d)
        calendar.add(Calendar.DATE, 1)
        return df.format(calendar.time)
    }

    fun dateToReadable(currentDate: String, date: String): String {
        val cc = Calendar.getInstance()
        cc.timeInMillis = currentDate.toLong()

        val d = Calendar.getInstance()
        d.timeInMillis = date.toLong()

        if (isToday(cc, d)) {
            return "Today"
        }
        if (isYesterday(currentDate.toLong(), date.toLong())) {
            return "Yesterday"
        }

        return "${getDayDifference(cc, d)} days ago"
    }

    fun isToday(cal1: Calendar, cal2: Calendar): Boolean {
        val df = SimpleDateFormat(DATE_FORMAT, Locale.US)
        df.timeZone = TimeZone.getDefault()
        val c1 = df.format(cal1.time)
        val c2 = df.format(cal2.time)
        return c1 == c2
    }

    fun isYesterday(currentDate: Long, date: Long): Boolean {
        val time = Time()
        time.set(date)

        val thenYear = time.year
        val thenMonth = time.month
        val thenMonthDay = time.monthDay

        time.set(currentDate - 86400000)
        return (thenYear == time.year
                && thenMonth == time.month
                && thenMonthDay == time.monthDay)
    }

    fun getDayDifference(currentDate: Calendar, date: Calendar): Long {
        val msDiff = currentDate.timeInMillis - date.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(msDiff) + 1
    }
}