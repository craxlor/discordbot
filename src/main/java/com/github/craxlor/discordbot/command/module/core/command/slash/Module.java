package com.github.craxlor.discordbot.command.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.DiscordServer;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.commandlist.Commandlist;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

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
        Commandlist commandlist = GuildManager.getGuildManager(guild).getCommandlist();
        String module = event.getOption(OPT_NAME).getAsString();
        Database database = Database.getInstance();
        DiscordServer discordServer = database.getDiscordServer(event.getGuild().getIdLong());
        String modules = discordServer.getModules();
        switch (subcommandName) {
            case ADD_NAME -> {
                if (modules == null)
                    discordServer.setModules(module);
                else
                    discordServer.setModules(modules + "," + module);

                commandlist.add(module);
                statusDetail = "Added the module: **" + module + "**";
            }
            case REMOVE_NAME -> {
                if (modules.contains(module)) {
                    if (modules.contains(",")) { // contains multiple modules
                        String[] mArray = modules.split(",");
                        modules = "";
                        for (int i = 0; i < mArray.length; i++) {
                            if (mArray[i].equalsIgnoreCase(module)) // skip module that shall be removed
                                continue;
                            // add , after each module except the last
                            if (i + 1 < mArray.length)
                                modules += mArray[i] + ",";
                            else
                                modules += mArray[i];
                        }
                    } else { // contains only one module
                        modules = null;
                    }
                    discordServer.setModules(modules);
                }
                commandlist.remove(module);
                statusDetail = "Removed the module: **" + module + "**";
            }
        }
        database.update(discordServer);

        statusDetail += "\nUpdating commands will take a while. Be patient. (:";
        // update commandlist
        guild.updateCommands().queue();
        guild.updateCommands().addCommands(commandlist.getGuildCommands().getCommandData()).queue();
        return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS, statusDetail);
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
