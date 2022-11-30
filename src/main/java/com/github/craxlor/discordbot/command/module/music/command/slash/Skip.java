package com.github.craxlor.discordbot.command.module.music.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Skip extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "skip";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Skips the current track.";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        String commandAction = "";
        AudioTrackInfo audioTrackInfo = null;
        Status status = Status.FAIL;
        if (musicManager.player.getPlayingTrack() == null) {
            commandAction = "There is nothing to skip at the moment!";
        } else {
            musicManager.scheduler.nextTrack();
            status = Status.SUCCESS;
            if (musicManager.player.getPlayingTrack() == null) {
                commandAction = ":fast_forward: Skipped to nothing.\nThe Bot will be disconnected from the channel.";
                Disconnect.disconnect(event.getGuild());
            } else {
                commandAction = ":fast_forward: Skipped to "
                        + musicManager.player.getPlayingTrack().getInfo().title + " :thumbsup:";
                audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
            }
        }
        return new Reply(event.deferReply(), false).onMusic(event, status, commandAction, audioTrackInfo);
    }

}
