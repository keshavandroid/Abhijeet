package com.jeuxdevelopers.superchat.utils

import android.content.Context
import android.widget.Toast
import android.text.format.Time
import androidx.core.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {
    fun showShortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getDateFromMillis(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val format = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }


    fun getTimeFromMillis(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }


    // UTC to local and local to UTC
    fun localToUTC(time: Long): Long {
        try {
            val dateFormat =
                SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val date = Date(time)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val strDate = dateFormat.format(date)
            //            System.out.println("Local Millis * " + date.getTime() + "  ---UTC time  " + strDate);//correct
            val dateFormatLocal =
                SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val utcDate = dateFormatLocal.parse(strDate)
            //            System.out.println("UTC Millis * " + utcDate.getTime() + " ------  " + dateFormatLocal.format(utcDate));
            return utcDate.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return time
    }

    fun utcToLocal(utcTime: Long): Long {
        try {
            val timeFormat = Time()
            timeFormat.set(utcTime + TimeZone.getDefault().getOffset(utcTime))
            return timeFormat.toMillis(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return utcTime
    }


    fun localTimeToUtc(millis: Long): Long{
     val date = Date(millis - Calendar.getInstance().timeZone.getOffset(Date().time))
      return date.time
    }
    fun utcTimeToLocal(millis: Long): Long{
        val date = Date(millis + Calendar.getInstance().timeZone.getOffset(Date().time))
        return date.time
    }


    fun getTimeWithLast(millis: Long): String {
        val threeDayMillis: Long = 1000 * 60 * 60 * 24 * 3
        val twoDayMillis: Long = 1000 * 60 * 60 * 24 * 2
        val dayMillis: Long = 1000 * 60 * 60 * 24
        val hourMillis: Long = 1000 * 60 * 60 * 1
        val minuteMillis: Long = 1000 * 60 * 1
        val secondMillis: Long = 1000

        when (val millisDiffer = System.currentTimeMillis() - millis) {
            in secondMillis until minuteMillis -> {
                return "Just now"
            }
            in minuteMillis until hourMillis -> {
                return "${TimeUnit.MILLISECONDS.toMinutes(millisDiffer)} min ago"
            }
            in hourMillis until dayMillis -> {
                return "${TimeUnit.MILLISECONDS.toHours(millisDiffer)} hours ago"
            }
            in dayMillis until twoDayMillis -> {
                return "Today"
            }
//            in twoDayMillis until threeDayMillis -> {
//                return "Yesterday"
//            }
            else -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = millis
                val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                return dateFormat.format(calendar.time)
            }
        }

    }
}