import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthorizationRefresh{
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private SpotifyApi spotifyApi;
    private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;

    public AuthorizationRefresh(String clientId, String clientSecret, String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
        authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
        authorizationCodeRefresh_Sync();
        authorizationCodeRefresh_Async();
    }

    public void authorizationCodeRefresh_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void authorizationCodeRefresh_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRefreshRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
/*
    public static void main(String[] args) throws ParseException, SpotifyWebApiException, IOException {
        authorizationCodeRefresh_Sync();
        authorizationCodeRefresh_Async();

        App app = new App(spotifyApi,"LAPTOP-42MARVS2");
        app.launch();
    }
*/
}