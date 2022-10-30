package com.github.craxlor.discordbot.module.music.util;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.core5.http.ParseException;

import com.github.craxlor.discordbot.Properties;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public class SpotifyHelper {

    private static final Logger logger = com.github.craxlor.utilities.Logger.getLogger("spotify");

    public static SpotifyApi getSpotifyApi() throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(Properties.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(Properties.get("SPOTIFY_CLIENT_SECRET"))
                .build();
        // clientcredentials
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        ClientCredentials clientCredentials = clientCredentialsRequest.execute();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        return spotifyApi;

    }

    @Nullable
    public static Track getTrack(@Nonnull String url) {
        try {
            String trackID = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
            SpotifyApi spotifyApi = getSpotifyApi();
            GetTrackRequest trackRequest = spotifyApi.getTrack(trackID).build();
            return trackRequest.execute();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
            return null;
        }

    }
}
