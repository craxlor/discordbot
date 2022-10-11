package com.github.craxlor.discordbot.module.music.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.command.SCMusic;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Disconnect extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "disconnect";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "reset the queue and disconnect the bot";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        disconnect(event.getGuild());
        return new Reply(event.deferReply(), true).onMusic(event, Status.SUCCESS,
                "The queue was reset and the bot was disconnected");
    }

    public static void disconnect(Guild guild) {
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();
        musicManager.scheduler.clearQueue();
        musicManager.scheduler.nextTrack();
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

}
