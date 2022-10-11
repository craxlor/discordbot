package com.github.craxlor.discordbot.module.reddit.handler;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.Logger;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RedditHandler extends ListenerAdapter {
    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        Logger logger = guildManager.getLogger();
        logger.info("prepare guild" + "\nGuild: " + guild.getName());

        // load reddit galleries
        guildManager.getGalleryTasks().reloadTasks();
    }
}
