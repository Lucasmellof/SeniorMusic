package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.manager.music.AudioUtils
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import cf.lucasmellof.senior.core.utils.extensions.send
import cf.lucasmellof.senior.core.utils.progressBar
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import java.util.function.Consumer

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class VolumeCommand : MusicCog {
    override fun sameChannel() = true
    override fun player() = true

    @Command(guildOnly = true)
    fun volume(ctx: Context, @Greedy value: Int?) {
        displayVolume(ctx, value)
    }

    companion object {
        fun displayVolume(ctx: Context, value: Int?): String {
            var id = ""
            if (value == null) {
                val volume = ctx.guildManager!!.audioPlayer.volume
                val progress = progressBar(volume.toDouble(), 100.0, 20)
                ctx.send({
                    setColor(defaultColor)
                    setTitle("\uD83D\uDD0A Volume")
                    setDescription(progress)
                    setFooter("Set the volume using: ${ctx.trigger}volume [5-100] ")
                }, Consumer { id = it.id })
                return id
            }
            val new = value in (5 until 100)
            if (!new) {
                ctx.send({
                    setColor(defaultColor)
                    setTitle("\uD83D\uDD0A Volume")
                    setDescription("\uD83D\uDE1F Whoops, the value needs to be greater than ``5`` and less than ``110``")
                }, Consumer { id = it.id })
                return id
            }

            val progress = progressBar(value.toDouble(), 100.0, 20)
            ctx.guildManager!!.audioPlayer.volume = value
            //TODO: DATABASE
            ctx.send({
                setColor(defaultColor)
                setTitle("${AudioUtils.volumeIcon(value)} Volume")
                setDescription(progress)
                setFooter("✅ You changed the music player's volume to $value ")
            }, Consumer { id = it.id })
            return id
        }
    }
}