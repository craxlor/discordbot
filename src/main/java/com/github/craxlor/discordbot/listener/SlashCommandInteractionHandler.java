package com.github.craxlor.discordbot.listener;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.LogHelper;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandInteractionHandler extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        Logger logger = guildManager.getLogger();
        // find command
        SlashCommand slashCommand = guildManager.getCommandlist().find(event.getName());
        Reply reply = new Reply(event.deferReply(), false);
        String statusDetail = "";
        // command has not been found
        if (slashCommand == null) {
            statusDetail = "unknown command";
            reply.onCommand(event, Status.ERROR, statusDetail);
            logger.warn(LogHelper.logCommand(event, Status.ERROR, statusDetail));
            return;
        }
        // check if member is allowed to use the command
        if (slashCommand.memberHasPermission(event.getMember(), event.getGuild()) == false) {
            statusDetail = "missing permission";
            reply.onCommand(event, Status.FAIL, statusDetail);
            logger.warn(LogHelper.logCommand(event, Status.FAIL, statusDetail));
            return;
        }
        // try to execute command
        try {
            reply = slashCommand.execute(event);
            // TODO insert logging to reply for better, more accurate logging
            logger.info(LogHelper.logCommand(event, Status.SUCCESS, "successful execution"));
            if (reply != null)
                reply.send();
        } catch (Exception e) {
            statusDetail = """
                    fatal error on command execution
                    error message:
                    %s
                    --------------------------------
                    localizied error message:
                    %s
                    """.formatted(e.getMessage(), e.getLocalizedMessage());
            reply.onCommand(event, Status.ERROR, statusDetail).send();
            logger.warn(LogHelper.logCommand(event, Status.ERROR, statusDetail));
            e.printStackTrace();
        }
    }

    @Nonnull
    public static String getCommandName(@Nonnull SlashCommandInteractionEvent event) {
        String name = event.getName();
        String group = event.getSubcommandGroup();
        String sub = event.getSubcommandName();
        if (group != null)
            name += " " + group;
        if (sub != null)
            name += " " + sub;
        return name;
    }
}
