package com.plaglefleau.clashofclansmanage.utils

import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.modals.Modal

object DiscordModal {
    fun createWarInscriptionModal(playerTags: List<String>, modalName: String): Modal {
        val playerTagSelection = StringSelectMenu.create("choose-player-tag")
            .addOptions(playerTags.map { SelectOption.of(it, it) })
            .setPlaceholder("Choisissez un compte")
            .build()

        return Modal.create(modalName, "Rejoindre la guerre")
            .addComponents(Label.of("Comptes", playerTagSelection))
            .build()
    }
}