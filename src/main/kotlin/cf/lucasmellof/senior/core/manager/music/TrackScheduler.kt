package cf.lucasmellof.senior.core.manager.music

import cf.lucasmellof.senior.core.utils.toTypedArray
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color
import java.time.Instant
import java.util.concurrent.*
import kotlin.math.ceil

class TrackScheduler(var audioPlayer: AudioPlayer, var guild: Guild) : AudioEventAdapter() {
    val queue = LinkedBlockingQueue<TrackContext>()
    var repeatMode: RepeatMode = RepeatMode.OFF
    var currentTrack: TrackContext? = null
    var previousTrack: TrackContext? = null
    private var lastMessageId = ""
    private val voteSkips = ArrayList<String>()
    val requiredVotes: Int by lazy {
        val listeners = guild.audioManager.connectedChannel!!.members.stream()
            .filter { m: Member? ->
                !m!!.user.isBot
            }.count().toInt()
        return@lazy ceil(listeners * .55).toInt()
    }

    val listeners: Array<String> =
        guild.audioManager.connectedChannel!!.members.stream().map { it.user.id }.toTypedArray()

    val missingVotes = requiredVotes - voteSkips.size

    fun shuffle() {
        queue.shuffled()
    }

    fun startNext(isSkipped: Boolean) {
        voteSkips.clear()
        if (RepeatMode.SONG == repeatMode && !isSkipped && currentTrack != null) {
            audioPlayer.startTrack(currentTrack!!.makeClone().track, false)
        } else {
            if (currentTrack != null) previousTrack = currentTrack
            currentTrack = queue.poll()
            audioPlayer.startTrack(if (currentTrack == null) null else currentTrack!!.track, false)
        }
        if (currentTrack == null) onQueueEnd()
    }

    fun offer(trackContext: TrackContext) {
        queue.offer(trackContext)
        if (audioPlayer.playingTrack == null) startNext(false)
    }

    fun skip() {
        startNext(true)
    }

    fun stop(): Int {
        val removedSongs = queue.size
        queue.clear()
        startNext(true)
        audioPlayer.destroy()
        return removedSongs
    }

    fun restart(member: Member): Boolean {
        if (currentTrack != null && currentTrack!!.track.state == AudioTrackState.PLAYING) {
            currentTrack!!.track.position = 0
            return true
        } else if (previousTrack != null && AudioUtils.connect(
                previousTrack!!.channel,
                member
            )
        ) {
            val tracks: MutableList<TrackContext?> = ArrayList()
            tracks.add(previousTrack!!.makeClone())
            queue.drainTo(tracks)
            queue.addAll(tracks)
            startNext(true)
            return true
        }
        return false
    }

    override fun onTrackStart(
        player: AudioPlayer,
        track: AudioTrack
    ) {
        val channel = currentTrack!!.channel
        if (channel.canTalk()) {
            val vc = guild.audioManager.connectedChannel
            if (vc == null) {
                channel.sendMessage("\uD83D\uDE1F Whoops, looks like I lost the connection to the voice channel!")
                    .queue()
                stop()
                return
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val info = track.info
            val builder = EmbedBuilder()
            builder.setTitle("\uD83D\uDD0A Playing in ${vc.name}", track.info.uri)
            builder.setDescription(
                String.format("**Now playing** `%s` \n", info!!.title) +
                        "**Length:**" + "`" + AudioUtils.formatTime(info.length) + "`"
            )
            builder.setImage("https://i.ytimg.com/vi/" + track.info.identifier + "/hqdefault.jpg")
            builder.setColor(Color.red)
            builder.setTimestamp(Instant.now())
            channel.sendMessage(builder.build()).complete().delete().queueAfter(3, TimeUnit.MINUTES)
        }
    }

    private var leaveTask: ScheduledFuture<*>? = null
    private val executorService = Executors.newScheduledThreadPool(
        3, ThreadFactoryBuilder()
            .setNameFormat("Senior-ScheduledExecutor Thread-%d").build()
    )

    private fun onQueueEnd() {
        val m = guild.audioManager

        AudioLoader.minusQueuedSongs()
        audioPlayer.destroy()
        executorService.execute { m.closeAudioConnection() }
    }

    private fun leave() {
        queue.clear()
        currentTrack!!.channel.sendMessage("\uD83E\uDD14 I was left alone in the music channel, I will leave in one minute if no one joins!")
            .queue()
        startNext(true)
    }

    fun scheduleLeave() {
        if (leaveTask != null) return
        leaveTask = executorService.schedule({ leave() }, 2, TimeUnit.MINUTES)
    }

    fun cancelLeave() {
        if (leaveTask == null) return
        leaveTask!!.cancel(true)
        leaveTask = null
    }

    override fun onTrackEnd(
        player: AudioPlayer,
        track: AudioTrack,
        endReason: AudioTrackEndReason
    ) {
        voteSkips.clear()
        if (endReason.mayStartNext) {
            startNext(false)
        }
    }

    override fun onTrackException(
        player: AudioPlayer,
        track: AudioTrack,
        exception: FriendlyException
    ) {
        try {
            val channel = currentTrack!!.channel
            if (channel.canTalk()) {
                val guild = channel.guild
                val msg =
                    String.format(
                        "\uD83D\uDE1F Whoops, could not play ``%s``, reason: %s",
                        track.info.title,
                        exception.message
                    )
                if (guild.selfMember
                        .hasPermission(Permission.MESSAGE_HISTORY)
                ) channel.editMessageById(lastMessageId, msg).queue() else channel.sendMessage(msg).queue()
                AudioLoader.minusQueuedSongs()
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onTrackStuck(
        player: AudioPlayer,
        track: AudioTrack,
        thresholdMs: Long
    ) {
        try {
            val channel = currentTrack!!.channel
            if (channel.canTalk()) {
                val guild = channel.guild
                val msg = "\uD83D\uDE1F Whoops, the song got stuck! I'm skipping it!"
                AudioLoader.minusQueuedSongs()
                if (guild.selfMember
                        .hasPermission(Permission.MESSAGE_HISTORY)
                ) channel.editMessageById(lastMessageId, msg).queue() else channel.sendMessage(msg).queue()
            }
        } catch (ignored: Exception) {
        }
    }

    enum class RepeatMode(var message: String) {
        SONG("Song"), OFF("Off")
    }

    fun computeVote(member: Member, channel: TextChannel) {
        if (voteSkips.contains(member.id)) {
            voteSkips.remove(member.id)
            channel.sendMessage(":musical_note: ``${member.effectiveName}`` removed their vote to skip this song! (``${voteSkips.size}/$requiredVotes``)")
                .queue()
        }
        voteSkips.add(member.id)
        if (requiredVotes <= voteSkips.size) {
            startNext(true)
            channel.sendMessage(":musical_note: The necessary amount of votes to skip has been reached, I'm skipping the current song.")
                .queue()
        } else {
            channel.sendMessage(":musical_note: ``%s`` voted to skip this song! (``%s/%s``)").queue()
        }
    }

    companion object {
        val LEAVE_EXECUTOR_SERVICE: ScheduledExecutorService =
            Executors.newScheduledThreadPool(5) { r: Runnable? ->
                val t = Thread(r, "VoiceLeaveThread")
                t.isDaemon = true
                t
            }
    }
}