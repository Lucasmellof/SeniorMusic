package cf.lucasmellof.senior

import cf.lucasmellof.senior.core.manager.EventManager
import cf.lucasmellof.senior.core.manager.config.coreConfig
import cf.lucasmellof.senior.core.manager.music.MusicManager
import com.jagrosh.jdautilities.waiter.EventWaiter
import me.devoxin.flight.api.CommandClient
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
object Music {
    val logger = LoggerFactory.getLogger(Music::class.java)
    lateinit var shardManager: ShardManager
    lateinit var commandClient: CommandClient
    lateinit var eventWaiter: EventWaiter
    lateinit var musicManager: MusicManager

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
        if (coreConfig.token == "<change>") {
            logger.error("Change the token in the configuration!")
            exitProcess(1)
        }
        setupFlight()
        setupEventWaiter()
        setupAudio()
        loadShardManager()

    }

    private fun loadShardManager() {
        shardManager = DefaultShardManagerBuilder.create(EnumSet.allOf(GatewayIntent::class.java))
            .setToken(coreConfig.token)
            .addEventListeners(commandClient, EventManager(), eventWaiter)
            .build()
    }

    @ExperimentalStdlibApi
    fun setupFlight() {
        commandClient = CommandClientBuilder()
            .setPrefixes("!")
            .registerDefaultParsers()
            .setOwnerIds(*coreConfig.ownerIDs)
            .configureDefaultHelpCommand { enabled = false }
            .build()
        commandClient.commands.register("cf.lucasmellof.senior.commands")
        commandClient.commands
    }

    private fun setupEventWaiter() {
        eventWaiter = EventWaiter()
    }

    private fun setupAudio() {
        musicManager = MusicManager()
    }
}