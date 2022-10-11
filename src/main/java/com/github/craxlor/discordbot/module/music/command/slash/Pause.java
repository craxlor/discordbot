package com.github.craxlor.discordbot.module.music.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.command.SCMusic;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Pause extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "pause";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "pause the current track";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        AudioTrackInfo audioTrackInfo = null;
        String commandAction = "";
        Status status = Status.FAIL;
        if (musicManager.player.getPlayingTrack() == null) {
            commandAction = "There is no track playing at the moment";
        } else {
            if (musicManager.player.isPaused()) {
                commandAction = "The track was already paused.";
            } else {
                musicManager.scheduler.onPlayerPause(musicManager.player);
                commandAction = ":pause_button: The track was paused";
                status = Status.SUCCESS;
            }
            audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
        }
        return new Reply(event.deferReply(), false).onMusic(event, status, commandAction, audioTrackInfo);
    }

}
