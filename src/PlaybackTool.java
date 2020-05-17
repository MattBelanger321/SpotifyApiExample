import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class PlaybackTool{
    private static SpotifyApi spotifyApi;
    private static Device device;

    public static void tool(SpotifyApi spotifyApi1, Device device1) {
        spotifyApi = spotifyApi1;
        device = device1;

        int cont = 0;
        while(cont >=0){
            showPlaybackMenu();
            cont = getPlaybackOp();
        }
    }


    protected static void showPlaybackMenu() {
        System.out.println("" +
                "Selection Value        Operation Description\n"+
                "1:                     Play Spotify\n" +
                "2:                     Pause Playback\n"+
                ""
        );
    }


    private static int getPlaybackOp() {
        int cont; //Determines what will happen after after operation is completed
        switch(App.getInt()){
            case 1:
                cont = play();
                break;
            case 2:
                cont = stop();
                break;
            case -1: cont = -1; break;
            default: System.err.println("INVALID INPUT: STATUS -100"); return -100;
        }
        return cont;
    }

    /*Pauses Current Playing Song or Does Nothing if No Music is Playing*/
    private static int stop() {
        try {
            spotifyApi.pauseUsersPlayback().build().execute();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            /*TODO DIFERENTIATE*/
            System.err.println("MAKE SURE "+device.getName() + "is your active device\nOr no music is playing");
        }
        return 0;
    }

    private static int play() {
        try {
            spotifyApi.startResumeUsersPlayback().build().execute();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            /*TODO DIFERENTIATE*/
            System.err.println("MAKE SURE "+device.getName() + "is your active device\nOr music is already playing");
        }
        return 0;
    }
}
