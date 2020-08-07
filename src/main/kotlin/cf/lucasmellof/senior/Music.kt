package cf.lucasmellof.senior

import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import java.util.*

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
object Music {
    lateinit var shardManager: ShardManager

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
        loadShardManager()

    }

    private fun loadShardManager() {
        shardManager = DefaultShardManagerBuilder.create(EnumSet.allOf(GatewayIntent::class.java))
            .setToken("")
            .build()
    }

}