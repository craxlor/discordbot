package com.github.craxlor.discordbot.module.music.command;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.Logger;
import com.github.craxlor.discordbot.module.music.command.slash.Play;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public abstract class SCMusic extends SlashCommand {

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    // TODO consider adding replies for specific error messages
    @SuppressWarnings("null")
    @Override
    public boolean memberHasPermission(Member member, Guild guild) {
        Logger logger = GuildManager.getGuildManager(guild).getLogger();
        String errorTitle = "";
        String errorMessage = "";
        // is member in a voiceChannel
        if (member.getVoiceState().inAudioChannel() == false) {
            errorTitle = "missing audio connection";
            errorMessage = "you have to be in a voice channel";
            logger.logMusicPermissonError(errorTitle, errorMessage);
            return false;
        }
        Role dj = GuildManager.getGuildManager(guild).getGuildConfig().getDJRole();
        // has member the necessarry role
        if ((member.getRoles().contains(dj) || guild.getPublicRole().equals(dj)) == false) {
            errorTitle = "missing role";
            errorMessage = "you need the role " + dj.getAsMention();
            logger.logMusicPermissonError(errorTitle, errorMessage);
            return false;
        }
        /**
         * check if member and bot are in the same voiceChannel
         * for all musicCommands except Play
         */
        if (this instanceof Play == false) {
            // is bot in a voiceChannel
            Member bot = guild.getSelfMember();
            if (bot.getVoiceState().inAudioChannel() == false) {
                errorTitle = "missing audio connection";
                errorMessage = "the bot is not in an voice channel";
                logger.logMusicPermissonError(errorTitle, errorMessage);
                return false;
            }
            // are member and bot in the same voiceChannel
            if (bot.getVoiceState().getChannel().getIdLong() != member.getVoiceState().getChannel().getIdLong()) {
                errorTitle = "matching  audio connection";
                errorMessage = "you have to be in the same voice channel";
                logger.logMusicPermissonError(errorTitle, errorMessage);
                return false;
            }
        }
        return true;
    }
}
