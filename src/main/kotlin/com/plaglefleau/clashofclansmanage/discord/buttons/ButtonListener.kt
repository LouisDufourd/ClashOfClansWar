package com.plaglefleau.clashofclansmanage.discord.buttons

import com.plaglefleau.clashofclansmanage.discord.buttons.execution.JoinWarButton
import com.plaglefleau.clashofclansmanage.discord.buttons.execution.LeaveWarButton
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ButtonListener: ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when(event.componentId) {
            "join-war" -> JoinWarButton().execute(event)
            "leave-war" -> LeaveWarButton().execute(event)
        }
    }
}