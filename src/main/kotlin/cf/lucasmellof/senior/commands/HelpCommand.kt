package cf.lucasmellof.senior.commands

import cf.lucasmellof.senior.core.utils.category.InfoCog
import cf.lucasmellof.senior.core.utils.defaultColor
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class HelpCommand : InfoCog {
    @Command
    fun help(ctx: Context, @Greedy command: String?) {
        if (command == null) {
            return helpEmbed(ctx)
        }
        return helpCommand(ctx, command)
    }

    fun helpCommand(ctx: Context, value: String) {
        val cmdName = ctx.commandClient.commands.findCommandByName(value)
        val cmdAlias = ctx.commandClient.commands.findCommandByAlias(value)
        when {
            cmdName != null -> command(ctx, cmdName)
            cmdAlias != null -> command(ctx, cmdAlias)
            else -> helpEmbed(ctx)
        }
    }

    fun helpEmbed(ctx: Context) {
        val category = ctx.commandClient.commands.values.groupBy { it.category }
        ctx.send {
            setColor(defaultColor)
            setTitle("Listing all my commands")
            setDescription("For more information, use `!help {command}`")
            for (commands in category) {
                val command = commands.value.map { "``${it.name}``" }.toList().joinToString(", ")
                addField(commands.key, command, false)
            }
        }
    }

    fun command(ctx: Context, command: CommandFunction) {
        return ctx.send {
            setColor(defaultColor)
            setTitle(":bookmark_tabs: Help for the command `${command.name}`")
            addField("Description", "``" + command.properties.description + "``", false)
            if (!command.properties.aliases.isNullOrEmpty()) {
                addField("Aliases", "``" + command.properties.aliases.joinToString(", ") + "``", false)
            }
            addField("Owner only:", "``${if (command.properties.developerOnly) "Yes" else "No"}``", true)
            addField("Guild only:", "``${if (command.properties.guildOnly) "Yes" else "No"}``", true)
        }
    }
}