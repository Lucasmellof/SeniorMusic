package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.manager.music.AudioUtils
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
class NowPlayingCommand : MusicCog {
    override fun playingTrack() = true

    @Command(guildOnly = true, aliases = ["np"])
    fun nowplaying(ctx: Context) {
        val manager = ctx.guildManager!!
        val trackContext = manager.trackScheduler.currentTrack!!
        val track = trackContext.track

        val volume = manager.audioPlayer.volume

        ctx.send {
            setColor(defaultColor)
            setTitle("\uD83C\uDFB6 Now Playing")
            setDescription("**[${track.info.title}](${track.info.uri})**")
            addField("\uD83D\uDC81 Requester", ctx.jda.getUserById(trackContext.dj)!!.asMention, false)
            addField("\uD83D\uDCC3 Request channel", trackContext.channel.asMention, false)
            addField("\uD83D\uDD01 Repeating", manager.trackScheduler.repeatMode.name, true)
            addField("${AudioUtils.volumeIcon(volume)} Volume", "$volume%", true)
            val timeString = if (track.duration == Long.MAX_VALUE) {
                "`Streaming`"
            } else {
                val position = AudioUtils.formatTime(track.position)
                val duration = AudioUtils.formatTime(track.duration)
                "`[$position / $duration]`"
            }
            addField("\uD83D\uDD50 Time", timeString, false)
            addField("\u25b6 Progress", AudioUtils.getProgressBar(track.position, track.duration), false)
        }
    }
}