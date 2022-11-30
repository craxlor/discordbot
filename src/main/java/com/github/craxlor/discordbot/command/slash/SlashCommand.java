package com.github.craxlor.discordbot.command.slash;

import com.github.craxlor.discordbot.command.Command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand implements Command {

    protected SlashCommandData commandData;

    public SlashCommand() {
        commandData = Commands.slash(getName(), getDescription());
        commandData.setGuildOnly(isGuildOnly());
    }

    @Override
    public CommandData getCommandData() {
        return commandData;
    }
    
    
}
