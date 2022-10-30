package com.github.craxlor.discordbot.module.music.command.slash;

import java.util.List;

import javax.annotation.Nonnull;

import org.json.simple.JSONObject;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.command.SCMusic;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.module.music.util.SpotifyHelper;
import com.github.craxlor.discordbot.module.music.util.YouTubeHelper;
import com.github.craxlor.discordbot.module.music.util.YouTubeStorage;
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
    private static final String QUEUE_DESCRIPTION = "Queues a track.";
    private static final String NEXT_NAME = "next";
    private static final String NEXT_DESCRIPTION = "Plays a track after the current track.";
    private static final String NOW_NAME = "now";
    private static final String NOW_DESCRIPTION = "Starts immediate playback of the provided track.";
    private static final String OPT_NAME = "input";
    private static final String OPT_DESCRIPTION = "Provide a Youtube video/playlist; Spotify track url; searchTerm(yt-Search) to play.";
    // attributes to avoid scoping problems in execute()
    private String commandAction = null;
    private AudioTrackInfo trackInfo = null;
    private Status status = Status.ERROR;
    private String input = null;

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
        input = event.getOption(OPT_NAME).getAsString();
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
                        "You have to be in the same voice channel as the bot!");
            }
        }

        YouTubeStorage youTubeStorage = YouTubeStorage.getInstance();
        JSONObject videoInformation = null;
        if (input.contains("http") == false && input.contains("www.") == false) {
            // assume that input is not containing an url but a searchTerm to look up
            videoInformation = YouTubeHelper.findVideo(input);
            input = YouTubeHelper.YOUTUBE_VIDEO_PREFIX + (String) videoInformation.get("videoId");
        }
        // check if the YT-Video is known in YouTubeStorage
        else if (input.contains("youtube.com/watch?v=")) {
            String videoId;
            // get videoId
            if (input.contains("&list"))
                videoId = input.substring(input.lastIndexOf("?v=") + 3, input.indexOf("&list"));
            else
                videoId = input.substring(input.lastIndexOf("?v=") + 3, input.length());
            // retrieve YouTubeStorage entry to provide more information in command-reply
            if (youTubeStorage.containsVideoId(videoId)) {
                videoInformation = youTubeStorage.getByVideoId(videoId);
            }
        }
        // check if provided url is from spotify
        else if (input.contains("open.spotify")) {
            if (input.contains("track") == false)
                return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
                        "I just support spotify **tracks**!");
            String searchTerm = convertSpotifyUrlToSearchTerm(input);
            videoInformation = YouTubeHelper.findVideo(searchTerm);
            input = YouTubeHelper.YOUTUBE_VIDEO_PREFIX + (String) videoInformation.get("videoId");
        }

        /**
         * input should contain a viable url in any case by now
         * TODO
         * build a new onMusic reply which can display additional information from
         * videoInformation JSONObject
         */

        if (videoInformation != null && videoInformation.get("error") != null)
            return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
                    "The daily quota limit has been reached. Therfore I cannot do this action!");

        GuildManager.getAudioPlayerManager().loadItemOrdered(musicManager, input, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                connectTo(member);
                switch (subcommandName) {
                    case QUEUE_NAME -> {
                        musicManager.scheduler.queue(track);
                        // check if the track is played immediately
                        if (musicManager.player.getPlayingTrack().equals(track))
                            commandAction = "Playing: " + track.getInfo().title;
                        else
                            commandAction = track.getInfo().title + " has been added to the queue.";
                    }
                    case NEXT_NAME -> {
                        musicManager.scheduler.addOnTopOfQueue(track, true);
                        commandAction = track.getInfo().title + " is now at the top of the queue.";
                    }
                    case NOW_NAME -> {
                        musicManager.scheduler.addOnTopOfQueue(track, false);
                        commandAction = "Playing: " + track.getInfo().title;
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
                commandAction = "added playlist " + "[" + playlist.getName() + "](" + input + ")" + " to queue";
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

        return new Reply(event.deferReply(), false).onMusic(event, status, commandAction, trackInfo, videoInformation);
    }

    @SuppressWarnings("null")
    private void connectTo(@Nonnull Member member) {
        final VoiceChannel myChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final AudioManager audioManager = member.getGuild().getAudioManager();
        audioManager.openAudioConnection(myChannel);
    }

    @SuppressWarnings("null")
    private String convertSpotifyUrlToSearchTerm(@Nonnull String url) {
        // get track from spotify
        Track track = SpotifyHelper.getTrack(url);
        // build searchTerm
        String artists = "";
        for (ArtistSimplified artistSimplified : track.getArtists()) {
            artists += " " + artistSimplified.getName();
        }
        return track.getName() + artists;
    }
}
