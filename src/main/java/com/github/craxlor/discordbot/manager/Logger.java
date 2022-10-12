package com.github.craxlor.discordbot.manager;

import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;

public class Logger {

    protected java.util.logging.Logger logger;
    protected Guild guild;

    protected Logger(java.util.logging.Logger logger, Guild guild) {
        this.logger = logger;
        this.guild = guild;
    }

    public void logCommand(Status status, String statusDetail, String commmandName, String authorName) {
        String logMessage = """
                %s
                  Guild: %s
                Command: %s
                 Author: %s
                 Detail: %s""".formatted(status.toString(), guild.getName(), commmandName, authorName, statusDetail);
        switch (status) {
            case ERROR:
            case FAIL:
                logger.warning(logMessage);
                break;
            case SUCCESS:
                logger.info(logMessage);
                break;
        }
    }

    @SuppressWarnings("null")
    public void logGuildJoin() {
        logger.info("""
                joined a guild
                    Guild: %s
                    Owner: %s""".formatted(guild.getName(), guild.getOwner().getEffectiveName()));
    }

    // AUTOROOM
    public void logAutoroom(String action, String channeltype, String channelname) {
        logger.info("""
                    %s
                      Guild: %s
                Channeltype: %s
                Channelname: %s""".formatted(guild.getName(), channeltype, channelname));
    }

    public void logMusicPermissonError(String errortype, String errorDetail) {
        logger.warning("""
                ERROR
                 Guild: %s
                 Error: %s
                Detail: %s""".formatted(guild.getName(), errortype, errorDetail));
    }

    public void info(String arg0) {
        logger.info(arg0);
    }

    public void warning(String arg0) {
        logger.warning(arg0);
    }

}
