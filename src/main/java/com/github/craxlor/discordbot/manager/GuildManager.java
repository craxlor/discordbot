package com.github.craxlor.discordbot.manager;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.module.autoroom.command.AutoroomCollection;
import com.github.craxlor.discordbot.module.core.command.CoreCollection;
import com.github.craxlor.discordbot.module.core.command.slash.Module;
import com.github.craxlor.discordbot.module.music.command.MusicCollection;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {
    private static HashMap<Long, GuildManager> guildManagerMap = new HashMap<>();
    private static AudioPlayerManager playerManager;
    private Guild guild;
    private Logger logger;
    private Commandlist commandlist;
    private MusicManager musicManager;

    protected GuildManager(Guild guild) {
        this.guild = guild;
        logger = LoggerFactory.getLogger("sift");
        commandlist = new Commandlist();
        commandlist.addAll(new CoreCollection());
        Database database = Database.getInstance();
        String modules = database.getDiscordServer(guild.getIdLong()).getModules();
        if (modules != null) {
            if (modules.contains(",")) { // multiple modules
                for (String module : modules.split(",")) {
                    switch (module) {
                        case Module.OPT_AUTOROOM_NAME -> commandlist.addAll(new AutoroomCollection());
                        case Module.OPT_MUSIC_NAME -> commandlist.addAll(new MusicCollection());
                    }
                }
            } else { // only one module
                switch (modules) {
                    case Module.OPT_AUTOROOM_NAME -> {
                        commandlist.addAll(new AutoroomCollection());
                        System.out.println("added autoroom commands");
                    }
                    case Module.OPT_MUSIC_NAME -> commandlist.addAll(new MusicCollection());
                }
            }
        }
        musicManager = new MusicManager(getAudioPlayerManager());
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
    }

    public static GuildManager getGuildManager(Guild guild) {
        long id = guild.getIdLong();
        if (guildManagerMap.containsKey(id))
            return guildManagerMap.get(id);
        GuildManager guildManager = new GuildManager(guild);
        guildManagerMap.put(id, guildManager);
        return guildManager;
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        if (playerManager == null) {
            playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
        }
        return playerManager;
    }

    public Logger getLogger() {
        MDC.put("filename", guild.getId());
        return logger;
    }

    public Commandlist getCommandlist() {
        return commandlist;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
}
