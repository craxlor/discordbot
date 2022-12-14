package com.github.craxlor.discordbot.listener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.module.music.command.slash.Disconnect;
import com.github.craxlor.discordbot.util.core.GuildManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MusicVoiceConnectionHandler extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() != null) {// someone left a voiceChannel
            final Member member = event.getMember();
            final Member bot = event.getGuild().getSelfMember();
            if (member.equals(bot)) { // bot left voicechannel
                Disconnect.disconnect(event.getGuild());
                GuildManager.getGuildManager(event.getGuild()).getLogger().info("bot has been disconnected manually");
            }
        }

        if (event.getChannelJoined() == null) {
            // exit cause noone joined a channel -> bot didn't join a channel
            return;
        }
        // check if the bot joined to play music and handle auto disconnect after 5mins
        final Member member = event.getMember();
        final Member bot = event.getGuild().getSelfMember();
        if (member.equals(bot)) { // bot joined voicechannel
            Timer timer = new Timer();
            timer.schedule(new AutoDisconnect(event.getGuild()), TimeUnit.MINUTES.toMillis(5),
                    TimeUnit.SECONDS.toMillis(10));
        }
    }

    private class AutoDisconnect extends TimerTask {
        private Guild guild;
        private GuildManager guildManager;

        AutoDisconnect(Guild guild) {
            this.guild = guild;
            guildManager = GuildManager.getGuildManager(guild);
        }

        /**
         * Run.
         */
        @Override
        public void run() {
            // check if bot is not playing a track
            if (guildManager.getMusicManager().isPlaying() == false) {
                guild.getAudioManager().closeAudioConnection(); // disconnect bot
                guildManager.getLogger().info("bot has automatically disconnected itself");
                cancel(); // stop task
            }
        }
    }

}
