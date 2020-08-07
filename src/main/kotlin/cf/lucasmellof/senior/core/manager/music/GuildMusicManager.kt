package cf.lucasmellof.senior.core.manager.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import net.dv8tion.jda.api.entities.Guild

class GuildMusicManager(val audioPlayer: AudioPlayer, val guild: Guild) {
    val trackScheduler: TrackScheduler = TrackScheduler(audioPlayer, guild)

    init {
        audioPlayer.addListener(trackScheduler)
    }
}