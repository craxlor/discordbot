package com.github.craxlor.discordbot.manager.commandlist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.command.module.autoroom.command.AutoroomCollection;
import com.github.craxlor.discordbot.command.module.core.command.CoreCollection;
import com.github.craxlor.discordbot.command.module.music.command.MusicCollection;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Commandlist extends ArrayList<SlashCommand> {

    public static final String AUTOROOM = "autoroom";
    public static final String MUSIC = "music";
    public static final String REDDIT = "reddit";

    public boolean add(String module) {
        boolean b = false;
        switch (module) {
            case AUTOROOM -> b = addAll(new AutoroomCollection());
            case MUSIC -> b = addAll(new MusicCollection());
        }
        return b;
    }

    public boolean remove(String module) {
        boolean b = false;
        switch (module) {
            case AUTOROOM -> b = removeAll(new AutoroomCollection());
            case MUSIC -> b = removeAll(new MusicCollection());
        }
        return b;
    }

    @Nonnull
    public List<CommandData> getCommandData() {
        ArrayList<CommandData> result = new ArrayList<>();
        for (SlashCommand command : this) {
            result.add(command.getCommandData());
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
        // create a commandlist containing all modules
        final Commandlist commandlist = new Commandlist();
        commandlist.addAll(new CoreCollection());
        commandlist.addAll(new AutoroomCollection());
        commandlist.addAll(new MusicCollection());
        // create a 2nd commandlist containing all gloabl commands from the 1st
        // commandlist
        Commandlist result = new Commandlist();
        for (SlashCommand command : commandlist) {
            if (command.isGuildOnly() == false)
                result.add(command);
        }
        return result;
    }
}
