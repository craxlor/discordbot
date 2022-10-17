package com.github.craxlor.discordbot.manager.commandlist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.module.autoroom.command.AutoroomCollection;
import com.github.craxlor.discordbot.module.core.command.CoreCollection;
import com.github.craxlor.discordbot.module.music.command.MusicCollection;
import com.github.craxlor.discordbot.module.reddit.command.RedditCollection;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Commandlist extends ArrayList<SlashCommand> {

    @Nonnull
    public List<CommandData> getCommandData() {
        ArrayList<CommandData> result = new ArrayList<>();
        for (SlashCommand command : this) {
            result.add(command.getCommandData());
        }
        return result;
    }

    public List<CommandData> getGuildOnlyCommandsData() {
        ArrayList<CommandData> result = new ArrayList<>();
        for (SlashCommand command : this) {
            if (command.isGuildOnly()) {
                result.add(command.getCommandData());
            }
        }
        return result;
    }

    @Nullable
    public SlashCommand find(String slashCommandName) {
        for (SlashCommand slashCommand : this) {
            if (slashCommand.getCommandData().getName().equals(slashCommandName))
                return slashCommand;
        }
        return null;
    }

    public Commandlist getGuildCommands() {
        Commandlist guildCommands = new Commandlist();
        for (SlashCommand slashCommand : this) {
            if (slashCommand.isGuildOnly()) {
                guildCommands.add(slashCommand);
            }
        }
        return guildCommands;
    }

    public boolean removeAll(Commandlist commandlist) {
        boolean change = false;
        SlashCommand c;
        for (SlashCommand slashCommand : commandlist) {
            if ((c = find(slashCommand.getCommandData().getName())) != null) {
                remove(c);
                change = true;
            }
        }
        return change;
    }

    @Nonnull
    public static Commandlist getGlobalCommands() {
        final Commandlist commandlist = new Commandlist();
        commandlist.addAll(new CoreCollection());
        commandlist.addAll(new AutoroomCollection());
        commandlist.addAll(new MusicCollection());
        commandlist.addAll(new RedditCollection());
        Commandlist result = new Commandlist();
        for (SlashCommand command : commandlist) {
            if (command.isGuildOnly() == false)
                result.add(command);
        }
        return result;
    }
}
