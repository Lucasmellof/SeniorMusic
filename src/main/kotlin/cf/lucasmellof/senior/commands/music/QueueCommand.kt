package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.Music
import cf.lucasmellof.senior.core.manager.music.GuildMusicManager
import cf.lucasmellof.senior.core.manager.music.TrackScheduler
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.eventWaiter
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import cf.lucasmellof.senior.core.utils.getTimestamp
import com.jagrosh.jdautilities.paginator
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class QueueCommand : MusicCog {
    override fun playingTrack() = true

    @Command(guildOnly = true)
    fun queue(ctx: Context) {
        val manager = ctx.guildManager
        displayQueue(ctx, manager)
    }

    companion object {
        fun displayQueue(ctx: Context, manager: GuildMusicManager?): String {
            var id = ""
            val queue = manager!!.trackScheduler.queue

            var queueLength = 0L

            val paginator = ctx.eventWaiter.paginator {
                setColor(defaultColor)
                setTitle("Current queue")
                setEmptyMessage("\uD83D\uDE1F Whoops, there are no songs in the queue!")
                finally { it?.delete()?.queue() }
                for (trackContext in queue) {
                    var position = 1
                    val track = trackContext.track
                    val dj = Music.shardManager.getUserById(trackContext.dj)!!.name
                    entry {
                        buildString {
                            append("`[")
                            append(getTimestamp(track.duration))
                            append("]`**[")
                            append(track.info.title)
                            append("](")
                            append(track.info.uri)
                            append(")** added by")
                            append("**")
                            append(dj)
                            append("**")
                            position += 1
                        }
                    }
                    queueLength += track.duration
                }
                field(":musical_note: Current Song: ", false) {
                    val track = manager.trackScheduler.currentTrack
                    if (track == null) "Nothing" else "**[${track.track.info.title}](${track.track.info.uri})**"
                }
                field("Queue size:", true) { queue.size }
                field("Queue Duration", true) { getTimestamp(queueLength) }
                field(
                    "Is repeating",
                    true
                ) { if (manager.trackScheduler.repeatMode == TrackScheduler.RepeatMode.OFF) "no" else "yes" }
            }
            ctx.textChannel.let { id = paginator.display(it!!) }
            return id
        }
    }
}