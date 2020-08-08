package cf.lucasmellof.senior.core.manager.music

import cf.lucasmellof.senior.Music
import cf.lucasmellof.senior.core.manager.database.DatabaseManager
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.toTypedArray
import com.jagrosh.jdautilities.menu.Selector
import com.jagrosh.jdautilities.selector
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class AudioLoader(
    val user: Member,
    private val channel: TextChannel,
    private val msg: Message,
    private val search: String,
    private val force: Boolean
) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack) {
        loadSingle(track, false)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.isSearchResult) {
            onSearch(playlist)
            return
        }
        try {
            var i = 0
            for (track in playlist.tracks) {
                if (i > MAX_QUEUE_LENGTH) {
                    channel.sendMessageFormat(
                        "\uD83D\uDE1F You can't add playlists that have more than %s songs. I just added some of those songs.",
                        MAX_QUEUE_LENGTH
                    ).queue()
                    break //stop adding songs
                } else {
                    loadSingle(track, true)
                }
                i++
            }
            channel.sendMessageFormat(
                ":musical_note: Added the song ``%s`` to the queue! (``%s``) [``%s``]",
                i,
                playlist.name,
                getLength(
                    playlist.tracks.stream().mapToLong { temp: AudioTrack? -> temp!!.info.length }.sum()
                )
            )
                .complete().delete().queueAfter(2, TimeUnit.MINUTES)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun noMatches() {
        if (!search.startsWith("ytsearch:")) {
            loadAndPlay(user, channel, "ytsearch:$search", msg, force)
            return
        }
        channel.sendMessage(
            String.format(
                "\uD83D\uDE1F Whoops, looks like I didn't find anything similar to ``%s``.",
                (if (search.startsWith("ytsearch:")) search.substring(9) else search).trim { it <= ' ' }
            )
        ).queue()
        queuedSongs--
    }

    override fun loadFailed(exception: FriendlyException?) {
        channel.sendMessage(
            String.format(
                "\uD83D\uDE1F Whoops, could not play this song, reason: `%s`",
                exception!!.message
            )
        ).queue()
        Music.musicManager.getMusicManager(channel.guild).trackScheduler.startNext(false)
        queuedSongs--
    }

    private fun loadSingle(
        audioTrack: AudioTrack?,
        silent: Boolean
    ) {
        val trackInfo = audioTrack!!.info
        audioTrack.userData = user.id
        val manager = Music.musicManager.getMusicManager(channel.guild)
        val scheduler = manager.trackScheduler
        val title = trackInfo!!.title
        val length = trackInfo.length
        if (scheduler.queue.size > MAX_QUEUE_LENGTH) {
            if (!silent) channel.sendMessageFormat(
                "You can't add playlists that have more than %s songs. I just added some of those songs.",
                MAX_QUEUE_LENGTH
            ).queue()
            return
        }
        if (audioTrack.info.length > MAX_SONG_LENGTH) {
            channel.sendMessage(
                String.format(
                    "\uD83D\uDE1F You can't add songs that overpass %s minutes!",
                    AudioUtils.format(MAX_SONG_LENGTH)
                )
            ).queue()
            return
        }
        scheduler.offer(TrackContext(audioTrack, user.id, channel))
        if (scheduler.queue.stream().filter { track: TrackContext ->
                track.track.info.uri == audioTrack.info.uri
            }.count() > 350 && !silent) {
            return
        }
        if (!silent) channel.sendMessageFormat(
            "Added the song ``%s`` to the queue! (``%s``)",
            title,
            getLength(length)
        ).queue()
        manager.audioPlayer.volume = DatabaseManager(channel.guild).getGuildData()!!.volume
    }

    private fun onSearch(playlist: AudioPlaylist) {
        val options: Array<AudioTrack> = playlist.tracks.stream().limit(5).toTypedArray()
        Music.eventWaiter.selector {
            setUser(user.user)
            setType(Selector.Type.REACTIONS)
            setTitle(":musical_note: Please select which song you would like to play:")
            setColor(defaultColor)
            setDescription(":zap: You have several options, just choose one!")
            for (track in options) {
                addOption(
                    String.format(
                        "**[%s](%s)** (%s)",
                        track.info.title,
                        track.info.uri,
                        getLength(track.duration)
                    )
                ) { loadSingle(track, false) }
            }

            finally { message -> message?.delete()?.queue() }
        }.display(msg.textChannel)
    }

    companion object {
        private const val MAX_QUEUE_LENGTH = 60 //75 minutes
        private const val MAX_SONG_LENGTH: Long = 3900000 //65 minutes
        var queuedSongs: Long = 0
        fun loadAndPlay(
            user: Member,
            tc: TextChannel,
            search: String,
            msg: Message,
            force: Boolean
        ): Future<Void>? {
            return Music.musicManager.playerManager.loadItem(search, AudioLoader(user, tc, msg, search, force))
        }

        fun plusQueuedSongs() {
            queuedSongs++
        }

        fun minusQueuedSongs() {
            queuedSongs--
        }

        fun getLength(length: Long): String {
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        length
                    )
                )
            )
        }
    }

}