package com.github.craxlor.discordbot.module.autoroom.command;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.commandlist.CommandCollection;
import com.github.craxlor.discordbot.module.autoroom.command.slash.Customize;
import com.github.craxlor.discordbot.module.autoroom.command.slash.Setup;

public class AutoroomCollection extends ArrayList<SlashCommand> implements CommandCollection {

    public AutoroomCollection() {
        add(new Setup());
        add(new Customize());
    }
}