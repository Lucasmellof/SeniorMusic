package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import cf.lucasmellof.senior.core.utils.isDJ
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class StopCommand : MusicCog {
    override fun sameChannel() = true

    @Command(guildOnly = true)
    fun stop(ctx: Context) {
        return if (isDJ(ctx.member!!)) {
            val manager = ctx.guildManager!!
            manager.trackScheduler.stop()
            ctx.send("✅ You cleared ``${manager.trackScheduler.queue.size}`` songs from the queue.")
        } else {
            ctx.send("You need to be a DJ to use this command.")
        }
    }
}