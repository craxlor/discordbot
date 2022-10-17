package com.github.craxlor.discordbot.module.reddit.command;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.commandlist.CommandCollection;
import com.github.craxlor.discordbot.module.reddit.command.slash.Meme;
import com.github.craxlor.discordbot.module.reddit.command.slash.RedditGallery;

public class RedditCollection extends ArrayList<SlashCommand> implements CommandCollection {
    public RedditCollection() {
        add(new RedditGallery());
        add(new Meme());
    }
}
