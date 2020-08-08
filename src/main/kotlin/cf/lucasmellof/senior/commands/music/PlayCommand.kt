package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.manager.database.DatabaseManager
import cf.lucasmellof.senior.core.manager.music.AudioLoader
import cf.lucasmellof.senior.core.manager.music.AudioUtils
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.guildManager
import cf.lucasmellof.senior.core.utils.extensions.selfMember
import cf.lucasmellof.senior.core.utils.extensions.voiceChannel
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import java.net.URL

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
class PlayCommand : MusicCog {
    override fun manager() = false

    @Command(guildOnly = true)
    fun play(ctx: Context, @Greedy args: String?) {
        val userChannel = ctx.voiceChannel ?: ctx.send("\uD83D\uDE1F Whoops, you are not connected to a voice channel!")
        val botChannel = ctx.selfMember!!.voiceState?.channel
        if (botChannel != null && botChannel != userChannel) return ctx.send("\uD83D\uDE1F Whoops, you are not connected to the channel that i'm currently playing in!")

        if (args == null) return resume(ctx)

        var result = args

        try {
            URL(result)
        } catch (e: Exception) {
            result = if (args.startsWith("soundcloud")) {
                val name = args.substring("soundcloud".length).trim { it <= ' ' }
                if (name.isEmpty()) {
                    ctx.send("Enter the URL or name of the song!")
                    return
                }
                "scsearch: $result"
            } else "ytsearch: $result"
        }
        if (AudioUtils.connect(ctx.textChannel, ctx.member)) AudioLoader.loadAndPlay(
            ctx.member!!,
            ctx.textChannel!!,
            result,
            ctx.message,
            false
        )
        return
    }

    private fun isDJ(member: Member): Boolean {
        val djRole = member.guild.getRolesByName("DJ", true).stream().findFirst().orElse(null)
        return member.isOwner || member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR) || djRole != null && member.roles.contains(
            djRole
        ) || member.roles.contains(member.guild.getRoleById(DatabaseManager(member.guild).getGuildData()!!.djRoleID))
    }

    private fun resume(ctx: Context) {
        val manager = ctx.guildManager
        if (manager != null && manager.audioPlayer.isPaused) {
            manager.audioPlayer.isPaused = false
            return ctx.send {
                setColor(defaultColor)
                setTitle("\uD83D\uDD0A Resume")
                setDescription("✅ You resumed the current song!")
            }
        }
    }
}