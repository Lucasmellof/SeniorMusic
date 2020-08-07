package cf.lucasmellof.senior.commands.music

import cf.lucasmellof.senior.core.manager.music.AudioLoader
import cf.lucasmellof.senior.core.manager.music.AudioUtils
import cf.lucasmellof.senior.core.manager.music.TrackScheduler
import cf.lucasmellof.senior.core.utils.category.MusicCog
import cf.lucasmellof.senior.core.utils.defaultColor
import cf.lucasmellof.senior.core.utils.extensions.*
import cf.lucasmellof.senior.core.utils.isDJ
import cf.lucasmellof.senior.core.utils.progressBar
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 06/08/2020
 */
class MusicCommand : MusicCog {
    override fun manager() = false

//    RESOLVER ESSE PROBLEMA, AGORA ESTÁ MANDANDO DUAS VEZES...
//    DPS DO CONSUMER APAGAR E ENVIAR NOVAMENTE
//    PARA EVITAR PROBLEMAS

    @Command(guildOnly = true)
    fun music(ctx: Context) {
        val manager = ctx.musicManager.musicManagers.containsKey(ctx.guild!!.idLong)
        if (manager) {
            controller(ctx, true)
        } else {
            controller(ctx, false)
        }
    }

    fun controller(ctx: Context, manager: Boolean) {
        ctx.send({
            setColor(defaultColor)
            setTitle(":musical_note: Music controller")
            if (manager) {
                setDescription(buildString {
                    append("Press :pause_button: pause or resume the current song!\n")
                    append("Press :repeat: to repeat the current song!\n")
                    append("Press :twisted_rightwards_arrows: to shuffle the current queue!\n")
                    append("Press :stop_button: to stop the current queue!\n")
                    append("Press :signal_strength: to change the volume!\n")
                    append("Press :hash: to see the queue!")
                })
            } else setDescription("Press :arrow_forward: to search for a song!")
        }, Consumer {
            it.addReaction("\u25b6").queue()
            if (manager) {
                it.addReaction("\u23F8").queue()
                it.addReaction("\uD83D\uDD01").queue()
                it.addReaction("\uD83D\uDD00").queue()
                it.addReaction("\u23F9").queue()
                it.addReaction("\uD83D\uDCF6").queue()
                it.addReaction("\u0023\u20e3").queue()
            }
            waitFor(ctx, it.id, manager)
        })
    }

    fun waitFor(ctx: Context, messageId: String, managerB: Boolean) {
        ctx.eventWaiter.waitForEvent(
            MessageReactionAddEvent::class.java, { ev ->
                (ev.channel.id == ctx.textChannel!!.id && ev.member!!.id == ctx.member!!.id && ev.messageId == messageId)
            },
            { action ->
                val guildManager = ctx.musicManager.musicManagers[ctx.guild!!.idLong]
                when (action.reactionEmote.name) {
                    "\u25b6" -> {
                        ctx.send({
                            setColor(defaultColor)
                            setTitle(":musical_note: Play")
                            setDescription("Enter the URL or name of the song!")
                        }, Consumer {
                            waitForMessage(ctx, it.id, Consumer { s ->
                                if (AudioUtils.connect(ctx.textChannel, ctx.member)) AudioLoader.loadAndPlay(
                                    ctx.user!!,
                                    ctx.textChannel!!,
                                    s,
                                    ctx.message,
                                    false
                                )
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                            })

                        })

                    }
                    "\u23f8" -> {
                        val manager = guildManager!!.audioPlayer
                        manager.isPaused = manager.isPaused.not()

                        ctx.send({
                            setColor(defaultColor)
                            setTitle("\uD83D\uDD0A ${if (manager.isPaused) "Pause" else "Resume"}")
                            setDescription("✅ You ${if (manager.isPaused) "paused" else "resumed"} the current song!")
                        }, Consumer {
                            it.delete().queueAfter(10, TimeUnit.SECONDS) {
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                                action.retrieveMessage().submit().join()
                                    .removeReaction(action.reactionEmote.emoji, action.user!!)
                                controller(ctx, managerB)
                            }
                        })
                    }
                    "\uD83D\uDD01" -> {
                        val track = guildManager!!.trackScheduler
                        track.repeatMode =
                            if (track.repeatMode == TrackScheduler.RepeatMode.SONG) TrackScheduler.RepeatMode.OFF else TrackScheduler.RepeatMode.SONG
                        ctx.send({
                            setColor(defaultColor)
                            setTitle(":musical_note: Repeat")
                            setDescription(
                                if (track.repeatMode == TrackScheduler.RepeatMode.OFF) "✅ You disabled repeat mode." else "✅ You changed the repeat mode of this guild to ``Song``."
                            )
                        }, Consumer {
                            it.delete().queueAfter(10, TimeUnit.SECONDS) {
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                                action.retrieveMessage().submit().join()
                                    .removeReaction(action.reactionEmote.emoji, action.user!!)
                                controller(ctx, managerB)
                            }
                        })
                    }
                    "\uD83D\uDD00" -> {
                        val message = ShuffleCommand.displayShuffle(ctx)
                        ctx.textChannel!!.retrieveMessageById(message).complete().delete()
                            .queueAfter(10, TimeUnit.SECONDS) {
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                                action.retrieveMessage().submit().join()
                                    .removeReaction(action.reactionEmote.emoji, action.user!!)
                                controller(ctx, managerB)
                            }
                    }
                    "\u23f9" -> {
                        var description = "\"You need to be a DJ to use this command.\""
                        if (isDJ(ctx.member!!)) {
                            guildManager!!.trackScheduler.stop()
                            description =
                                "✅ You cleared ``${guildManager.trackScheduler.queue.size}`` songs from the queue."
                        }
                        ctx.send({
                            setColor(defaultColor)
                            setTitle(":musical_note: Stop")
                            setDescription(description)
                        }, Consumer {
                            it.delete().queueAfter(10, TimeUnit.SECONDS) {
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                                action.retrieveMessage().submit().join()
                                    .removeReaction(action.reactionEmote.emoji, action.user!!)
                                controller(ctx, managerB)
                            }
                        })
                        action.retrieveMessage().submit().join()
                            .removeReaction(action.reactionEmote.emoji, action.user!!)
                        controller(ctx, managerB)
                    }
                    "\uD83D\uDCF6" -> {
                        ctx.send({
                            val volume = ctx.guildManager!!.audioPlayer.volume
                            val progress = progressBar(volume.toDouble(), 100.0, 20)
                            setColor(defaultColor)
                            setTitle("\uD83D\uDD0A Volume")
                            setDescription(progress)
                            setFooter("Set the volume by entering a new value")
                        }, Consumer {
                            waitForMessage(ctx, it.id, Consumer { s ->
                                if (AudioUtils.connect(ctx.textChannel, ctx.member)) AudioLoader.loadAndPlay(
                                    ctx.user!!,
                                    ctx.textChannel!!,
                                    s,
                                    ctx.message,
                                    false
                                )
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                            })

                        })

                    }
                    "\u0023\u20e3" -> {
                        val message = QueueCommand.displayQueue(ctx, guildManager!!)
                        ctx.textChannel!!.retrieveMessageById(message).submit().join().delete()
                            .queueAfter(10, TimeUnit.SECONDS) {
                                ctx.textChannel!!.retrieveMessageById(messageId).submit().join().delete().queue()
                                action.retrieveMessage().submit().join()
                                    .removeReaction(action.reactionEmote.emoji, action.user!!)
                                controller(ctx, managerB)
                            }

                    }
                }
            }, 120, TimeUnit.SECONDS, {
                ctx.textChannel!!.sendMessage(
                    "Você demorou muito para reagir."
                ).queue { message -> message.delete().queueAfter(1, TimeUnit.MINUTES) }
            })
    }

    fun waitForMessage(ctx: Context, messageId: String, consumer: Consumer<String>): String {
        var message = ""
        ctx.eventWaiter.waitForEvent(GuildMessageReceivedEvent::class.java, { ev ->
            (ev.channel.id == ctx.textChannel!!.id && ev.member!!.id == ctx.member!!.id)
        }, { action ->
            ctx.textChannel!!.retrieveMessageById(messageId).complete().delete().queue()
            message = action.message.contentRaw
            consumer.accept(message)
        }, 120, TimeUnit.SECONDS, {
            ctx.textChannel!!.retrieveMessageById(messageId).complete().delete().queue()
            ctx.textChannel!!.sendMessage("You took too long to send the message.")
                .queue { it.delete().queueAfter(1, TimeUnit.MINUTES) }
        })
        return message
    }

}