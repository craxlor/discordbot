package com.github.craxlor.discordbot.command.module.reddit.slash;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.reddit.RedditHelper;
import com.github.craxlor.discordbot.util.reply.EmbedBuilder;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.jReddit.Listings;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Meme extends SlashCommand {
    List<String> postedList;
    List<String> subreddits;

    public Meme() {
        super();
        postedList = new ArrayList<>();
        subreddits = new ArrayList<>();
        subreddits.add("dankmemes");
        subreddits.add("memes");
        subreddits.add("AdviceAnimals");
        subreddits.add("MemeEconomy");
        subreddits.add("ComedyCemetry");
    }

    @Override
    public boolean memberHasPermission(@Nonnull Member member, @Nonnull Guild guild) {
        return true;
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        Reddit reddit = new Reddit();
        String subReddit = subreddits.get(new Random().nextInt(subreddits.size()));
        // get 50 posts
        List<RedditPost> redditPosts = reddit.getListing(subReddit, Listings.HOT, 50);
        reddit.close();
        for (RedditPost redditPost : redditPosts) {
            if (postedList.contains(redditPost.getId())) {
                continue;
            }
            if (RedditHelper.canBePosted(redditPost)) {
                postedList.add(redditPost.getId());
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setRedditFormat(event.getGuild(), redditPost);
                event.getHook().setEphemeral(false).sendMessageEmbeds(embedBuilder.build()).queue();
                break;
            }
        }
        if (postedList.size() > 200)
            postedList.clear();
        return null;
    }

    @Override
    @Nonnull
    public String getName() {
        return "meme";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "get a random meme from a random meme subreddit";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

}
