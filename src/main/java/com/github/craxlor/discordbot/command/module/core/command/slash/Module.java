package com.github.craxlor.discordbot.command.module.core.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.element.DiscordServer;
import com.github.craxlor.discordbot.util.Properties;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.core.commandlist.Commandlist;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
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

    public Module() {
        SubcommandData add = new SubcommandData(ADD_NAME, ADD_DESCRIPTION);
        SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
        OptionData optionData = new OptionData(OptionType.STRING, OPT_NAME, OPT_DESCRIPTION, true, false);
        optionData.addChoice(Commandlist.AUTOROOM, Commandlist.AUTOROOM);
        optionData.addChoice(Commandlist.MUSIC, Commandlist.MUSIC);
        optionData.addChoice(Commandlist.REDDIT, Commandlist.REDDIT);
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
        // commandlist for the guild
        Commandlist commandlist = GuildManager.getGuildManager(guild).getCommandlist();
        Database database = Database.getInstance();
        // database entry, that will be edited
        DiscordServer discordServer = database.getDiscordServer(event.getGuild().getIdLong());
        // module that shall be toggled
        String module = event.getOption(OPT_NAME).getAsString();
        // already enabled modules
        String modules = discordServer.getModules();
        Status status = Status.ERROR;
        switch (subcommandName) {
            case ADD_NAME -> {
                // check if module can be activated
                switch (module) {
                    case Commandlist.MUSIC -> {
                        // check if all necessary entries exist in properties file
                        String yak = Properties.get("YOUTUBE_API_KEY");
                        String sci = Properties.get("SPOTIFY_CLIENT_ID");
                        String scs = Properties.get("SPOTIFY_CLIENT_SECRET");
                        if (yak == null || sci == null || scs == null) {
                            statusDetail = "the music module requires a Youtube API Key and Spotify API Access (set tokens in .properties file)";
                            status = Status.FAIL;
                            return new Reply(event.deferReply(), false).onCommand(event, status, statusDetail);
                        }
                        Member owner = guild.getOwner();
                        PrivateChannel privateChannel = owner.getUser().openPrivateChannel().complete();
                        if (discordServer.getDj_id() < 1) {
                            // dj role notification, only if role hasn't been set already
                            privateChannel.sendMessage(
                                    "It is advisable to bind a DJ role, otherwise no one can use the music module commands.\n/role dj")
                                    .queue();
                        }
                        // music log channel notification
                        privateChannel.sendMessage(
                                "A log channel can be set up to better view the playlist activities of the bot.\n/musicLog bind")
                                .queue();
                    }
                }
                // edit database entry
                if (modules == null)
                    discordServer.setModules(module);
                else
                    discordServer.setModules(modules + "," + module);
                commandlist.add(module);
                status = Status.SUCCESS;
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
                    commandlist.remove(module);
                    status = Status.SUCCESS;
                    statusDetail = "Removed the module: **" + module + "**";
                }
            }
        }
        database.update(discordServer);
        statusDetail += "\nUpdating commands will take a while. Please be patient. (:";
        // update commandlist
        guild.updateCommands().queue();
        guild.updateCommands().addCommands(commandlist.getGuildCommands().getCommandData()).queue();
        return new Reply(event.deferReply(), false).onCommand(event, status, statusDetail);
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
