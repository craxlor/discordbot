package com.github.craxlor.discordbot.module.reddit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.jReddit.Listings;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GalleryTask extends TimerTask {

    String subreddit;
    TextChannel messageChannel;
    Reddit reddit;
    private static Logger logger;
    ArrayList<RedditPost> posted;

    public GalleryTask(String subreddit, TextChannel messageChannel) {
        this.subreddit = subreddit;
        this.messageChannel = messageChannel;
        posted = new ArrayList<>();
        MDC.put("filename", "reddit");
        logger = LoggerFactory.getLogger("sift");
        try {
            reddit = new Reddit();
        } catch (ParseException | IOException e) {
            logger.warn(e.getLocalizedMessage());
        }
    }

    @Override
    @SuppressWarnings("null")
    public void run() {
        boolean isPostCompatible = false;
        Random random = new Random();
        RedditPost redditPost = null;

        // get some posts (500)
        List<RedditPost> listing = reddit.getListing(subreddit, Listings.HOT, 500);
        // todo export file validation to make it reusable for other commands
        while (isPostCompatible == false) {
            // select a random post
            redditPost = listing.get(random.nextInt(listing.size()));
            // check if post has been used already
            if (posted.contains(redditPost))
                continue;
            isPostCompatible = RedditHelper.canBePosted(redditPost);
        }
        // send message
        RedditHelper.sendRedditPost(messageChannel, redditPost);
        posted.add(redditPost);
        logger.info("posted " + redditPost.getUrl_overridden_by_dest());
    }

}
