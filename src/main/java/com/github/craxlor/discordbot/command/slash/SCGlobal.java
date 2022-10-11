package com.github.craxlor.discordbot.command.slash;

public abstract class SCGlobal extends SlashCommand {

    @Override
    public boolean isGuildOnly() {
        return false;
    }
}
