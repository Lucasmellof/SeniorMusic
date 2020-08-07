package cf.lucasmellof.senior.core.manager.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.TextChannel

class TrackContext(var track: AudioTrack, var dj: String, var channel: TextChannel) {
    fun makeClone(): TrackContext {
        track = track.makeClone()
        return this
    }
}