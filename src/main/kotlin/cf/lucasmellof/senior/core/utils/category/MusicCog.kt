package cf.lucasmellof.senior.core.utils.category

import cf.lucasmellof.senior.core.manager.music.GuildMusicManager
import cf.lucasmellof.senior.core.utils.extensions.musicManager
import cf.lucasmellof.senior.core.utils.extensions.selfMember
import cf.lucasmellof.senior.core.utils.extensions.voiceChannel
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.entities.Cog

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
interface MusicCog : Cog {

    override fun name() = ":headphones: | ➥ Music"

    fun sameChannel() = false
    fun playingTrack() = false
    fun player() = false
    fun manager() = true
    fun voiceState() = false

    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        val audioManager: GuildMusicManager? = ctx.musicManager.musicManagers[ctx.guild!!.idLong]
        if (manager() && audioManager == null) {
            ctx.send("\uD83D\uDE1F Whoops, there are no songs being played!")
            return false
        }
        val bot = ctx.selfMember!!.voiceState?.channel
        if (voiceState() && ctx.voiceChannel == null) {
            ctx.send("\uD83D\uDE1F Whoops, you are not connected to a voice channel!")
            return false
        }
        if (player() && bot == null) {
            ctx.send("\uD83D\uDE1F Whoops, i'm not connected to a voice channel!")
            return false
        }
        if (sameChannel() && bot != ctx.voiceChannel) {
            ctx.send("\uD83D\uDE1F Whoops, you are not connected to the channel that i'm currently playing in!")
            return false
        }
        if (playingTrack() && audioManager?.audioPlayer?.playingTrack == null) {
            ctx.send("\uD83D\uDE1F Whoops, I'm not playing anywhere!")
            return false
        }
        return true
    }
}