package me.aberrantfox.hotbot.listeners

import me.aberrantfox.hotbot.dsls.embed.embed
import me.aberrantfox.hotbot.extensions.fullName
import me.aberrantfox.hotbot.logging.BotLogger
import me.aberrantfox.hotbot.services.LimitedList
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.awt.Color


class MessageDeleteListener(val logger: BotLogger) : ListenerAdapter() {
    val list = LimitedList<Message>(5000)

    override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val found = list.find { it == event.message }

        if(found != null) {
            logger.history(embed {
                title("Message Edit")
                description("${event.author.asMention}(${event.author.fullName()})")
                setColor(Color.ORANGE)


                field {
                    name = "Old"
                    value = found.contentRaw
                    inline = false
                }

                field {
                    name = "New"
                    value = event.message.contentRaw
                    inline = false
                }
            })

            list.remove(found)
            list.add(event.message)
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) { list.add(event.message) }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val found = list.find { it.id == event.messageId }

        if(found != null) {
            logger.history(embed {
                title("Message Delete")
                description("${found.author.asMention}(${found.author.fullName()})")
                setColor(Color.ORANGE)

                field {
                    name = "Message in ${found.channel.name}"
                    value = found.contentRaw
                    inline = false
                }
            })
        }
    }
}