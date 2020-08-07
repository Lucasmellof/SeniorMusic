package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class PauseCommand : MusicCog {
    override fun sameChannel() = true

    @Command(guildOnly = true, aliases = ["unpause"])
    fun pause(ctx: Context) {
        val manager = ctx.guildManager!!.audioPlayer

        manager.isPaused = manager.isPaused.not()

        return ctx.send {
            setColor(defaultColor)
            setTitle("\uD83D\uDD0A ${if (manager.isPaused) "Pause" else "Resume"}")
            setDescription("✅ You ${if (manager.isPaused) "paused" else "resumed"} the current song!")
        }
    }
}