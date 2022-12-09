package com.github.craxlor.discordbot.command.slash;

import org.slf4j.Logger;

import com.github.craxlor.discordbot.Properties;
import com.github.craxlor.discordbot.command.module.music.command.slash.Play;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.manager.GuildManager;

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
        Database database = Database.getInstance();
        Long dj_id = database.getDiscordServer(guild.getIdLong()).getDj_id();

        Role dj = guild.getRoleById(dj_id);
        Logger logger = GuildManager.getGuildManager(guild).getLogger();
        String errorTitle = "";
        String errorMessage = "";
        boolean status = false;
        // is member in a voiceChannel
        if (member.getVoiceState().inAudioChannel() == false) {
            errorTitle = "missing audio connection";
            errorMessage = "you have to be in a voice channel";
        }
        // has member the necessarry role
        else if (dj != null && (member.getRoles().contains(dj) || guild.getPublicRole().equals(dj)) == false
                && member.getIdLong() != Properties.DEV_ID) {
            errorTitle = "missing role";
            errorMessage = "you need the role " + dj.getAsMention();
        }
        /**
         * check if member and bot are in the same voiceChannel
         * for all musicCommands except Play
         */
        else if (this instanceof Play == false) {
            // is bot in a voiceChannel
            Member bot = guild.getSelfMember();
            if (bot.getVoiceState().inAudioChannel() == false) {
                errorTitle = "missing audio connection";
                errorMessage = "the bot is not in a voice channel";
            }
            // are member and bot in the same voiceChannel
            else if (bot.getVoiceState().getChannel().getIdLong() != member.getVoiceState().getChannel().getIdLong()) {
                errorTitle = "matching audio connection";
                errorMessage = "you have to be in the same voice channel";
            } else
                status = true;
        } else
            status = true;

        if (status == false) {
            logger.warn("""
                    ERROR
                     Guild: %s
                    Author: %s
                     Error: %s
                    Detail: %s""".formatted(guild.getName(), member.getEffectiveName(), errorTitle, errorMessage));
        }
        return status;
    }
}
