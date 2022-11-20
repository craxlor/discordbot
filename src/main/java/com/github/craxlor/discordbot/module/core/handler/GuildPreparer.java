package com.github.craxlor.discordbot.module.core.handler;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildPreparer extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // setup a GuildManager with its necessary components
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        guildManager.getLogger().info("""
                joined a guild
                    Guild: %s
                    Owner: %s""".formatted(guild.getName(), guild.getOwner().getEffectiveName()));
        // config
        GuildConfig config = (GuildConfig) guildManager.getGuildConfig();
        config.setGuildName(guild.getName());
    }
}
