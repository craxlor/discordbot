package com.github.craxlor.discordbot.reply;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.handler.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.javaredditwrapper.RedditPost;
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
    public EmbedBuilder addMusicFields(@Nullable AudioTrackInfo trackInfo) {
        if (trackInfo != null) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(trackInfo.length);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60;
            String second = String.valueOf(seconds);
            if (second.length() < 2) {
                second = "0" + second;
            }
            addField("track", "[" + trackInfo.title + "](" + trackInfo.uri + ")", true);
            addField("length", minutes + ":" + second + " minutes", true);
            addField("youtube channel", trackInfo.author, true);
        }
        return this;
    }

    public EmbedBuilder setRedditFormat(Guild guild, RedditPost redditPost, String url) {
        setColor(guild);
        setImage(url);
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
        addField("Command", commmandName, false);
        // switch (status) {
        // case SUCCESS -> {
        // setThumbnail("attachment://checkmark.png");
        // replyCallbackAction.setFiles(FileUpload.fromData(new
        // File("resources/assets/emotes/checkmark.png"),
        // "checkmark.png"));
        // }
        // default -> {
        // setThumbnail("attachment://crossmark.png");
        // replyCallbackAction.setFiles(FileUpload.fromData(new
        // File("resources/assets/emotes/crossmark.png"),
        // "crossmark.png"));
        // }
        // }
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
