package com.github.craxlor.discordbot.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.ICommand;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand implements ICommand {

    @Nonnull
    protected SlashCommandData commandData;

    public SlashCommand() {
        commandData = Commands.slash(getName(), getDescription());
        commandData.setGuildOnly(isGuildOnly());
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return commandData;
    }
    
    
}
