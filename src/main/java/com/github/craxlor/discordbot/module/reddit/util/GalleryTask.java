package com.github.craxlor.discordbot.module.reddit.util;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import com.github.craxlor.discordbot.reply.EmbedBuilder;
import com.github.craxlor.jReddit.Listings;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GalleryTask extends TimerTask {

    String subreddit;
    TextChannel messageChannel;
    Reddit reddit;
    private static Logger logger = com.github.craxlor.utilities.Logger.getLogger("reddit");
    ArrayList<RedditPost> posted;

    public GalleryTask(String subreddit, TextChannel messageChannel) {
        this.subreddit = subreddit;
        this.messageChannel = messageChannel;
        posted = new ArrayList<>();
        try {
            reddit = new Reddit();
        } catch (ParseException | IOException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        boolean isPostCompatible = false;
        String post_hint = null, url = null, mimeType = null;
        Random random = new Random();
        RedditPost redditPost = null;
        FileNameMap fileNameMap = null;

        // get some posts (500)
        List<RedditPost> listing = reddit.getListing(subreddit, Listings.HOT, 500);
        while (isPostCompatible == false) {
            // select a random post
            redditPost = listing.get(random.nextInt(listing.size()));
            // check if post has been used already
            if (posted.contains(redditPost))
                continue;
            // check if post contains usable content
            post_hint = redditPost.getPost_hint();
            if (post_hint != null && post_hint.equals("image"))
                url = redditPost.getUrl_overridden_by_dest();
            // skip turn if there's no usable content
            // bot cannot post files bigger than 8 mb
            if (url == null || new File(url).length() > 8388608)
                continue;
            // check mimetype of file
            fileNameMap = URLConnection.getFileNameMap();
            mimeType = fileNameMap.getContentTypeFor(url);
            if (mimeType.indexOf("image") == -1)
                continue;
            isPostCompatible = true;
        }
        // TODO save file option -> button or contextmenu
        // send message
        sendRedditPost(messageChannel, redditPost, url);
        posted.add(redditPost);
        logger.info("posted " + url);
    }

    // REDDIT-----------------------------------------
    public static void sendRedditPost(TextChannel channel, RedditPost redditPost, String url) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setRedditFormat(channel.getGuild(), redditPost, url);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
