package cf.lucasmellof.senior.core.utils

import java.lang.management.ManagementFactory
import java.text.DecimalFormat

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
object Utils {
    private const val K: Long = 1024
    private const val M = K * K
    private const val G = M * K
    private const val T = G * K
    fun convertToStringRepresentation(value: Long): String? {
        val dividers = longArrayOf(T, G, M, K, 1)
        val units = arrayOf("TB", "GB", "MB", "KB", "B")
        if (value < 1)
            throw IllegalArgumentException("Invalid file size: $value")
        var result: String? = null
        for (i in dividers.indices) {
            val divider = dividers[i]
            if (value >= divider) {
                result = format(value, divider, units[i])
                break
            }
        }
        return result
    }

    private fun format(value: Long, divider: Long, unit: String): String {
        val result = if (divider > 1) value.toDouble() / divider.toDouble() else value.toDouble()
        return DecimalFormat("#,##0.#").format(result) + " " + unit
    }

    fun getUptime(): String {
        val duration = ManagementFactory.getRuntimeMXBean().uptime
        val years = duration / 31104000000L
        val months = duration / 2592000000L % 12
        val days = duration / 86400000L % 30
        val hours = duration / 3600000L % 24
        val minutes = duration / 60000L % 60
        val seconds = duration / 1000L % 60
        var uptime =
            ((if (years == 0L) "" else "$years Years, ") + (if (months == 0L) "" else "$months Months, ")
                    + (if (days == 0L) "" else "$days Days, ") + (if (hours == 0L) "" else "$hours Hours, ")
                    + (if (minutes == 0L) "" else "$minutes Minutes, ") + if (seconds == 0L) "" else "$seconds Seconds")
        uptime = replaceLast(uptime, ", ", "")
        return replaceLast(uptime, ",", " e")
    }

    fun replaceLast(text: String, regex: String, replacement: String): String {
        return text.replaceFirst("(?s)(.*)" + regex.toRegex(), "$1$replacement")
    }
}