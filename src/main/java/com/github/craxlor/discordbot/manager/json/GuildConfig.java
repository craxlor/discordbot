package com.github.craxlor.discordbot.manager.json;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.utilities.JSONFile;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GuildConfig extends JSONFile {

    private static String directoryPath = "resources" + File.separator + "guildconfigs" + File.separator;
    protected Guild guild;

    public GuildConfig(Guild guild) {
        super(directoryPath, guild.getId());
        this.guild = guild;
    }

    // GUILD NAME
    @SuppressWarnings("unchecked")
    public void setGuildName(String name) {
        JSONObject guild = root.get("guild") != null ? (JSONObject) root.get("guild") : new JSONObject();
        guild.put("name", name);
        put("guild", guild, true);
    }

    // GUILD ROLES
    @SuppressWarnings("unchecked")
    private void setRole(String name, long id) {
        JSONObject guild = root.get("guild") != null ? (JSONObject) root.get("guild") : new JSONObject();
        JSONObject roles = guild.get("roles") != null ? (JSONObject) guild.get("roles") : new JSONObject();
        roles.put(name, id);
        guild.put("roles", roles);
        put("guild", guild, true);
    }

    private Role getRole(String name) {
        JSONObject guild, roles;
        if ((guild = (JSONObject) root.get("guild")) == null)
            return null;
        if ((roles = (JSONObject) guild.get("roles")) == null)
            return null;

        Object o = roles.get(name);
        return o == null ? null : this.guild.getRoleById((long) o);
    }

    public void setAdminRole(long id) {
        setRole("admin", id);
    }

    public void setDJRole(long id) {
        setRole("dj", id);
    }

    public Role getAdminRole() {
        return getRole("admin");
    }

    public Role getDJRole() {
        return getRole("dj");
    }

    // GUILD EMBED COLOR
    @SuppressWarnings("unchecked")
    public void setEmbedColor(long red, long green, long blue) {
        JSONObject embedColor = new JSONObject();
        embedColor.put("red", red);
        embedColor.put("green", green);
        embedColor.put("blue", blue);
        JSONObject guild = root.get("guild") != null ? (JSONObject) root.get("guild") : new JSONObject();
        guild.put("embed-color", embedColor);
        put("guild", guild, true);
    }

    public void removeEmbedColor() {
        JSONObject guild;
        if ((guild = (JSONObject) root.get("guild")) == null) {
            return;
        }
        guild.remove("embed-color");
        put("guild", guild, true);
    }

    public Color getEmbedColor() {
        JSONObject guild, embedColor;
        if ((guild = (JSONObject) root.get("guild")) == null) {
            return null;
        }
        if ((embedColor = (JSONObject) guild.get("embed-color")) == null) {
            return null;
        }
        long red = (long) embedColor.get("red");
        long green = (long) embedColor.get("green");
        long blue = (long) embedColor.get("blue");
        return new Color((int) red, (int) green, (int) blue);
    }

    // MODULE
    @SuppressWarnings("unchecked")
    public void addModule(String module) {
        JSONArray modules = root.get("modules") != null ? (JSONArray) root.get("modules") : new JSONArray();
        modules.add(module);
        put("modules", modules, true);
    }

    public boolean removeModule(String module) {
        JSONArray modules = (JSONArray) root.get("modules");
        if (modules == null)
            return false;
        boolean b = modules.remove(module);
        put("modules", modules, true);
        return b;
    }

    public List<String> getModules() {
        JSONArray modules = (JSONArray) root.get("modules");
        if (modules == null) {
            return new ArrayList<>();
        }
        List<String> moduleList = new ArrayList<>();
        for (Object module : modules) {
            if (module != null) {
                moduleList.add(module.toString());
            }
        }
        return moduleList;
    }

    public boolean containsModule(String module) {
        JSONArray modules = (JSONArray) root.get("modules");
        if (modules == null)
            return false;
        return modules.contains(module);
    }

    // GENERIC METHODS
    @SuppressWarnings("unchecked")
    protected boolean removeNestedArrayElement(String elementKey, String nestedArrayKey, long channelID) {
        JSONObject element;
        if ((element = (JSONObject) root.get(elementKey)) == null)
            return false;
        JSONArray nestedArray;
        if ((nestedArray = (JSONArray) element.get(nestedArrayKey)) == null)
            return false;
        JSONObject channel;
        boolean b = false;
        for (Object o : nestedArray) {
            channel = (JSONObject) o;
            if ((long) channel.get("channel-id") == channelID) {
                b = nestedArray.remove(o);
                break;
            }
        }
        element.put(nestedArrayKey, nestedArray);
        put(elementKey, element, true);
        return b;
    }

    protected JSONObject getNestedArrayElement(String elementKey, String nestedElementKey, long channelID) {
        JSONArray nestedArray = getNestedArray(elementKey, nestedElementKey);
        if (nestedArray == null)
            return null;
        JSONObject channel;
        for (Object o : nestedArray) {
            channel = (JSONObject) o;
            if ((long) channel.get("channel-id") == channelID)
                return channel;
        }
        return null;
    }

    protected JSONArray getNestedArray(String elementKey, String nestedElementKey) {
        JSONObject element;
        if ((element = (JSONObject) root.get(elementKey)) == null)
            return null;
        return (JSONArray) element.get(nestedElementKey);
    }

    // AUTOROOM
    @SuppressWarnings("unchecked")
    public void addAutoroomTrigger(String staticName, long channelID, long categoryID) {
        JSONObject trigger = new JSONObject();
        trigger.put("name", staticName);
        trigger.put("channel-id", channelID);
        trigger.put("category-id", categoryID);

        JSONObject autoroom = root.get("autoroom") != null ? (JSONObject) root.get("autoroom") : new JSONObject();

        JSONArray triggers = autoroom.get("triggers") != null ? (JSONArray) autoroom.get("triggers") : new JSONArray();

        triggers.add(trigger);
        autoroom.put("triggers", triggers);
        put("autoroom", autoroom, true);
    }

    public boolean removeAutoroomTrigger(long channelID) {
        return removeNestedArrayElement("autoroom", "triggers", channelID);
    }

    public JSONObject getAutoroomTrigger(long channelID) {
        return getNestedArrayElement("autoroom", "triggers", channelID);
    }

    public boolean isAutoroomTrigger(long channelID) {
        JSONObject channels = getNestedArrayElement("autoroom", "triggers", channelID);
        if (channels == null)
            return false;
        return channels.get("channel-id") != null;
    }

    public JSONArray getAutoroomTriggers() {
        return getNestedArray("autoroom", "triggers");
    }

    // autoroom CHANNELS
    @SuppressWarnings("unchecked")
    public void addAutoroom(long channelID, long triggerID) {
        JSONObject channel = new JSONObject();
        channel.put("channel-id", channelID);
        channel.put("trigger-id", triggerID);
        JSONObject autoroom = root.get("autoroom") != null ? (JSONObject) root.get("autoroom") : new JSONObject();
        JSONArray channels = autoroom.get("channels") != null ? (JSONArray) autoroom.get("channels") : new JSONArray();
        channels.add(channel);
        autoroom.put("channels", channels);
        put("autoroom", autoroom, true);
    }

    public boolean removeAutoroom(long channelID) {
        return removeNestedArrayElement("autoroom", "channels", channelID);
    }

    public boolean isAutoroom(long channelID) {
        JSONObject channels = getNestedArrayElement("autoroom", "channels", channelID);
        if (channels == null)
            return false;
        return channels.get("channel-id") != null;
    }

    public JSONArray getAutorooms() {
        return getNestedArray("autoroom", "channels");
    }

    // MUSIC
    public void setMusicLog(long id) {
        put("music-log", id, true);
    }

    public void removeMusicLog() {
        remove("music-log", true);
    }

    public long getMusicLog() {
        Object o = root.get("music-log");
        return o == null ? -1 : (long) o;
    }

    // REDDIT
    @SuppressWarnings("unchecked")
    public void setRedditCategory(long id) {
        JSONObject reddit = root.get("reddit") != null ? (JSONObject) root.get("reddit")
                : new JSONObject();
        reddit.put("category-id", id);
        put("reddit", reddit, true);
    }

    public Category getRedditCategory() {
        JSONObject reddit;
        if ((reddit = (JSONObject) root.get("reddit")) == null)
            return null;
        Object o = reddit.get("category-id");
        return o == null ? null : guild.getCategoryById((long) o);
    }

    // REDDIT GALLERY
    @SuppressWarnings("unchecked")
    public void addRedditGallery(String name, long channelID) {
        JSONObject gallery = new JSONObject();
        gallery.put("name", name);
        gallery.put("channel-id", channelID);
        JSONObject reddit = root.get("reddit") != null ? (JSONObject) root.get("reddit") : new JSONObject();
        JSONArray galleries = reddit.get("galleries") != null ? (JSONArray) reddit.get("galleries") : new JSONArray();
        galleries.add(gallery);
        reddit.put("galleries", galleries);
        put("reddit", reddit, true);
    }

    public boolean removeRedditGallery(long channelID) {
        return removeNestedArrayElement("reddit", "galleries", channelID);
    }

    public TextChannel getRedditGallery(long channelID) {
        JSONObject redditgallery = getNestedArrayElement("reddit", "galleries", channelID);
        if (redditgallery == null)
            return null;
        Object o = (long) redditgallery.get("channel-id");
        return o == null ? null : guild.getTextChannelById((long) o);
    }

    public JSONArray getRedditGalleries() {
        return getNestedArray("reddit", "galleries");
    }
}
