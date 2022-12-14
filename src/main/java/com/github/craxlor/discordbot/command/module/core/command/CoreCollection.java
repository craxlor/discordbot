package com.github.craxlor.discordbot.command.module.core.command;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.module.core.command.slash.Clear;
import com.github.craxlor.discordbot.command.module.core.command.slash.EmbedColor;
import com.github.craxlor.discordbot.command.module.core.command.slash.Module;
import com.github.craxlor.discordbot.command.module.core.command.slash.Ping;
import com.github.craxlor.discordbot.command.module.core.command.slash.Reload;
import com.github.craxlor.discordbot.command.module.core.command.slash.Role;
import com.github.craxlor.discordbot.command.module.core.command.slash.Shutdown;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.core.commandlist.CommandCollection;

public class CoreCollection extends ArrayList<SlashCommand> implements CommandCollection {

    public CoreCollection() {
        add(new Clear());
        add(new EmbedColor());
        add(new Ping());
        add(new Role());
        add(new Shutdown());
        add(new Reload());
        add(new Module());
    }

}
