package com.github.craxlor.discordbot.listener;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.DiscordServer;
import com.github.craxlor.discordbot.manager.GuildManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildPreparer extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // setup a GuildManager with its necessary components
        Guild guild = event.getGuild();

        Database database = Database.getInstance();
        DiscordServer discordServer = new DiscordServer(guild.getIdLong(), guild.getName());
        database.insert(discordServer);
        

        GuildManager guildManager = GuildManager.getGuildManager(guild);
        guildManager.getLogger().info("""
                joined a guild
                    Guild: %s
                    Owner: %s""".formatted(guild.getName(), guild.getOwner().getEffectiveName()));
    }
}
