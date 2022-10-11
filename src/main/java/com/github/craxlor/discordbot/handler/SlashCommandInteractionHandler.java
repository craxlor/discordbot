package com.github.craxlor.discordbot.handler;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.Logger;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandInteractionHandler extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        Logger logger = guildManager.getLogger();
        // find command
        SlashCommand slashCommand = guildManager.getCommandlist().find(event.getName());
        Reply reply = new Reply(event.deferReply(), false);
        String statusDetail = "";
        // command has not been found
        if (slashCommand == null) {
            statusDetail = "couldn't find command";
            reply.onCommand(event, Status.ERROR, statusDetail);
            logger.logCommand(Status.ERROR, statusDetail, getCommandName(event), member.getEffectiveName());
            return;
        }
        // check if member is allowed to use the command
        else if (slashCommand.memberHasPermission(member, event.getGuild()) == false) {
            statusDetail = "missing permisson";
            logger.logCommand(Status.FAIL, statusDetail, getCommandName(event), member.getEffectiveName());
            reply.onCommand(event, Status.FAIL, statusDetail);
            return;
        }
        // try to execute command
        try {
            reply = slashCommand.execute(event);
            logger.logCommand(Status.SUCCESS, "successful execution", getCommandName(event), member.getEffectiveName());
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
            logger.logCommand(Status.ERROR, statusDetail, getCommandName(event), member.getEffectiveName());
            reply.onCommand(event, Status.ERROR, statusDetail).send();
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
