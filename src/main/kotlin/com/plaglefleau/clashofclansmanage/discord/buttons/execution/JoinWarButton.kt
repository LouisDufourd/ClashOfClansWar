package com.plaglefleau.clashofclansmanage.discord.buttons.execution

import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.database.WarManager
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
import com.plaglefleau.clashofclansmanage.utils.DiscordModal
import com.plaglefleau.clashofclansmanage.utils.DiscordPermission
import com.plaglefleau.clashofclansmanage.utils.DiscordUser
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.modals.Modal

class JoinWarButton: DiscordExecuteButton {
    override fun execute(event: ButtonInteractionEvent) {
        val member = event.member
        val guild = event.guild

        if (member == null || guild == null)
            return DiscordEventReply.replyEphemeralMessage(event, "Erreur: Impossible de trouver le membre ou le serveur.")

        if(DiscordPermission.hasPermission(member, DiscordPermission.getRolesByNames(guild, DiscordUser.NOT_MEMBER)))
            return DiscordEventReply.replyEphemeralMessage(
                event,
                """
                    Erreur: Vous n'êtes pas un membre du clan. 
                    Veuillez lié votre compte clash à votre compte discord en utilisant la commande /link <tagJoueur> <jetonApi>.
                """.trimIndent()
            )

        val playerTags = AccountManager().getPlayerTags(member.id).filter { tag ->
            val manager = WarManager()
            !(manager.getPlayerInscriptionForWar(manager.getNextWarId(), tag)?.participation ?: true)
        }

        if (playerTags.isEmpty()) return DiscordEventReply.replyEphemeralMessage(event, "Erreur: Aucun compte clash of clan n'a été trouvé pour le membre ${member.effectiveName}.")

        event.replyModal(DiscordModal.createWarInscriptionModal(playerTags, "join-war-modal")).queue()
    }
}