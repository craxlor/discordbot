package com.github.craxlor.discordbot.command;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.util.reply.Reply;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command {

    CommandData getCommandData();

    boolean memberHasPermission(@Nonnull Member member, @Nonnull Guild guild);

    Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception;

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

    boolean isGuildOnly();
}
