package com.github.craxlor.discordbot;

import com.github.craxlor.discordbot.handler.GuildPreparer;
import com.github.craxlor.discordbot.handler.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.module.autoroom.handler.AutoroomHandler;
import com.github.craxlor.discordbot.module.music.handler.MusicVoiceConnectionHandler;
import com.github.craxlor.discordbot.module.reddit.handler.RedditHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

public class Main {
    public static void main(String[] args) {
        try {
            JDABuilder builder = JDABuilder.createDefault(com.github.craxlor.discordbot.Secrets.DEV_TOKEN);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.setChunkingFilter(ChunkingFilter.ALL);
            builder.setLargeThreshold(50);
            JDA jda = builder.build();
            jda.addEventListener(new GuildPreparer());
            jda.addEventListener(new SlashCommandInteractionHandler());
            jda.addEventListener(new AutoroomHandler());
            jda.addEventListener(new MusicVoiceConnectionHandler());
            jda.addEventListener(new RedditHandler());
            jda.getPresence().setActivity(Activity.watching("..."));
            // jda.updateCommands().addCommands().queue();
            jda.updateCommands().addCommands(Commandlist.getGlobalCommands().getCommandData()).queue();
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
