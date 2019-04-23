package com.qlh.sdk.myview.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者：QLH on 2016/12/7 17:24
 * 描述：日期处理
 */
object DateUtil {

    private val MONTH_FORMAT = "yyyy-MM"
    private val DAY_FORMAT = "yyyy-MM-dd"
    private val HOUR_FORMAT = "yyyy-MM-dd HH"
    private val MINUTE_FORMAT = "yyyy-MM-dd HH:mm"
    private val SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private val MILL_SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss:SSSS"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    val currentTime: String
        get() {
            val cal = Calendar.getInstance()
            return formatDateToDay(cal.time)
        }


    /**
     * yyyy-MM-dd HH:mm:ss:SSSS  毫秒
     *
     * @return String
     */
    fun formatDateToMillSecond(time: Date): String {
        val format = SimpleDateFormat(MILL_SECOND_FORMAT)
        return format.format(time)

    }

    /**
     * yyyy-MM-dd HH:mm:ss 秒
     *
     * @return String
     */
    fun formatDateToSecond(time: Date): String {
        val format = SimpleDateFormat(SECOND_FORMAT)
        return format.format(time)

    }

    /**
     * yyyy-MM-dd HH:mm 分
     *
     * @return String
     */
    fun formatDateToMinutes(time: Date): String {
        val format = SimpleDateFormat(MINUTE_FORMAT)
        return format.format(time)

    }


    /**
     * yyyy-MM-dd HH:mm:ss 时
     *
     * @return String
     */
    fun formatDateToHour(time: Date): String {
        val format = SimpleDateFormat(HOUR_FORMAT)
        return format.format(time)

    }

    /**
     * yyyy-MM-dd  天
     *
     * @return String
     */
    fun formatDateToDay(time: Date): String {
        val format = SimpleDateFormat(DAY_FORMAT)
        return format.format(time)

    }

    /**
     * yyyy-MM   月
     *
     * @return String
     */
    fun formatDateToMonth(time: Date): String {
        val format = SimpleDateFormat(MONTH_FORMAT)
        return format.format(time)

    }

    /**
     *
     * 得到与当前日期偏移量为X的日期。
     * @param offset 天
     * @return String
     */
    fun getOffsetDate(offset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, offset)
        return formatDateToDay(cal.time)
    }

    /**
     * 将字符串时间如：2017-12-26格式化成long型
     */

    fun StringToDate(date: String): Long {
        val format = SimpleDateFormat(DAY_FORMAT)
        var d: Date? = null
        try {
            d = format.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return d!!.time
    }

}
