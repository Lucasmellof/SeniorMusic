package cf.lucasmellof.senior.core.manager.music

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import java.util.concurrent.TimeUnit

object AudioUtils {
    private const val BLOCK_INACTIVE: String = "\u25AC"
    private const val BLOCK_ACTIVE: String = "\uD83D\uDD18"
    private const val TOTAL_BLOCKS = 10
    fun isAlone(vc: VoiceChannel): Boolean {
        return vc.members.stream()
            .filter { m: Member? ->
                !m!!.user.isBot
            }.count() == 0L
    }

    fun getProgressBar(percent: Long, duration: Long): String? {
        val activeBlocks = (percent.toFloat() / duration * TOTAL_BLOCKS).toInt()
        val builder = StringBuilder()
        for (i in 0 until TOTAL_BLOCKS) builder.append(if (activeBlocks == i) BLOCK_ACTIVE else BLOCK_INACTIVE)
        return builder.append(BLOCK_INACTIVE).toString()
    }

    fun format(length: Long): String? {
        val hours = length / 3600000L % 24
        val minutes = length / 60000L % 60
        val seconds = length / 1000L % 60
        return ((if (hours == 0L) "" else octal(hours) + ":")
                + (if (minutes == 0L) "00" else octal(minutes)) + ":" + if (seconds == 0L) "00" else octal(
            seconds
        ))
    }

    fun octal(num: Long): String? {
        return if (num > 9) num.toString() else "0$num"
    }

    fun connect(tc: TextChannel?, member: Member?): Boolean {
        if (tc!!.guild.audioManager.isConnected || tc.guild.audioManager.isAttemptingToConnect) {
            while (!tc.guild.audioManager.isConnected) {
                try {
                    Thread.sleep(150)
                } catch (ignored: InterruptedException) {
                }
            }
            if (tc.guild.audioManager.connectedChannel != member!!.voiceState!!.channel) {
                tc.sendMessage("\uD83D\uDE1F Whoops, you are not connected to the channel that i'm currently playing in!")
                    .queue()
                return false
            }
            return true
        }
        if (!member!!.voiceState!!.inVoiceChannel()) {
            tc.sendMessage("\uD83D\uDE1F Whoops, you are not connected to a voice channel!").queue()
            return false
        }
        val vc = member.voiceState!!.channel
        if (!tc.guild.selfMember.hasPermission(vc!!, Permission.VOICE_CONNECT)) {
            tc.sendMessage("\uD83D\uDE1F Whoops, looks like I don't have permission to join that voice channel!")
                .queue()
            return false
        } else if (vc.userLimit > 0 && vc.members.size > vc.userLimit && !tc.guild
                .selfMember.hasPermission(Permission.MANAGE_CHANNEL)
        ) {
            tc.sendMessage("\uD83D\uDE1F Whoops, looks like I can't join that voice channel because it's full!").queue()
            return false
        }
        val sent =
            tc.sendMessage(String.format("Connecting in ` %s `...", vc.name)).submit().join()
        tc.guild.audioManager.openAudioConnection(vc)
        sent!!.editMessage(":white_check_mark: Connected.").queue { it.delete().queueAfter(5, TimeUnit.SECONDS) }
        return true
    }

    fun volumeIcon(volume: Int): String? {
        if (volume == 0) return "\uD83D\uDD07"
        if (volume < 30) return "\uD83D\uDD08"
        return if (volume < 70) "\uD83D\uDD09" else "\uD83D\uDD0A"
    }

    fun formatTime(duration: Long): String? {
        if (duration == Long.MAX_VALUE) return "LIVE"
        var seconds = Math.round(duration / 1000.0)
        val hours = seconds / (60 * 60)
        seconds %= 60 * 60.toLong()
        val minutes = seconds / 60
        seconds %= 60
        return (if (hours > 0) "$hours:" else "") + (if (minutes < 10) "0$minutes" else minutes) + ":" + if (seconds < 10) "0$seconds" else seconds
    }
}