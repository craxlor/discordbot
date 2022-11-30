package com.github.craxlor.discordbot.command.module.music.command;

import java.util.ArrayList;

import com.github.craxlor.discordbot.command.module.music.command.slash.Disconnect;
import com.github.craxlor.discordbot.command.module.music.command.slash.MusicLog;
import com.github.craxlor.discordbot.command.module.music.command.slash.Pause;
import com.github.craxlor.discordbot.command.module.music.command.slash.Play;
import com.github.craxlor.discordbot.command.module.music.command.slash.Queue;
import com.github.craxlor.discordbot.command.module.music.command.slash.Resume;
import com.github.craxlor.discordbot.command.module.music.command.slash.Skip;
import com.github.craxlor.discordbot.command.module.music.command.slash.Song;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.manager.commandlist.CommandCollection;

public class MusicCollection extends ArrayList<SlashCommand> implements CommandCollection {

    public MusicCollection() {
        add(new Disconnect());
        add(new Pause());
        add(new Play());
        add(new Queue());
        add(new Resume());
        add(new Skip());
        add(new Song());
        add(new MusicLog());
    }
}
