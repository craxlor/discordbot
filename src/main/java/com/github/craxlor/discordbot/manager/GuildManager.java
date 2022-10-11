package com.github.craxlor.discordbot.manager;

import java.util.HashMap;
import java.util.List;

import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.module.autoroom.command.AutoroomCollection;
import com.github.craxlor.discordbot.module.core.command.CoreCollection;
import com.github.craxlor.discordbot.module.core.command.slash.Module;
import com.github.craxlor.discordbot.module.music.command.MusicCollection;
import com.github.craxlor.discordbot.module.music.manager.MusicManager;
import com.github.craxlor.discordbot.module.reddit.command.RedditCollection;
import com.github.craxlor.discordbot.module.reddit.manager.GalleryTasks;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {
    private static HashMap<Long, GuildManager> guildManagerMap = new HashMap<>();
    private static AudioPlayerManager playerManager;

    private Logger logger;
    private Commandlist commandlist;
    private GuildConfig guildConfig;
    private MusicManager musicManager;
    private GalleryTasks galleryTasks;

    protected GuildManager(Guild guild) {
        logger = new Logger(com.github.craxlor.utilities.Logger.getLogger(guild.getId()), guild);
        guildConfig = new GuildConfig(guild);
        commandlist = new Commandlist();
        commandlist.addAll(new CoreCollection());
        List<String> modules = guildConfig.getModules();
        for (String module : modules) {
            switch (module) {
                case Module.OPT_AUTOROOM_NAME -> commandlist.addAll(new AutoroomCollection());
                case Module.OPT_MUSIC_NAME -> commandlist.addAll(new MusicCollection());
                case Module.OPT_REDDIT_NAME -> commandlist.addAll(new RedditCollection());
            }
        }
        musicManager = new MusicManager(getAudioPlayerManager());
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        galleryTasks = new GalleryTasks(guild);
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
        return logger;
    }

    public Commandlist getCommandlist() {
        return commandlist;
    }

    public GuildConfig getGuildConfig() {
        return guildConfig;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public GalleryTasks getGalleryTasks() {
        return galleryTasks;
    }
}
