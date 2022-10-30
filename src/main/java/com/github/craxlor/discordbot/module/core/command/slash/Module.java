package com.github.craxlor.discordbot.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.module.autoroom.command.AutoroomCollection;
import com.github.craxlor.discordbot.module.music.command.MusicCollection;
import com.github.craxlor.discordbot.module.reddit.command.RedditCollection;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Module extends SCAdmin {

    private static final String ADD_NAME = "add";
    private static final String ADD_DESCRIPTION = "Adds a module.";
    private static final String REMOVE_NAME = "remove";
    private static final String REMOVE_DESCRIPTION = "Removes a module.";
    private static final String OPT_NAME = "module-option";
    private static final String OPT_DESCRIPTION = "Select a module to add or remove.";

    public static final String OPT_AUTOROOM_NAME = "autoroom";
    private static final String OPT_AUTOROOM_DESCRIPTION = "autoroom";
    public static final String OPT_MUSIC_NAME = "music";
    private static final String OPT_MUSIC_DESCRIPTION = "music";
    public static final String OPT_REDDIT_NAME = "reddit";
    private static final String OPT_REDDIT_DESCRIPTION = "reddit";

    public Module() {
        SubcommandData add = new SubcommandData(ADD_NAME, ADD_DESCRIPTION);
        SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
        OptionData optionData = new OptionData(OptionType.STRING, OPT_NAME, OPT_DESCRIPTION, true, false);
        optionData.addChoice(OPT_AUTOROOM_NAME, OPT_AUTOROOM_DESCRIPTION);
        optionData.addChoice(OPT_MUSIC_NAME, OPT_MUSIC_DESCRIPTION);
        optionData.addChoice(OPT_REDDIT_NAME, OPT_REDDIT_DESCRIPTION);
        add.addOptions(optionData);
        remove.addOptions(optionData);
        commandData.addSubcommands(add, remove);
    }

    @Override
    @Nonnull
    public String getName() {
        return "module";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "add or remove certain modules of the bot";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        String subcommandName = event.getSubcommandName();
        String statusDetail = "";
        Guild guild = event.getGuild();
        GuildConfig config = GuildManager.getGuildManager(guild).getGuildConfig();
        Commandlist commandlist = GuildManager.getGuildManager(guild).getCommandlist();
        String module = event.getOption(OPT_NAME).getAsString();
        switch (subcommandName) {
            case ADD_NAME -> {
                config.addModule(module);
                add(module, commandlist);
                statusDetail = "Added the module: **" + module + "**";
            }
            case REMOVE_NAME -> {
                config.removeModule(module);
                remove(module, commandlist);
                statusDetail = "Removed the module: **" + module + "**";
            }
        }
        statusDetail += "\nUpdating commands will take a while. Be patient. (:";
        // update commandlist
        guild.updateCommands().queue();
        guild.updateCommands().addCommands(commandlist.getGuildOnlyCommandsData()).queue();
        return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS, statusDetail);
    }

    public void add(String module, Commandlist commandlist) {
        switch (module) {
            case "autoroom" -> commandlist.addAll(new AutoroomCollection());
            case "music" -> commandlist.addAll(new MusicCollection());
            case "reddit" -> commandlist.addAll(new RedditCollection());
        }
    }

    public void remove(String module, Commandlist commandlist) {
        switch (module) {
            case "autoroom" -> commandlist.removeAll(new AutoroomCollection());
            case "music" -> commandlist.removeAll(new MusicCollection());
            case "reddit" -> commandlist.removeAll(new RedditCollection());
        }
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
