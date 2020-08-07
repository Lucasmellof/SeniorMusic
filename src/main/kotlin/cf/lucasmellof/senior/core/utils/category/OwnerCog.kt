package cf.lucasmellof.senior.core.utils.category

import cf.lucasmellof.senior.core.utils.extensions.user
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.entities.Cog

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
interface OwnerCog : Cog {
    override fun name() = ":crystal_ball: | ➥ Owner"
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        val user = ctx.user!!
        if (!ctx.commandClient.ownerIds.contains(user.idLong)) {
            ctx.send("\uD83D\uDE1F Whoops, you aren't my owner!")
            return false
        }
        return true
    }
}