package cf.lucasmellof.senior

import com.jagrosh.jdautilities.waiter.EventWaiter
import me.devoxin.flight.api.CommandClient
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import java.util.*

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
object Music {
    lateinit var shardManager: ShardManager
    lateinit var commandClient: CommandClient
    lateinit var eventWaiter: EventWaiter

    val shardCount by lazy {
        shardManager.shardsTotal
    }

    val averageShardLatency by lazy {
        shardManager.shards
            .stream()
            .map { shard -> shard.gatewayPing }
            .reduce { a, b -> a + b }
            .get() / shardCount
    }

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) {
        setupFlight()
        setupEventWaiter()
        loadShardManager()

    }

    private fun loadShardManager() {
        shardManager = DefaultShardManagerBuilder.create(EnumSet.allOf(GatewayIntent::class.java))
            .setToken("")
            .build()
    }

    @ExperimentalStdlibApi
    fun setupFlight() {
        commandClient = CommandClientBuilder()
            .setPrefixes("!")
            .registerDefaultParsers()
            .setOwnerIds("")
            .configureDefaultHelpCommand { enabled = false }
            .build()
        commandClient.commands.register("cf.lucasmellof.senior.commands")
        commandClient.commands
    }

    private fun setupEventWaiter() {
        eventWaiter = EventWaiter()
    }
}