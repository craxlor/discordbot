package com.github.craxlor.discordbot.util.reply;

import com.github.craxlor.discordbot.listener.SlashCommandInteractionHandler;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LogHelper {

    @SuppressWarnings("null")
    public static String logCommand(SlashCommandInteractionEvent event, Status status, String statusDetail) {
        return """
                %s
                  Guild: %s
                Command: %s
                 Author: %s
                 Detail: %s""".formatted(status.toString(), event.getGuild().getName(),
                SlashCommandInteractionHandler.getCommandName(event), event.getMember().getEffectiveName(),
                statusDetail);
    }

    public static String logAutoroom(String guildName, String channelname, String action, String channeltype) {
        return """
                    %s
                      Guild: %s
                Channeltype: %s
                Channelname: %s""".formatted(action, guildName, channeltype, channelname);
    }

    public static String logMusicPermissonError(String guildName, String errortype, String errorDetail) {
        return """
                ERROR
                 Guild: %s
                 Error: %s
                Detail: %s""".formatted(guildName, errortype, errorDetail);
    }

}
