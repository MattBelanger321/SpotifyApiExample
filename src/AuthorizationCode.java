import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthorizationCode{
    private static final String clientId = "11681662b6c94af2a77730826269f808";
    private static final String clientSecret = "b7192adc21374e389437cc8954b4298e";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("https://mattbelanger321.github.io/callback");
    private static final String code = "AQB-5vvyKTKf5BEJMra0usdGOo-RYcVKKklApo7g2izjhzWbiDaclJVZr0WrF3vnmFmqnrJI4lq_v5lx1FnxgxoGDEt54Pz_e9eXM0heMZ9-Z4CimVlxAXdEp7SS9Bwmf-PITw1u698KUYY8P9bX28fxmlr9A1MCb6jiNECkx1cwal7Q1RXka_cP_GOb8EnjCQ2ditFzQ15bq-CsKnJqZPZ_Mz4tzEL4HNLXsc3_tK2QJYGeRBbTSbvvn7jtTJ4HShvoNwP_zPKOfKm1vTFwhD5CwUKI11qjpPIluyeezlY61e0u1-s6FZsC67tp4YPKCvu6jpOegVKO";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();
    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
            .build();

    public static void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            System.out.println(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void authorizationCode_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public static void main(String[] args) {
        authorizationCode_Sync();
        authorizationCode_Async();
    }
}