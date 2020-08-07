package cf.lucasmellof.senior.core.manager.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.api.entities.Guild
import java.util.concurrent.ConcurrentHashMap

class MusicManager {
    val musicManagers: ConcurrentHashMap<Long, GuildMusicManager> = ConcurrentHashMap()
    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()

    fun getMusicManager(guild: Guild): GuildMusicManager {
        val audioPlayer = playerManager.createPlayer()
        if (guild.audioManager.sendingHandler == null || guild.audioManager
                .sendingHandler !is PlayerSendHandler
        ) guild.audioManager.sendingHandler = PlayerSendHandler(audioPlayer)
        return musicManagers.computeIfAbsent(guild.idLong) { GuildMusicManager(audioPlayer, guild) }
    }

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
    }
}