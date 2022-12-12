package com.github.craxlor.discordbot.database.element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiscordServer {

    private long guild_id, admin_id, dj_id, musicLog_id;
    @Nonnull
    private String name;
    @Nullable
    private String modules, colorHex;

    public DiscordServer(long guild_id, @Nonnull String name) {
        this.guild_id = guild_id;
        this.dj_id = -1l;
        this.admin_id = -1l;
        this.musicLog_id = -1l;
        this.name = name;
        this.modules = null;
        this.colorHex = null;
    }

    public DiscordServer(long guild_id, long admin_id, long dj_id, long musicLog_id,
            @Nonnull String name, @Nonnull String modules, @Nonnull String colorHex) {
        this.guild_id = guild_id;
        this.dj_id = dj_id;
        this.admin_id = admin_id;
        this.musicLog_id = musicLog_id;
        this.name = name;
        this.modules = modules;
        this.colorHex = colorHex;
    }

    // SETTER
    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    public void setDj_id(long dj_id) {
        this.dj_id = dj_id;
    }

    public void setAdmin_id(long admin_id) {
        this.admin_id = admin_id;
    }

    public void setMusicLog_id(long musicLog_id) {
        this.musicLog_id = musicLog_id;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    // GETTER
    public long getGuild_id() {
        return guild_id;
    }

    public long getDj_id() {
        return dj_id;
    }

    public long getAdmin_id() {
        return admin_id;
    }

    public long getMusicLog_id() {
        return musicLog_id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getModules() {
        return modules;
    }
    
    @Nullable
    public String getColorHex() {
        return colorHex;
    }

}
