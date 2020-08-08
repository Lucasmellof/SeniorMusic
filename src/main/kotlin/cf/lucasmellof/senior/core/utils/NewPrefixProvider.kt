package cf.lucasmellof.senior.core.utils

import cf.lucasmellof.senior.core.manager.config.coreConfig
import cf.lucasmellof.senior.core.manager.database.DatabaseManager
import me.devoxin.flight.api.entities.PrefixProvider
import net.dv8tion.jda.api.entities.Message

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
class NewPrefixProvider : PrefixProvider {
    override fun provide(message: Message): List<String> {
        val guildSettings = DatabaseManager(message.guild)
        val prefixes = mutableListOf(
            "${message.jda.selfUser.name.toLowerCase()} ",
            "<@${message.jda.selfUser.id}> ",
            "<@!${message.jda.selfUser.id}> "
        )

        val customPrefix = guildSettings.getGuildData()?.prefix
            ?: coreConfig.defaultPrefix

        prefixes.add(customPrefix)
        return prefixes.toList()
    }
}