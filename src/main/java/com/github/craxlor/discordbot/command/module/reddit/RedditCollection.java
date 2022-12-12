package com.github.craxlor.discordbot.command.module.reddit;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.module.reddit.slash.RedditGallery;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.core.commandlist.CommandCollection;

public class RedditCollection extends ArrayList<SlashCommand> implements CommandCollection {

    public RedditCollection() {
        add(new RedditGallery());
    }

}
