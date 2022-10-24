package com.github.craxlor.discordbot.module.music.util;

import javax.annotation.Nullable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.utilities.JSONFile;

public class YouTubeHistory extends JSONFile {

    private JSONArray history;

    public YouTubeHistory() {
        super("resources", "youtubehistory");
        if ((history = (JSONArray) root.get("history")) == null) {
            history = new JSONArray();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean add(String key, String value) {
        JSONObject object = new JSONObject();
        object.put("key", key);
        object.put("value", value);
        boolean b = history.add(object);
        put("history", history, true);
        return b;
    }

    public boolean contains(String key) {
        JSONObject e;
        for (Object entry : history) {
            e = (JSONObject) entry;
            if (((String) e.get("key")).equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public String get(String key) {
        JSONObject e;
        for (Object entry : history) {
            e = (JSONObject) entry;
            if (((String) e.get("key")).equals(key)) {
                return (String) e.get("value");
            }
        }
        return null;
    }
}
