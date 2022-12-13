package com.github.craxlor.discordbot.listener;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.module.autoroom.command.slash.Setup;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.AutoroomChannel;
import com.github.craxlor.discordbot.database.element.AutoroomTrigger;

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
        Member member = event.getMember();
        VoiceChannel voiceChannel;
        Database database = Database.getInstance();
        /**
         * create a dynamic voice channel whenever a member moves or joins in a
         * specified voice channel
         */
        if ((voiceChannel = (VoiceChannel) event.getChannelJoined()) != null) {
            AutoroomTrigger autoroomTrigger;
            if ((autoroomTrigger = database.getAutoroomTrigger(voiceChannel.getIdLong())) != null)
                createAutoroomChannel(database, autoroomTrigger, member);
        }
        // delete dynamic voice channel if it is empty
        if ((voiceChannel = (VoiceChannel) event.getChannelLeft()) != null) {
            if (database.getAutoroomChannel(voiceChannel.getIdLong()) != null)
                leaveAutoroomChannel(database, voiceChannel);
        }

    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        long channelID = event.getChannel().getIdLong();
        Database database = Database.getInstance();
        if (database.getAutoroomTrigger(channelID) != null)
            database.removeAutoroomTrigger(channelID);
        if (database.getAutoroomChannel(channelID) != null)
            database.removeAutoroomChannel(channelID);

    }

    @Override
    @SuppressWarnings("null")
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        long autoroomID;
        VoiceChannel autoroom;
        Database database = Database.getInstance();
        List<AutoroomChannel> autoroomChannels = database.getAutoroomChannelsByGuild(guild.getIdLong());
        if (autoroomChannels == null || autoroomChannels.isEmpty() || autoroomChannels.size() < 1)
            return;

        for (AutoroomChannel autoroomChannel : autoroomChannels) {
            autoroomID = autoroomChannel.getChannel_id();
            autoroom = guild.getVoiceChannelById(autoroomID);
            if (autoroom.getMembers().size() < 1) {
                autoroom.delete().queue();
                database.removeAutoroomChannel(autoroomID);
            }
        }

    }

    @SuppressWarnings("null")
    private void createAutoroomChannel(Database database, AutoroomTrigger autoroomTrigger, Member member) {
        String name = autoroomTrigger.getNaming_pattern();
        // parse name
        if (name.contains("number")) {
            List<AutoroomChannel> autoroomChannels = database.getAutoroomChannelsByTrigger(autoroomTrigger.getTrigger_id());
            name = name.replace("number", Integer.toString(autoroomChannels.size()+1));
        }
        if (name.contains("username")) {
            // use username as dynamicName
            name = name.replace("username", member.getEffectiveName());
        }
        Guild guild = member.getGuild();
        Category category = guild.getCategoryById(autoroomTrigger.getCategory_id());
        VoiceChannel autoroomTriggerVC = guild.getVoiceChannelById(autoroomTrigger.getTrigger_id());
        // create new voicechannel
        VoiceChannel autoroom = null;
        if (autoroomTrigger.getInheritance() == null) {
            autoroom = guild.createVoiceChannel(name, category).complete();
        } else {
            switch (autoroomTrigger.getInheritance()) {
                case Setup.CHOICE_TRIGGER -> {
                    autoroom = guild.createCopyOfChannel(autoroomTriggerVC).setName(name).complete();
                    autoroom.getManager().setParent(category).queue();
                }
                case Setup.CHOICE_CATEGORY -> {
                    autoroom = guild.createVoiceChannel(name, category).complete();
                }
            }
        }
        // channel author is allowed to customize the channel
        autoroom.upsertPermissionOverride(member).setAllowed(Permission.MANAGE_CHANNEL).queue();
        // inherit user limit from trigger channel
        autoroom.getManager().setUserLimit(autoroomTriggerVC.getUserLimit()).queue();
        // move member to new voiceChannel
        guild.moveVoiceMember(member, autoroom).queue();
        // add created voiceChannel to database
        AutoroomChannel autoroomChannel = new AutoroomChannel(autoroom.getIdLong(), autoroomTrigger.getTrigger_id(),
                guild.getIdLong());
        database.insert(autoroomChannel);
    }

    private void leaveAutoroomChannel(Database database, VoiceChannel autoroomChannelVC) {
        if (autoroomChannelVC.getMembers().size() < 1) {
            database.removeAutoroomChannel(autoroomChannelVC.getIdLong());
            autoroomChannelVC.delete().queue();
        }
    }
}
