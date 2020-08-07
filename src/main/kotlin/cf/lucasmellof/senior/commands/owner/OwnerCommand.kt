package cf.lucasmellof.senior.commands.owner

import cf.lucasmellof.senior.Music
import cf.lucasmellof.senior.core.utils.category.OwnerCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.shardManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import kotlin.system.exitProcess

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class OwnerCommand : OwnerCog {
    @Command(developerOnly = true)
    fun restart(ctx: Context, @Greedy value: Int?) {
        ctx.send {
            setColor(defaultColor)
            setTitle(":arrows_counterclockwise: Restart")
            setDescription("Restarting shard")
        }
        Thread.sleep(1000)
        return if (value == null) Music.shardManager.restart() else Music.shardManager.restart(value)
        exitProcess(10)
    }

    @Command(developerOnly = true)
    fun shutdown(ctx: Context, @Greedy value: Int?) {
        ctx.send {
            setColor(defaultColor)
            setTitle(":stop_button: Shutdown")
            setDescription("Turning off the bot")
        }
        Thread.sleep(1000)
        return if (value == null) Music.shardManager.restart() else Music.shardManager.restart(value)
        exitProcess(20)
    }

    @Command(developerOnly = true)
    fun shard(ctx: Context) {
        return ctx.send {
            setColor(defaultColor)
            setTitle("Shard info")
            setDescription("Total | Servers: ${Music.shardManager.guilds.size}, Users: ${Music.shardManager.users.size}, Average latency: ${Music.averageShardLatency}ms")
            for (shard in ctx.shardManager.shards.reversed()) {
                addField(
                    "Shard ${shard.shardInfo.shardId} ${shard.status.name} ${if (ctx.guild!!.jda.shardInfo.shardId == shard.shardInfo.shardId) "(actual)" else ""}",
                    """
                    ${shard.guilds.size} Guilds
                    ${shard.users.size} Users
                    Gateway ${shard.gatewayPing}ms
                    Rest ${shard.restPing.complete()}ms
                """.trimIndent(),
                    true
                )
            }
        }
    }
}