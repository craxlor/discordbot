package com.github.craxlor.discordbot.util.reply;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.YouTubeSearch;
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
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setCommandReply(event, status, statusDetail);
        messageEmbed = embedBuilder.build(event.getMember());
        return this;
    }

    @SuppressWarnings("null")
    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail,
            @Nullable AudioTrackInfo audioTrackInfo, @Nullable YouTubeSearch youTubeSearch) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setCommandReply(event, status, statusDetail);
        if (audioTrackInfo != null)
            embedBuilder.addMusicFields(audioTrackInfo, youTubeSearch);
        messageEmbed = embedBuilder.build(event.getMember());
        // send reply message in music log channel
        Guild guild = event.getGuild();
        Database database = Database.getInstance();
        long id = database.getDiscordServer(guild.getIdLong()).getMusicLog_id();
        if (id > -1 && event.getChannel().getIdLong() != id) {
            replyCallbackAction.setEphemeral(true);
            guild.getTextChannelById(id).sendMessageEmbeds(messageEmbed).queue();
        }
        return this;
    }

    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail) {
        return onMusic(event, status, statusDetail, null, null);
    }

    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail,
            AudioTrackInfo audioTrackInfo) {
        return onMusic(event, status, statusDetail, audioTrackInfo, null);
    }

    public Reply onMusic(SlashCommandInteractionEvent event, Status status, String statusDetail,
            YouTubeSearch youTubeSearch) {
        return onMusic(event, status, statusDetail, null, youTubeSearch);
    }

    public void send() {
        if (messageEmbed != null)
            replyCallbackAction.addEmbeds(messageEmbed).queue();
        else
            replyCallbackAction.queue();
    }
}
