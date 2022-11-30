package com.github.craxlor.discordbot.command.module.autoroom.command;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.module.autoroom.command.slash.Customize;
import com.github.craxlor.discordbot.command.module.autoroom.command.slash.Setup;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.commandlist.CommandCollection;

public class AutoroomCollection extends ArrayList<SlashCommand> implements CommandCollection {

    public AutoroomCollection() {
        add(new Setup());
        add(new Customize());
    }
}