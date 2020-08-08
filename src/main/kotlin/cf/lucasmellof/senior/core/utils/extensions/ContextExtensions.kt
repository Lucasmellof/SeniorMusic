package cf.lucasmellof.senior.core.utils.extensions

import cf.lucasmellof.senior.Music
import cf.lucasmellof.senior.core.manager.database.DatabaseManager
import cf.lucasmellof.senior.core.manager.database.Guild
import cf.lucasmellof.senior.core.manager.music.GuildMusicManager
import cf.lucasmellof.senior.core.manager.music.MusicManager
import com.jagrosh.jdautilities.waiter.EventWaiter
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.sharding.ShardManager
import java.util.function.Consumer

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 05/08/2020
 */
val Context.selfMember: Member?
    get() = guild!!.selfMember
val Context.voiceChannel: VoiceChannel?
    get() = member!!.voiceState?.channel
val Context.user: User?
    get() = member!!.user
val Context.musicManager: MusicManager
    get() = Music.musicManager
val Context.guildManager: GuildMusicManager?
    get() = Music.musicManager.getMusicManager(guild!!)
val Context.shardManager: ShardManager
    get() = Music.shardManager
val Context.eventWaiter: EventWaiter
    get() = Music.eventWaiter
val Context.data: Guild?
    get() = DatabaseManager(guild!!).getGuildData()

fun Context.send(embed: EmbedBuilder.() -> Unit, consumer: Consumer<Message> = Consumer { }) {
    messageChannel.sendMessage(EmbedBuilder().apply(embed).build()).queue(consumer)
}
