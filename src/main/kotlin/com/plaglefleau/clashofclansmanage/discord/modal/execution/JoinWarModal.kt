package com.plaglefleau.clashofclansmanage.discord.modal.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.util.concurrent.ConcurrentHashMap

class JoinWarModal: DiscordModalExecution {
    override fun execute(event: ModalInteractionEvent) {
        println("Received join war modal event: $event")
        event.deferReply(true).queue()
        val tag = event.getValue("choose-player-tag")?.asStringList?.firstOrNull()

        if (tag == null) {
            event.hook.sendMessage("Error: Could not find the tag for the user").setEphemeral(true).queue()
            return
        }

        WarManager().joinNextWar(tag)

        event.hook.sendMessage("Vous avez inscrit le compte avec pour tag \"$tag\" à la prochaine guerre").setEphemeral(true).queue()
    }
}