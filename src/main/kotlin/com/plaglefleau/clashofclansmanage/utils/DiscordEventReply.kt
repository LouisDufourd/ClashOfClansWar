package com.plaglefleau.clashofclansmanage.utils

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

object DiscordEventReply {
    fun replyEphemeralMessage(reply: IReplyCallback, message: String) {
        reply.reply(message).setEphemeral(true).queue()
    }

    fun hookEphemeralMessage(reply: IReplyCallback, message: String) {
        reply.hook.sendMessage(message).setEphemeral(true).queue()
    }

    fun replyPublicMessage(reply: IReplyCallback, message: String) {
        reply.reply(message).queue()
    }

    fun hookPublicMessage(reply: IReplyCallback, message: String) {
        reply.hook.sendMessage(message).queue()
    }
}