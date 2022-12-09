package com.github.craxlor.discordbot.database.element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YouTubeSearch {
    @Nonnull
    private String video_id, video_title, channel_id;
    @Nullable
    private String searchTerm;

    public YouTubeSearch(@Nonnull String video_id, @Nonnull String video_title, @Nonnull String channel_id,
            @Nonnull String searchTerm) {
        this.video_id = video_id;
        this.video_title = video_title;
        this.channel_id = channel_id;
        this.searchTerm = searchTerm;
    }

    public YouTubeSearch(@Nonnull String video_id, @Nonnull String video_title, @Nonnull String channel_id) {
        this.video_id = video_id;
        this.video_title = video_title;
        this.channel_id = channel_id;
        searchTerm = null;
    }

    // SETTER
    public void setVideo_id(@Nonnull String video_id) {
        this.video_id = video_id;
    }

    public void setVideo_title(@Nonnull String video_title) {
        this.video_title = video_title;
    }

    public void setChannel_id(@Nonnull String channel_id) {
        this.channel_id = channel_id;
    }

    public void setSearchTerm(@Nonnull String searchTerm) {
        this.searchTerm = searchTerm;
    }

    // GETTER
    @Nonnull
    public String getVideo_id() {
        return video_id;
    }

    @Nonnull
    public String getVideo_title() {
        return video_title;
    }

    @Nonnull
    public String getChannel_id() {
        return channel_id;
    }

    @Nullable
    public String getSearchTerm() {
        return searchTerm;
    }

    public String getVideoThumbnailURL() {
        return "https://i.ytimg.com/vi/" + video_id + "/hqdefault.jpg";
    }

    public String getVideoURL() {
        return "https://www.youtube.com/watch?v=" + video_id;
    }

    public String getChannelURL() {
        return "https://www.youtube.com/channel/" + channel_id;
    }
}
