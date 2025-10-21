package com.plaglefleau.clashofclansmanage.discord.buttons.execution

import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
import com.plaglefleau.clashofclansmanage.utils.DiscordModal
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class LeaveWarButton: DiscordExecuteButton {
    override fun execute(event: ButtonInteractionEvent) {
        val pair = DiscordUser.verifyUser(event, event) ?: return

        val member = pair.first

        val playerTags = AccountManager().getPlayerTags(member.id).filter { tag ->
            val manager = WarManager()
            manager.getPlayerInscriptionForWar(manager.getNextWarId(), tag)?.participation ?: false
        }

        if (playerTags.isEmpty()) return DiscordEventReply.replyEphemeralMessage(event, "Erreur: Aucun compte clash of clan n'a été trouvé pour le membre ${member.effectiveName}.")

        event.replyModal(DiscordModal.createWarInscriptionModal(playerTags, "leave-war-modal")).queue()
    }
}