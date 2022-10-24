package com.github.craxlor.discordbot.module.music.command.slash;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.command.SCMusic;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.module.music.util.SpotifyHelper;
import com.github.craxlor.discordbot.module.music.util.YouTubeHelper;
import com.github.craxlor.discordbot.module.music.util.YouTubeHistory;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class Play extends SCMusic {

    private static final String QUEUE_NAME = "queue";
    private static final String QUEUE_DESCRIPTION = "queue a track";
    private static final String NEXT_NAME = "next";
    private static final String NEXT_DESCRIPTION = "play track after the current track";
    private static final String NOW_NAME = "now";
    private static final String NOW_DESCRIPTION = "immediate playback of the provided track";
    private static final String OPT_NAME = "youtube-url";
    private static final String OPT_DESCRIPTION = "provide a youtube video or playlist to play";
    // attributes to avoid scoping problems
    private String commandAction = null;
    private AudioTrackInfo trackInfo = null;
    private Status status = Status.ERROR;
    private String url = null;

    public Play() {
        SubcommandData queue = new SubcommandData(QUEUE_NAME, QUEUE_DESCRIPTION);
        SubcommandData next = new SubcommandData(NEXT_NAME, NEXT_DESCRIPTION);
        SubcommandData now = new SubcommandData(NOW_NAME, NOW_DESCRIPTION);
        OptionData option = new OptionData(OptionType.STRING, OPT_NAME, OPT_DESCRIPTION, true);
        queue.addOptions(option);
        next.addOptions(option);
        now.addOptions(option);
        commandData.addSubcommands(queue, next, now);
    }

    @Override
    @Nonnull
    public String getName() {
        return "play";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "play music";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        url = event.getOption(OPT_NAME).getAsString();
        Member member = event.getMember();
        String subcommandName = event.getSubcommandName();
        Guild guild = event.getGuild();
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();

        // check if bot is already playing
        Member bot = guild.getSelfMember();
        if (bot.getVoiceState().inAudioChannel() && musicManager.isPlaying()) {
            // check if member & bot are not in the same channel
            if (bot.getVoiceState().getChannel().getIdLong() != member.getVoiceState().getChannel().getIdLong()) {
                return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
                        "you have to be in the same AudioChannel");
            }
        }
        // check if provided url is from spotify
        if (url.contains("spotify")) {
            if (url.contains("track") == false)
                return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
                        "I just support spotify **tracks**");
            // get track from spotify
            Track track = SpotifyHelper.getTrack(url);
            // build searchTerm
            String artists = "";
            for (ArtistSimplified artistSimplified : track.getArtists()) {
                artists += " " + artistSimplified.getName();
            }
            String searchTerm = track.getName() + artists;
            url = getYouTubeUrlBySearchTerm(searchTerm);
        } else if (url.contains("http") == false) {
            /**
             * assume that url is not containing an url but a searchTerm to look
             * up in youtube
             */
            url = getYouTubeUrlBySearchTerm(url);
        }
        GuildManager.getAudioPlayerManager().loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                connectTo(member);
                switch (subcommandName) {
                    case QUEUE_NAME -> {
                        musicManager.scheduler.queue(track);
                        // check if the track is played immediately
                        if (musicManager.player.getPlayingTrack().equals(track)) {
                            commandAction = "playing " + track.getInfo().title;
                        } else {
                            commandAction = track.getInfo().title + " has been added to queue";
                        }
                    }
                    case NEXT_NAME -> {
                        musicManager.scheduler.addOnTopOfQueue(track, true);
                        commandAction = track.getInfo().title + " is now at the top of the queue";
                    }
                    case NOW_NAME -> {
                        musicManager.scheduler.addOnTopOfQueue(track, false);
                        commandAction = "playing " + track.getInfo().title;
                    }
                }
                trackInfo = track.getInfo();
                status = Status.SUCCESS;
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                connectTo(member);
                final List<AudioTrack> tracks = playlist.getTracks();
                switch (subcommandName) {
                    case QUEUE_NAME -> musicManager.scheduler.addToQueue(tracks);
                    case NEXT_NAME -> musicManager.scheduler.addOnTopOfQueue(tracks, true);
                    case NOW_NAME -> musicManager.scheduler.addOnTopOfQueue(tracks, false);
                }
                commandAction = "added playlist " + "[" + playlist.getName() + "](" + url + ")" + " to queue";
                status = Status.SUCCESS;
            }

            @Override
            public void noMatches() {
                commandAction = ":x: Nothing found";
                status = Status.FAIL;
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                commandAction = ":x: Could not load \n" + exception.getMessage();
                status = Status.FAIL;
            }
        }).get();

        return new Reply(event.deferReply(), false).onMusic(event, status, commandAction, trackInfo);
    }

    @SuppressWarnings("null")
    private void connectTo(@Nonnull Member member) {
        final VoiceChannel myChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final AudioManager audioManager = member.getGuild().getAudioManager();
        audioManager.openAudioConnection(myChannel);
    }

    private String getYouTubeUrlBySearchTerm(@Nonnull String searchTerm) {
        YouTubeHistory history = new YouTubeHistory();
        if (history.contains(searchTerm))
            return history.get(searchTerm);
        else {
            String url = YouTubeHelper.getVideoURLBySearchTerm(searchTerm);
            // save searchTerm to reduce quota usage
            history.add(searchTerm, url);
            return url;
        }
    }

}
