package com.github.craxlor.discordbot.module.reddit.command.slash;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.module.reddit.util.RedditHelper;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.jReddit.Listings;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Meme extends SlashCommand {
    private static Logger logger = com.github.craxlor.utilities.Logger.getLogger("reddit");
    private final ArrayList<RedditPost> posted = new ArrayList<>();

    @Override
    public boolean memberHasPermission(@Nonnull Member member, @Nonnull Guild guild) {
        return true;
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        Reddit reddit = new Reddit();
        Random random = new Random();
        List<RedditPost> listing = reddit.getListing("dankmemes", Listings.HOT, 500);
        RedditPost redditPost = null;
        boolean bool = false;
        while (bool == false) {
            redditPost = listing.get(random.nextInt(listing.size()));
            if (posted.contains(redditPost))
                continue;
            bool = RedditHelper.canBePosted(redditPost);
        }
        posted.add(redditPost);
        logger.info("posted " + redditPost.getUrl_overridden_by_dest());
        return new Reply(event.deferReply(), false).onReddit(event, redditPost);
    }

    @Override
    @Nonnull
    public String getName() {
        return "meme";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "receive a random meme from r/dankmemes";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

}
