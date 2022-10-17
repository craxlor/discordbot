package com.github.craxlor.discordbot.module.autoroom.handler;

import javax.annotation.Nonnull;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.Logger;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.module.autoroom.command.slash.Setup;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoroomHandler extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        GuildConfig config = guildManager.getGuildConfig();
        Member member = event.getMember();
        VoiceChannel voiceChannel;
        /**
         * create a dynamic voice channel whenever a member moves or joins in a
         * specified voice channel
         */
        if ((voiceChannel = (VoiceChannel) event.getChannelJoined()) != null) {
            if (config.getAutoroomTrigger(voiceChannel.getIdLong()) != null)
                joinedAutoroomTrigger(guild, guildManager, voiceChannel, member);
        }
        // delete dynamic voice channel if it is empty
        if ((voiceChannel = (VoiceChannel) event.getChannelLeft()) != null) {
            if (config.isAutoroom(voiceChannel.getIdLong()))
                leftAutoroom(guild, guildManager, voiceChannel, member);
        }
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        GuildManager guildManager = GuildManager.getGuildManager(event.getGuild());
        GuildConfig config = guildManager.getGuildConfig();
        long channelID = event.getChannel().getIdLong();
        String channelName = event.getChannel().getName();
        Logger logger = (Logger) GuildManager.getGuildManager(event.getGuild()).getLogger();
        if (config.isAutoroomTrigger(channelID)) {
            config.removeAutoroomTrigger(channelID);
            logger.logAutoroom("an autoroom channel was manually deleted", "Trigger", channelName);
        }
        if (config.isAutoroom(channelID)) {
            config.removeAutoroom(channelID);
            logger.logAutoroom("an autoroom channel was manually deleted", "Autoroom", channelName);
        }
    }

    @Override
    @SuppressWarnings("null")
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        GuildConfig config = guildManager.getGuildConfig();
        long autoroomID;
        VoiceChannel autoroom;
        JSONArray autoroomArray = config.getAutorooms();
        if (autoroomArray == null || autoroomArray.size() < 1)
            return;

        for (Object o : autoroomArray) {
            autoroomID = (long) ((JSONObject) o).get("channel-id");
            autoroom = guild.getVoiceChannelById(autoroomID);
            if (autoroom.getMembers().size() < 1) {
                autoroom.delete().queue();
                config.removeAutoroom(autoroomID);
            }
        }
    }

    @SuppressWarnings("null")
    private void joinedAutoroomTrigger(Guild guild, GuildManager guildManager, VoiceChannel autoroomTrigger,
            Member member) {
        GuildConfig config = guildManager.getGuildConfig();
        Logger logger = (Logger) guildManager.getLogger();
        long autoroomTriggerID = autoroomTrigger.getIdLong();
        JSONObject autoroomTriggerJSON = config.getAutoroomTrigger(autoroomTriggerID);

        Category category = guild.getCategoryById((long) autoroomTriggerJSON.get("category-id"));
        String name = (String) autoroomTriggerJSON.get("name");
        // parse name
        if (name.contains("number")) {
            // count all autorooms created through this autoroomTrigger
            JSONArray autoroomsJSON = config.getAutorooms();
            if (autoroomsJSON != null) {
                int number = 0;
                JSONObject autoroomJSON;
                for (Object object : autoroomsJSON) {
                    autoroomJSON = (JSONObject) object;
                    if ((long) autoroomJSON.get("trigger-id") == autoroomTriggerID) {
                        number++;
                    }
                }
                name = name.replace("number", Integer.toString(number + 1));
            }
        }
        if (name.contains("username")) {
            // use username as dynamicName
            name = name.replace("username", member.getEffectiveName());
        }
        // create new voicechannel
        VoiceChannel autoroom = null;
        if (autoroomTriggerJSON.get("parent") == null)
            autoroom = guild.createVoiceChannel(name, category).complete();
        else {
            switch ((String) autoroomTriggerJSON.get("parent")) {
                case Setup.CHOICE_TRIGGER -> {
                    autoroom = guild.createCopyOfChannel(autoroomTrigger).setName(name).complete();
                    autoroom.getManager().setParent(category).queue();
                }
                case Setup.CHOICE_CATEGORY -> {
                    autoroom = guild.createVoiceChannel(name, category).complete();
                }
            }
        }
        autoroom.upsertPermissionOverride(member).setAllowed(Permission.MANAGE_CHANNEL).queue();
        // inherit user limit from trigger channel
        autoroom.getManager().setUserLimit(autoroomTrigger.getUserLimit()).queue();
        // move member to new voiceChannel
        guild.moveVoiceMember(member, autoroom).queue();
        // add created voiceChannel to config
        config.addAutoroom(autoroom.getIdLong(), autoroomTrigger.getIdLong());
        logger.logAutoroom("an autoroom channel was created automatically", "Autoroom", name);
    }

    private void leftAutoroom(Guild guild, GuildManager guildManager, VoiceChannel autoroom, Member member) {
        GuildConfig config = guildManager.getGuildConfig();
        Logger logger = (Logger) guildManager.getLogger();
        // delete dynamic voice channel if it is empty
        if (autoroom.getMembers().size() < 1) {
            config.removeAutoroom(autoroom.getIdLong());
            autoroom.delete().queue();
            logger.logAutoroom("an autoroom channel was deleted automatically", "Autoroom", autoroom.getName());
        }

    }
}
