package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.manager.music.TrackScheduler
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class RepeatCommand : MusicCog {
    override fun playingTrack() = true
    override fun sameChannel() = true

    @Command(guildOnly = true)
    fun repeat(ctx: Context, @Greedy value: String) {
        val manager = ctx.guildManager!!
        if (value == null) {
            return ctx.send { }
        }
        when (value) {
            "stop", "st" -> {
                manager.trackScheduler.repeatMode = TrackScheduler.RepeatMode.OFF
                return ctx.send("✅ You disabled repeat mode.")
            }
            "song", "s" -> {
                manager.trackScheduler.repeatMode = TrackScheduler.RepeatMode.SONG
                return ctx.send("✅ You changed the repeat mode of this guild to ``Song``.")
            }
        }
    }
}