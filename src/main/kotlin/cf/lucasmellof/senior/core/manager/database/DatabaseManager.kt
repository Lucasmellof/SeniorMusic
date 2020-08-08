package cf.lucasmellof.senior.core.manager.database

import cf.lucasmellof.senior.core.manager.config.coreConfig
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.api.entities.Guild
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis
import cf.lucasmellof.senior.core.manager.database.Guild as dbGuild

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
class DatabaseManager(guild: Guild) {

    private val id = guild.id

    fun getGuildData(): dbGuild? {
        return guilds.findOne(dbGuild::id eq id)
    }

    companion object {
        private lateinit var client: MongoClient
        private lateinit var db: MongoDatabase
        lateinit var guilds: MongoCollection<dbGuild>
        var guildPrefixes = mutableMapOf<String, String>()

        fun initialize() {
            val logger = LoggerFactory.getLogger(DatabaseManager::class.java)
            logger.info("Connecting to the database... ")
            val milli = measureTimeMillis {

                client = KMongo.createClient(ConnectionString(coreConfig.mongoURL))
                db = client.getDatabase(coreConfig.mongodbName)
                guilds = db.getCollection<dbGuild>("guilds")
                val allGuilds = guilds.find("{}")
                allGuilds.forEach { guildPrefixes[it.id] = it.prefix }
            }
            logger.info("Done! (${milli}ms)")
        }
    }
}
