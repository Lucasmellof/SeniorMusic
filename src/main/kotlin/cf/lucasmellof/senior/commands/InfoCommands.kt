package cf.lucasmellof.senior.commands

import cf.lucasmellof.senior.Music
import cf.lucasmellof.senior.core.utils.Utils
import cf.lucasmellof.senior.core.utils.category.InfoCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.shardManager
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import kotlinx.coroutines.future.await
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import net.dv8tion.jda.api.JDAInfo


/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class InfoCommands : InfoCog {
    @Command
    suspend fun ping(ctx: Context) {
        val rest = ctx.jda.restPing.submit().await()
        return ctx.send {
            setColor(defaultColor)
            setTitle("Pong!")
            addField(":zap: Rest API", "__**${rest}ms**__", false)
            addField(":stopwatch: Gateway", "__**${ctx.jda.gatewayPing}ms**__", false)
        }
    }

    @Command
    fun stats(ctx: Context) {
        return ctx.send {
            setTitle("Bot status")
            setColor(defaultColor)
            addField(":information_desk_person: Users:", ctx.shardManager.users.size.toString(), true)
            addField(":page_with_curl: Text channels:", ctx.shardManager.textChannels.size.toString(), true)
            addField(":musical_note: Voice channels:", ctx.shardManager.voiceChannels.size.toString(), true)
            addField(":wave: Threads:", Thread.activeCount().toString(), true)
            addField(":diamond_shape_with_a_dot_inside: Shards", Music.shardCount.toString(), true)
            addField(":shield: Guilds", ctx.shardManager.guilds.size.toString(), true)
            addField(":ping_pong: Ping ", "${Music.averageShardLatency}ms", true)
            addField(":desktop:  JDA version:", "[${JDAInfo.VERSION}](${JDAInfo.GITHUB})", true)
            addField(
                ":vertical_traffic_light: Memory (USING/TOTAL):", Utils.convertToStringRepresentation(
                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                ) + " / " + Utils.convertToStringRepresentation(Runtime.getRuntime().maxMemory()), true
            )
            addField(":watch: Uptime:", Utils.getUptime(), true)
            addField(":musical_keyboard: Music Player", PlayerLibrary.VERSION, true)
        }
    }
}