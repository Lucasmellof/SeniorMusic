package cf.lucasmellof.senior.core.manager

import cf.lucasmellof.senior.Music
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EventManager : ListenerAdapter() {
    override fun onReady(e: ReadyEvent) {
        if (e.jda.shardInfo.shardId == Music.shardManager.shardsTotal - 1) {
            println(
                """
            ||-=========================================================
            || Account info: ${e.jda.selfUser.name}#${e.jda.selfUser.discriminator} (ID: ${e.jda.selfUser.id})
            || Connected to ${e.jda.guilds.size} guilds, ${e.jda.textChannels.size + e.jda.voiceChannels.size} channels and ${e.jda.users.size} users
            || Registered Commands: ${Music.commandClient.commands.size}
            || Status: ${e.jda.presence.status}
            || Presence: ${e.jda.presence.activity?.name}
            ||-=========================================================
            """.trimMargin("|")
            )
        }
    }
}
