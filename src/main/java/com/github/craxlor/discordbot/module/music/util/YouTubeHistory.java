package com.github.craxlor.discordbot.module.music.util;

import javax.annotation.Nullable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.utilities.JSONFile;

public class YouTubeHistory extends JSONFile {

    private JSONArray history;

    public YouTubeHistory() {
        super("resources", "youtubehistory");
        history = root.get("history") == null ? new JSONArray() : (JSONArray) root.get("history");
    }

    @SuppressWarnings("unchecked")
    public boolean add(String key, String value) {
        JSONObject entry = new JSONObject();
        entry.put("key", key);
        entry.put("value", value);
        boolean b = history.add(entry);
        put("history", history, true);
        return b;
    }

    public boolean contains(String key) {
        JSONObject entry;
        for (Object o : history) {
            entry = (JSONObject) o;
            if (((String) entry.get("key")).equals(key))
                return true;
        }
        return false;
    }

    @Nullable
    public String get(String key) {
        JSONObject entry;
        for (Object o : history) {
            entry = (JSONObject) o;
            if (((String) entry.get("key")).equals(key))
                return (String) entry.get("value");
        }
        return null;
    }
}
