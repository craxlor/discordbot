package com.github.craxlor.discordbot.module.reddit.command.slash;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.json.simple.parser.ParseException;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.subreddit.SubReddit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RedditGallery extends SCAdmin {

    private static final String CREATE_NAME = "create";
    private static final String CREATE_DESCRIPTION = "Creates a reddit gallery.";
    private static final String CREATE_OPT_NAME = "subreddit";
    private static final String CREATE_OPT_DESCRIPTION = "Define a subreddit.";
    private static final String DELETE_NAME = "delete";
    private static final String DELETE_DESCRIPTION = "Deletes a reddit gallery.";
    private static final String DELETE_OPT_NAME = "gallery";
    private static final String DELETE_OPT_DESCRIPTION = "Select one of these";
    private static final String REMOVE_OPT_DELETE_NAME = "delete";
    private static final String REMOVE_OPT_DELETE_DESCRIPTION = "Select if the channel should be deleted.";

    private static final String CATEGORYNAME = "redditgalleries";

    public RedditGallery() {
        // create subCommand
        SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
        create.addOption(OptionType.STRING, CREATE_OPT_NAME, CREATE_OPT_DESCRIPTION, true);
        // delete subCommand
        SubcommandData delete = new SubcommandData(DELETE_NAME, DELETE_DESCRIPTION);
        delete.addOption(OptionType.CHANNEL, DELETE_OPT_NAME, DELETE_OPT_DESCRIPTION, true);
        delete.addOption(OptionType.BOOLEAN, REMOVE_OPT_DELETE_NAME, REMOVE_OPT_DELETE_DESCRIPTION);
        commandData.addSubcommands(create, delete);
    }

    @Override
    @Nonnull
    public String getName() {
        return "redditgallery";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "placeholder";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws ParseException, IOException {
        switch (event.getSubcommandName()) {
            case CREATE_NAME -> {
                return create(event);
            }
            case DELETE_NAME -> {
                return delete(event);
            }
        }
        return null;
    }

    @SuppressWarnings("null")
    private Reply create(SlashCommandInteractionEvent event) throws ParseException, IOException {
        String subredditName = event.getOption(CREATE_OPT_NAME).getAsString();
        Reddit reddit = new Reddit();
        SubReddit subReddit = reddit.getSubReddit(subredditName);
        if (subReddit == null) {
            return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
                    "The subreddit: " + subredditName + " does not exist!\nPlease check for the correct spelling.");
        }
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        GuildConfig config = guildManager.getGuildConfig();
        Category redditgalleriesCategory;
        // look if category exists already
        if (guild.getCategoriesByName(CATEGORYNAME, true).size() < 1) {
            // create category
            redditgalleriesCategory = guild.createCategory(CATEGORYNAME).complete();
            // save categoryid in config
            config.setRedditCategory(redditgalleriesCategory.getIdLong());
        } else {
            // get category via saved id in config
            redditgalleriesCategory = config.getRedditCategory();
        }
        // create textchannel
        TextChannel textChannel = redditgalleriesCategory.createTextChannel(subredditName).complete();
        // mark channel as nsfw if necessary
        if (subReddit.isOver18()) {
            textChannel.getManager().setNSFW(true).queue();
        }
        config.addRedditGallery(subredditName, textChannel.getIdLong());
        // reload tasks
        guildManager.getGalleryTasks().reloadTasks();
        return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS,
                "Created a gallery for the subreddit: " + subredditName + ".");
    }

    @SuppressWarnings("null")
    private Reply delete(SlashCommandInteractionEvent event) {
        TextChannel galleryChannel = event.getOption(DELETE_OPT_NAME)
                .getAsChannel()
                .asTextChannel();
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        GuildConfig config = guildManager.getGuildConfig();
        // stop task -> remove gallery in config & reload tasks
        config.removeRedditGallery(galleryChannel.getIdLong());
        guildManager.getGalleryTasks().reloadTasks();
        // delete textChannel
        OptionMapping optionMapping = event.getOption(REMOVE_OPT_DELETE_NAME);
        if (optionMapping != null && optionMapping.getAsBoolean())
            galleryChannel.delete().queue();

        return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS,
                "Deleted the gallery for the subreddit: " + galleryChannel.getName() + ".");
    }

}
