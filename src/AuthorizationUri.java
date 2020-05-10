import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AuthorizationUri {
    private static String clientId = "11681662b6c94af2a77730826269f808";
    private static String clientSecret = "b7192adc21374e389437cc8954b4298e";
    private static URI redirectUri = SpotifyHttpManager.makeUri("https://mattbelanger321.github.io/callback");

    private static SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();
    private AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .scope("user-read-playback-state,streaming,playlist-modify-public,user-library-modify,playlist-modify-private,app-remote-control")
            .show_dialog(true)
            .build();

    public  void authorizationCodeUri_Sync() {
        URI uri = authorizationCodeUriRequest.execute();

        openLink(uri);
    }

    private void openLink(URI uri) {
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop not supported!");
            System.exit(-1);
        }

        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.OPEN)) {
            try {
                desktop.browse(uri);
            }
            catch (IOException ioe) {
                System.err.println("Unable to open URI");
            }
        }
    }

    public  void authorizationCodeUri_Async() {
        try {
            CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            URI uri = uriFuture.join();

            //System.out.println("URI: " + uri.toString());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the Spotify Terminal App for Windows");
        System.out.println("Enter '1' for Initial Setup\nEnter Anything Else to Get Going!");
        if(new Scanner(System.in).nextLine().equals("1")){
            new AuthorizationUri().login();
        }else{
            String refreshToken = null;
            try {
                refreshToken = decodeToken();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.err.println("\nNO SAVED TOKEN: EXIT STATUS -7");
                System.exit(-7);
            }
            spotifyApi.setRefreshToken(refreshToken);
            App app = new App(spotifyApi,"LAPTOP-42MARVS2");
            app.launch();
        }
    }

    private static void login(String refreshToken) {

    }

    public static String decodeToken() throws FileNotFoundException {
       FileReader fr = new FileReader("pass.safe");
       String refreshToken = "";
       int c = '\0';    //holds character read from refresh token
       while(true){
           try {
               if ((c = fr.read()) == -1) break;
           } catch (IOException e) {
               e.printStackTrace();
           }
           refreshToken = (char)c!='-'?refreshToken + (char)(c-5):refreshToken + (char)c;
       }
       return refreshToken;
    }

    private void login() {
        Scanner scan = new Scanner(System.in);

        System.out.print(""+
                "LOGIN INSTRUCTIONS:\n" +
                "1: Look at the Site URL of the blank Redirect Page in your Address Bar\n" +
                "2: Find \"callback?code=--CODE HERE--\" in the Address bar\n"+
                "3: Copy the code and Paste it on the Next Line\n"
        );
        authorizationCodeUri_Sync();
        authorizationCodeUri_Async();
        System.out.print("Right Click the Text-Marker to Paste)> ");
        String code = scan.nextLine();

        AuthorizationCode ac = new AuthorizationCode(clientId,clientSecret,redirectUri,code);
        spotifyApi = ac.getSpotifyApi();

        AuthorizationRefresh ar = new AuthorizationRefresh(clientId,clientSecret,ac.getRefreshToken());
        spotifyApi = ar.getSpotifyApi();

        App app = new App(spotifyApi,"LAPTOP-42MARVS2");
        app.launch();
    }
}