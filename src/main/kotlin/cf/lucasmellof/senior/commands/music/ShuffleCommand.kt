package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import cf.lucasmellof.senior.core.utils.extensions.send
import cf.lucasmellof.senior.core.utils.isDJ
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import java.util.function.Consumer

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class ShuffleCommand {
    @Command
    fun shuffle(ctx: Context) {
        displayShuffle(ctx)
    }

    companion object {
        fun displayShuffle(ctx: Context): String {
            var id = ""
            return if (isDJ(ctx.member!!)) {
                val manager = ctx.guildManager!!
                manager.trackScheduler.shuffle()
                ctx.send({
                    setTitle(":twisted_rightwards_arrows: ")
                    setColor(defaultColor)
                    setDescription("You shuffled the queue.")
                }, Consumer {
                    id = it.id
                })
                id
            } else {
                ctx.send("You need to be a DJ to use this command.")
                id
            }
        }
    }
}