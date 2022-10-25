package com.github.craxlor.discordbot.module.music.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.craxlor.utilities.JSONFile;

public class YouTubeStorage extends JSONFile {

    private static YouTubeStorage INSTANCE;

    private JSONArray storage;
    private int quota;

    private YouTubeStorage() {
        super("resources", "youtubehistory");
        storage = root.get("storage") == null ? new JSONArray() : (JSONArray) root.get("storage");
        quota = root.get("quota") == null ? 10000 : ((Long) root.get("quota")).intValue();
    }

    public static YouTubeStorage getInstance() {
        if (INSTANCE == null)
            INSTANCE = new YouTubeStorage();
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public boolean add(@Nonnull JSONObject entry) {
        boolean b = storage.add(entry);
        put("storage", storage, true);
        return b;
    }

    public boolean containsVideoId(String videoId) {
        JSONObject entry;
        for (Object o : storage) {
            entry = (JSONObject) o;
            if (((String) entry.get("videoId")).equals(videoId))
                return true;
        }
        return false;
    }

    public boolean containsSearchTerm(String searchTerm) {
        JSONObject entry;
        for (Object o : storage) {
            entry = (JSONObject) o;
            if (((String) entry.get("searchTerm")).equals(searchTerm))
                return true;
        }
        return false;
    }

    @Nullable
    public JSONObject getByVideoId(String videoId) {
        JSONObject entry;
        for (Object o : storage) {
            entry = (JSONObject) o;
            if (((String) entry.get("videoId")).equals(videoId))
                return entry;
        }
        return null;
    }

    @Nullable
    public JSONObject getBySearchTerm(String searchTerm) {
        JSONObject entry;
        for (Object o : storage) {
            entry = (JSONObject) o;
            if (((String) entry.get("searchTerm")).equals(searchTerm))
                return entry;
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
