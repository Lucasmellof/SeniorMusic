package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class SkipCommand : MusicCog {
    override fun sameChannel() = true

    @Command(guildOnly = true)
    fun skip(ctx: Context) {
        val manager = ctx.guildManager

        val scheduler = manager!!.trackScheduler
        val track = scheduler.currentTrack!!

        if (track.dj == ctx.member!!.id) {
            scheduler.startNext(true)
            return ctx.send(" :musical_note: The requester of this song, ``${ctx.member!!.effectiveName}``, has requested to skip this song!")
        }
        return scheduler.computeVote(ctx.member!!, ctx.textChannel!!)
    }
}