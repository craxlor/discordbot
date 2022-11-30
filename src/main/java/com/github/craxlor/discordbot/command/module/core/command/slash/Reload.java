package com.github.craxlor.discordbot.command.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCDev;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Reload extends SCDev {

    @Override
    @Nonnull
    public String getName() {
        return "reload";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Reloads guild commands.";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        Guild guild = event.getGuild();
        Commandlist guildCommands = GuildManager.getGuildManager(guild).getCommandlist().getGuildCommands();
        // update commandlist
        guild.updateCommands().queue();
        guild.updateCommands().addCommands(guildCommands.getGuildCommands().getCommandData()).queue();
        return new Reply(event.deferReply(), true).onCommand(event, Status.SUCCESS, "The Commandlist has been updated!");
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
