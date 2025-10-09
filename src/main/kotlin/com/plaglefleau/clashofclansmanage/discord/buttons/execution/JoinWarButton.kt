package com.plaglefleau.clashofclansmanage.discord.buttons.execution

import com.plaglefleau.clashofclansmanage.database.AccountManager
import com.plaglefleau.clashofclansmanage.utils.DiscordEventReply
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
            return DiscordEventReply.hookEphemeralMessage(event, "Erreur: Impossible de trouver le membre ou le serveur.")

        if(member.roles.contains(guild.getRolesByName(DiscordUser.NOT_MEMBER, true).firstOrNull()))
            return DiscordEventReply.hookEphemeralMessage(event,
                """
                    Erreur: Vous n'êtes pas un membre du clan. 
                    Veuillez lié votre compte clash à votre compte discord en utilisant la commande /link <tagJoueur> <jetonApi>.
                """.trimIndent()
            )

        val playerTags = AccountManager().getPlayerTags(member.id)

        if (playerTags.isEmpty()) return DiscordEventReply.hookEphemeralMessage(event, "Erreur: Aucun compte clash of clan n'a été trouvé pour le membre ${member.effectiveName}.")

        event.replyModal(createModal(playerTags))
    }

    private fun createModal(playerTags: List<String>): Modal {
        val playerTagSelection = StringSelectMenu.create("choose-player-tag")
            .addOptions(playerTags.map { SelectOption.of(it, it) })
            .setPlaceholder("Choisissez un compte")
            .build()

        return Modal.create("join-war-modal", "Rejoindre la guerre")
            .addComponents(Label.of("Comptes", playerTagSelection))
            .build()
    }
}