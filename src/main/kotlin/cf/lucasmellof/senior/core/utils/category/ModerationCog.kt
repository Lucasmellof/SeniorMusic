package cf.lucasmellof.senior.core.utils.category

import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
interface ModerationCog : Cog {
    override fun name() = ":lock: | ➥ Moderation"
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        val member = ctx.member!!
        if (ctx.commandClient.ownerIds.contains(member.idLong)) return true
        if (!member.isOwner || !member.hasPermission(Permission.MANAGE_SERVER) || !member.hasPermission(Permission.ADMINISTRATOR)) {
            ctx.send("\uD83D\uDE1F Whoops, You must have permission to execute this command!")
            return false
        }
        return true
    }
}