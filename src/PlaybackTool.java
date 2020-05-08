import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.Device;

public class PlaybackTool extends Tool{
    private static SpotifyApi spotifyApi;
    private static Device device;

    public static void tool(SpotifyApi spotifyApi1, Device device1) {
        spotifyApi = spotifyApi1;
        device = device1;

        int cont = 0;
        while(cont >=0){
            showToolMenu();
            cont = getOp();
        }
    }


    protected static void showToolMenu() {
        System.out.println("" +
                "Selection Value     Operation Description\n"
        );
    }


    protected static int getOp() {
        return 0;
    }
}
