package com.github.craxlor.discordbot.reply;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.simple.JSONObject;

import com.github.craxlor.discordbot.handler.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.module.music.util.YouTubeHelper;
import com.github.craxlor.jReddit.RedditPost;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class EmbedBuilder extends net.dv8tion.jda.api.EmbedBuilder {

    private String footerText;

    public EmbedBuilder() {
        super();
    }

    @Override
    @Nonnull
    public MessageEmbed build() {
        return super.build();
    }

    @Nonnull
    public MessageEmbed build(Member member) {
        setColor(member.getGuild());
        setFooter(footerText + " | " + member.getJDA().getSelfUser().getName(), member.getEffectiveAvatarUrl());
        return super.build();
    }

    @SuppressWarnings("null")
    public EmbedBuilder addMusicFields(@Nullable AudioTrackInfo trackInfo, @Nullable JSONObject videoInformation) {
        // thumbnail
        if (videoInformation != null) {
            String thumbnailURL = (String) videoInformation.get("thumbnailUrl");
            setThumbnail(thumbnailURL);
        }
        // track name field
        if (videoInformation != null) {
            String videoURL = YouTubeHelper.YOUTUBE_VIDEO_PREFIX + (String) videoInformation.get("videoId");
            String videoTitle = (String) videoInformation.get("vieoTitle");
            addField("Track", "[" + videoTitle + "](" + videoURL + ")", false);

        } else if (trackInfo != null) {
            addField("Track", "[" + trackInfo.title + "](" + trackInfo.uri + ")", false);
        }
        // track length field
        if (trackInfo != null) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(trackInfo.length);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60;
            String second = String.valueOf(seconds);
            if (second.length() < 2) {
                second = "0" + second;
            }
            addField("Length", minutes + ":" + second + " minutes", true);
        }
        // channel field
        if (trackInfo != null) {
            if (videoInformation != null) {
                String channelURL = YouTubeHelper.YOUTUBE_CHANNEL_PREFIX + (String) videoInformation.get("channelId");
                addField("Youtube channel", "[" + trackInfo.author + "](" + channelURL + ")", true);
            } else
                addField("Youtube channel", trackInfo.author, true);
        }
        return this;
    }

    public EmbedBuilder setRedditFormat(Guild guild, RedditPost redditPost) {
        setColor(guild);
        setImage(redditPost.getUrl_overridden_by_dest());
        setTitle(redditPost.getTitle(), redditPost.getPermalink());
        setFooter(" 👍 " + redditPost.getUps() + " |" + " 💬 " + redditPost.getNum_comments() + " - "
                + redditPost.getSubReddit());
        return this;
    }

    @SuppressWarnings("null")
    public EmbedBuilder setCommandReply(SlashCommandInteractionEvent event, Status status, String statusDetail) {
        String commmandName = SlashCommandInteractionHandler.getCommandName(event);
        setTitle(status.toString());
        setDescription(statusDetail);
        // addField("Command", commmandName, false);
        switch (status) {
            case SUCCESS -> {
                setThumbnail("https://raw.githubusercontent.com/twitter/twemoji/master/assets/72x72/2705.png");
            }
            default -> {
                setThumbnail("https://raw.githubusercontent.com/twitter/twemoji/master/assets/72x72/274c.png");
            }
        }
        footerText = event.getMember().getEffectiveName() + " used " + commmandName;
        return this;
    }

    private EmbedBuilder setColor(Guild guild) {
        Color color = GuildManager.getGuildManager(guild).getGuildConfig().getEmbedColor();
        if (color == null) {
            Random random = new Random();
            color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        setColor(color);
        return this;
    }
}
