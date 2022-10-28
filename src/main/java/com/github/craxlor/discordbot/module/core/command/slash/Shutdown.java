package com.github.craxlor.discordbot.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCDev;
import com.github.craxlor.discordbot.handler.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Shutdown extends SCDev {

    @Override
    @Nonnull
    public String getName() {
        return "shutdown";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "shutdown discordbot";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        new Reply(event.deferReply(), true).onCommand(event, Status.SUCCESS, "The Bot is going offline!").send();
        GuildManager.getGuildManager(event.getGuild()).getLogger().logCommand(Status.SUCCESS, "successful execution",
                SlashCommandInteractionHandler.getCommandName(event), event.getMember().getEffectiveName());
        Thread.sleep(1000);
        event.getJDA().shutdown();
        System.exit(0);
        return null;
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
