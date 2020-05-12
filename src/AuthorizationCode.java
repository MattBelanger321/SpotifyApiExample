import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthorizationCode{
    private  String clientId;
    private String clientSecret;
    private URI redirectUri;
    private  String code;
    private String refreshToken;

    private SpotifyApi spotifyApi;
    private AuthorizationCodeRequest authorizationCodeRequest;
    public AuthorizationCode(String clientId,String clientSecret,URI redirectUri,String code){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.code = code;

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
        authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        authorizationCode_Sync();
        authorizationCode_Async();
    }
    public void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            String token = authorizationCodeCredentials.getRefreshToken();
            spotifyApi.setRefreshToken(token);
            setRefreshToken(token);
            encodeToken(token);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void encodeToken(String refreshToken) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("pass.safe");
        char c; //will hold current character of the refreshToken
        for(int i = 0; i < refreshToken.length(); i++){
            c = refreshToken.charAt(i);
            pw.printf("%c",c != '-'?c+5:c);
        }
        pw.close();
    }

    private String setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this.refreshToken;
    }

    public void authorizationCode_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        } catch (CompletionException e) {
            //System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
    public SpotifyApi getSpotifyApi(){
        return spotifyApi;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}