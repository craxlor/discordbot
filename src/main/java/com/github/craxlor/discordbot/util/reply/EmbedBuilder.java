package com.github.craxlor.discordbot.util.reply;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.YouTubeSearch;
import com.github.craxlor.discordbot.listener.SlashCommandInteractionHandler;
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
    public EmbedBuilder addMusicFields(@Nullable AudioTrackInfo trackInfo, @Nullable YouTubeSearch youTubeSearch) {
        // track name field
        if (youTubeSearch != null) {
            setThumbnail(youTubeSearch.getVideoThumbnailURL());
            addField("Track", "[" + youTubeSearch.getVideo_title() + "](" + youTubeSearch.getVideoURL() + ")", false);
        } else if (trackInfo != null)
            addField("Track", "[" + trackInfo.title + "](" + trackInfo.uri + ")", false);

        // track length field
        if (trackInfo != null) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(trackInfo.length);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60;
            String second = String.valueOf(seconds);
            if (second.length() < 2)
                second = "0" + second;
            addField("Length", minutes + ":" + second + " minutes", true);
        }
        // channel field
        if (trackInfo != null) {
            if (youTubeSearch != null)
                addField("Youtube channel", "[" + trackInfo.author + "](" + youTubeSearch.getChannelURL() + ")", true);
            else
                addField("Youtube channel", trackInfo.author, true);
        }
        return this;
    }

    public EmbedBuilder setRedditFormat(Guild guild, RedditPost redditPost) {
        setColor(guild);
        setImage(redditPost.getUrl_overridden_by_dest());
        setTitle(redditPost.getTitle(), redditPost.getPermalink());
        setFooter(" 👍 " + redditPost.getUps() + " |" + " 💬 " + redditPost.getNum_comments() + " - "
                + "r/" + redditPost.getSubReddit());
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
        Color color;
        Database database = Database.getInstance();
        String hex = database.getDiscordServer(guild.getIdLong()).getColorHex();

        if (hex != null) {
            color = Color.decode(hex);
        } else {
            Random random = new Random();
            color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        setColor(color);
        return this;
    }
}
