package com.github.craxlor.discordbot.reply;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.jReddit.RedditPost;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class Reply {

    @Nonnull
    private ReplyCallbackAction replyCallbackAction;
    @Nullable
    private MessageEmbed messageEmbed;

    public Reply(@Nonnull ReplyCallbackAction replyCallbackAction, boolean ephemeral) {
        this.replyCallbackAction = replyCallbackAction;
        this.replyCallbackAction.setEphemeral(ephemeral);
    }

    public Reply(@Nonnull ReplyCallbackAction replyCallbackAction, @Nonnull MessageEmbed messageEmbed,
            boolean ephemeral) {
        this.replyCallbackAction = replyCallbackAction;
        this.messageEmbed = messageEmbed;
        this.replyCallbackAction.setEphemeral(ephemeral);
    }

    public Reply setMessageEmbed(@Nonnull MessageEmbed messageEmbed) {
        this.messageEmbed = messageEmbed;
        return this;
    }

    public Reply onCommand(SlashCommandInteractionEvent event, Status status, String statusDetail) {
        // setup messageEmbed
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setCommandReply(event, status, statusDetail);
        messageEmbed = embedBuilder.build(event.getMember());
        return this;
    }

    @SuppressWarnings("null")
    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail,
            @Nullable AudioTrackInfo audioTrackInfo) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (audioTrackInfo != null)
            embedBuilder.addMusicFields(audioTrackInfo);
        embedBuilder.setCommandReply(event, status, statusDetail);
        messageEmbed = embedBuilder.build(event.getMember());
        // send reply message in music log channel
        Guild guild = event.getGuild();
        long id = GuildManager.getGuildManager(guild).getGuildConfig().getMusicLog();
        if (id != -1)
            guild.getTextChannelById(id).sendMessageEmbeds(messageEmbed).queue();
        return this;
    }

    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail) {
        return onMusic(event, status, statusDetail, null);
    }

    public Reply onReddit(SlashCommandInteractionEvent event, RedditPost redditPost) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setRedditFormat(event.getGuild(), redditPost);
        messageEmbed = embedBuilder.build(event.getMember());
        return this;
    }

    public void send() {
        if (messageEmbed != null)
            replyCallbackAction.addEmbeds(messageEmbed).queue();
        else
            replyCallbackAction.queue();
    }
}
