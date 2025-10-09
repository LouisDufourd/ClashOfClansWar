package com.plaglefleau.clashofclansmanage.discord.modal.execution

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

interface DiscordModalExecution {
    fun execute(event: ModalInteractionEvent)
}