package cf.lucasmellof.senior.core.utils

import cf.lucasmellof.senior.Music
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import java.awt.Color
import java.lang.Math.round
import java.util.stream.Collectors
import java.util.stream.Stream

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
val defaultColor: Color = Color(179, 84, 213)

inline fun <reified E> Stream<E>.toTypedArray(): Array<E> {
    return this.collect(Collectors.toList()).toTypedArray()
}

fun Context.send(embed: EmbedBuilder) {
    this.messageChannel.sendMessage(embed.build()).queue()
}

fun getRegional(value: String): String {
    return when (value) {
        "0" -> "0⃣"
        "1" -> "1⃣"
        "2" -> "2⃣"
        "3" -> "3⃣"
        "4" -> "4⃣"
        "5" -> "5⃣"
        "6" -> "6⃣"
        "7" -> "7⃣"
        "8" -> "8⃣"
        "9" -> "9⃣"
        else -> value.replace("⃣", "")
    }
}

fun progressBar(current: Double, max: Double, size: Int): String {
    val percentage = current / max
    val progress = (size * percentage).toInt()
    val emptyProgress = (size - progress)

    val progressText = "\u25AC".repeat(progress)
    val emptyProgressText = "\u2015".repeat(emptyProgress)
    val percentageText = "${round(percentage * 100)}%"
    return "```[$progressText$emptyProgressText]$percentageText```"
}

//TODO: Add database to use custom roles
fun isDJ(member: Member): Boolean {
    val djRole = member.guild.getRolesByName("DJ", true).firstOrNull()
    return member.isOwner || member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR) || djRole != null && member.roles.contains(
        djRole
    ) || Music.commandClient.ownerIds.contains(member.idLong)
}

fun getTimestamp(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
        else -> String.format("%02d:%02d", minutes, seconds % 60)
    }
}