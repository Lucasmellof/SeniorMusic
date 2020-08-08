package cf.lucasmellof.senior.commands

import cf.lucasmellof.senior.core.manager.config.coreConfig
import cf.lucasmellof.senior.core.manager.database.DatabaseManager
import cf.lucasmellof.senior.core.manager.database.Guild
import cf.lucasmellof.senior.core.utils.category.ModerationCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.data
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.annotations.SubCommand
import net.dv8tion.jda.api.entities.Role
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
class ModerationCommands : ModerationCog {
    @Command
    fun settings(ctx: Context) {
        val guild = ctx.data
        var prefix = guild?.djRoleID ?: "Default"
        if (prefix == "") prefix = "Default"
        return ctx.send {
            setColor(defaultColor)
            setTitle("\ufe0f Guild configuration")
            setDescription("You can change this setting using ${ctx.trigger}settings [key] [value]")
            addField("\uD83D\uDD11 Prefix", "``${guild?.prefix ?: coreConfig.defaultPrefix}``", false)
            addField("\uD83C\uDFA7 Dj Role", "``${prefix}``", false)
        }
    }

    @SubCommand
    fun prefix(ctx: Context, @Greedy value: String?) {
        val current = ctx.data?.prefix ?: "!"
        if (value == null) {
            return ctx.send("\uD83D\uDD11 My prefix is ${current}")
        }
        if (value.matches(Regex("\"<@!?(\\d+)>|<#(\\d+)>|<@&(\\d+)>\""))) {
            return ctx.send("\uD83D\uDE1F Whoops, you cannot define my prefix for a mention!")
        }
        if (value == current) {
            return ctx.send("\uD83D\uDE1F Whoops, my prefix is already $current!")
        }
        DatabaseManager.guildPrefixes[ctx.guild!!.id] = value
        DatabaseManager.guilds.updateOne(
            Guild::id eq ctx.guild!!.id, setValue(
                Guild::prefix, value
            )
        )
        ctx.send("✅ You have succesfully changed the guild prefix to ``${value}``.")
    }

    @SubCommand(aliases = ["dj"])
    fun djrole(ctx: Context, @Greedy role: Role?) {
        DatabaseManager.guilds.updateOne(
            Guild::id eq ctx.guild!!.id, setValue(
                Guild::djRoleID, role?.idLong
            )
        )
        val result = role?.let { it.asMention } ?: "default"
        ctx.send("✅ You have succesfully changed the guild dj role to ``$result``.")
    }
}