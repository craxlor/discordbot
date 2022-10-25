package com.github.craxlor.discordbot.module.music.util;

import javax.annotation.Nullable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.utilities.JSONFile;

public class YouTubeHistory extends JSONFile {

    private static YouTubeHistory INSTANCE;

    private JSONArray history;
    private int quota;

    private YouTubeHistory() {
        super("resources", "youtubehistory");
        history = root.get("searchHistory") == null ? new JSONArray() : (JSONArray) root.get("searchHistory");
        quota = root.get("quota") == null ? 10000 : (int) root.get("quota");
    }

    public static YouTubeHistory getInstance() {
        if (INSTANCE == null)
            INSTANCE = new YouTubeHistory();
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public boolean add(String key, String value) {
        JSONObject entry = new JSONObject();
        entry.put("key", key);
        entry.put("value", value);
        boolean b = history.add(entry);
        put("searchHistory", history, false);
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

    public void trackQuota(int cost) {
        quota -= cost;
        put("quota", quota, true);
    }

    public int getQuota() {
        return quota;
    }
}
