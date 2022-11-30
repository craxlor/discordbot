package com.github.craxlor.discordbot.command.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping extends SlashCommand {

    @Override
    @Nonnull
    public String getName() {
        return "ping";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Pong!";
    }

    @Override
    public boolean memberHasPermission(@Nonnull Member member, @Nonnull Guild guild) {
        return true;
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        return new Reply(event.deferReply(), true).onCommand(event, Status.SUCCESS, "Pong!");
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
