package com.github.craxlor.discordbot.module.music.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.Secrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;

public class YouTubeHelper {

    private static final Logger logger = com.github.craxlor.utilities.Logger.getLogger("youtube");

    public static YouTube getService() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        };
        YouTubeRequestInitializer youTubeRequestInitializer = new YouTubeRequestInitializer(
                Secrets.YOUTUBE_API_KEY);
        return new YouTube.Builder(httpTransport, GsonFactory.getDefaultInstance(), httpRequestInitializer)
                .setApplicationName("selfmadecrapcode")
                .setYouTubeRequestInitializer(youTubeRequestInitializer).build();

    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    @Nullable
    public static String getVideoURLBySearchTerm(@Nonnull String searchTerm) {
        try {
            YouTube youtubeService = getService();
            YouTube.Search.List request = youtubeService.search()
                    .list("snippet");
            SearchListResponse response = request
                    .setQ(searchTerm)
                    .setSafeSearch("none")
                    .setType("video")
                    .execute();
            // return URL for the 1st video from the response
            return "https://www.youtube.com/watch?v=" + response.getItems().get(0).getId().getVideoId();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
            return null;
        }

    }
}
