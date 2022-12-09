package com.github.craxlor.discordbot.command.module.music.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.DiscordServer;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class MusicLog extends SCAdmin {

    private static final String SET_NAME = "bind";
    private static final String SET_DESCRIPTION = "Sets a textchannel as a music log.";
    private static final String SET_OPT_NAME = "music-channel";
    private static final String SET_OPT_DESCRIPTION = "Select the textchannel where music commands should be logged.";
    private static final String REMOVE_NAME = "remove";
    private static final String REMOVE_DESCRIPTION = "Removes a textchannel as a music log.";
    private static final String REMOVE_OPT_DELETE_NAME = "delete";
    private static final String REMOVE_OPT_DELETE_DESCRIPTION = "Select if the channel should be deleted.";

    public MusicLog() {
        SubcommandData musicSet = new SubcommandData(SET_NAME, SET_DESCRIPTION);
        musicSet.addOption(OptionType.CHANNEL, SET_OPT_NAME, SET_OPT_DESCRIPTION, true);
        SubcommandData musicRemove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
        OptionData removeOptionDelete = new OptionData(OptionType.BOOLEAN, REMOVE_OPT_DELETE_NAME,
                REMOVE_OPT_DELETE_DESCRIPTION);
        musicRemove.addOptions(removeOptionDelete);
        commandData.addSubcommands(musicSet, musicRemove);
    }

    @Override
    @Nonnull
    public String getName() {
        return "musiclog";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "configure music log";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        String subcommandName = event.getSubcommandName();
        String msg = "";
        Status status = Status.ERROR;
        Database database = Database.getInstance();
        DiscordServer discordServer = database.getDiscordServer(event.getGuild().getIdLong());
        switch (subcommandName) {
            case SET_NAME -> {
                // set music channel id in config
                TextChannel tc = event.getOption(SET_OPT_NAME).getAsChannel()
                        .asTextChannel();
                discordServer.setMusicLog_id(tc.getIdLong());
                msg = "The music log has been set to " + tc.getAsMention() + ".";
                status = Status.SUCCESS;
            }
            case REMOVE_NAME -> {
                long id = discordServer.getMusicLog_id();
                if (discordServer.getMusicLog_id() > 0) {
                    discordServer.setMusicLog_id(-1);
                    OptionMapping optionMapping = event.getOption(REMOVE_OPT_DELETE_NAME);
                    if (optionMapping != null && optionMapping.getAsBoolean())
                        event.getGuild().getTextChannelById(id).delete().queue();
                    msg = "The music log has been removed.";
                    status = Status.SUCCESS;
                } else {
                    msg = "There is no music log to remove!";
                    status = Status.FAIL;
                }
            }
        }
        database.update(discordServer);

        return new Reply(event.deferReply(), false).onMusic(event, status, msg);
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

}
