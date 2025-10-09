package com.plaglefleau.clashofclansmanage.discord.modal.execution

import com.plaglefleau.clashofclansmanage.database.WarManager
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.util.concurrent.ConcurrentHashMap

class LeaveWarModal: DiscordModalExecution {
    override fun execute(event: ModalInteractionEvent) {
        event.deferReply(true).queue()
        val tag = event.getValue("choose-player-tag")?.asStringList?.firstOrNull()

        if (tag == null) {
            event.hook.sendMessage("Erreur: Le tag de l'utilisateur est introuvable").setEphemeral(true).queue()
            return
        }

        WarManager().joinNextWar(tag, false)

        event.hook.sendMessage("Vous avez désinscrit le compte avec pour tag \"$tag\" à la prochaine guerre").setEphemeral(true).queue()
    }
}