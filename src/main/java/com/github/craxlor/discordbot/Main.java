package com.github.craxlor.discordbot;

import java.sql.SQLException;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.listener.AutoroomHandler;
import com.github.craxlor.discordbot.listener.GuildPreparer;
import com.github.craxlor.discordbot.listener.MusicVoiceConnectionHandler;
import com.github.craxlor.discordbot.listener.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

// TODO bring back reddit stuff
// TODO QOL checks on module enabling (e.g. before enabling module music check for the required api keys, the required dj role and send a notification to bind a musicLog channel )
public class Main {
    public static void main(String[] args) throws SQLException {
        Database database = Database.getInstance();
        database.setupTables();
        try {
            JDABuilder builder = JDABuilder.createDefault(Properties.get("DEV_TOKEN"));
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.setChunkingFilter(ChunkingFilter.ALL);
            builder.setLargeThreshold(50);
            JDA jda = builder.build();
            jda.addEventListener(new GuildPreparer());
            jda.addEventListener(new SlashCommandInteractionHandler());
            jda.addEventListener(new AutoroomHandler());
            jda.addEventListener(new MusicVoiceConnectionHandler());
            jda.getPresence().setActivity(Activity.watching("..."));
            // jda.updateCommands().addCommands().queue();
            jda.updateCommands().addCommands(Commandlist.getGlobalCommands().getCommandData()).queue();
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
