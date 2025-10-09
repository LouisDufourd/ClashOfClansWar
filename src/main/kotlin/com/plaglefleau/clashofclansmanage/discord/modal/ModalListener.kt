package com.plaglefleau.clashofclansmanage.discord.modal

import com.plaglefleau.clashofclansmanage.discord.modal.execution.JoinWarModal
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.modals.Modal

class ModalListener: ListenerAdapter() {
    override fun onModalInteraction(event: ModalInteractionEvent) {
        if(event.modalId.startsWith("join-war-modal")) {
            JoinWarModal().execute(event)
            return
        }
    }
}