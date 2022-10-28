package com.github.craxlor.discordbot.module.music.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.command.SCMusic;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Resume extends SCMusic {
    @Override
    @Nonnull
    public String getName() {
        return "resume";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Resumes the paused track";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        String commandAction = "";
        Status status = Status.FAIL;
        AudioTrackInfo audioTrackInfo = null;
        if (musicManager.player.getPlayingTrack() == null) {
            commandAction = "There is nothing to play at the moment!";
        } else {
            if (musicManager.player.isPaused()) {
                musicManager.scheduler.onPlayerResume(musicManager.player);
                commandAction = ":play_pause: Resuming the playback of the current track.";
                status = Status.SUCCESS;
            } else
                commandAction = "There is already a track playing!";
            audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
        }
        return new Reply(event.deferReply(), false).onMusic(event, status, commandAction, audioTrackInfo);
    }

}
