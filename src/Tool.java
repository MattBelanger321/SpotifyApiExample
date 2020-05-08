import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.Device;

abstract class Tool {
    public static void tool(SpotifyApi spotifyApi1, Device device1) {
        System.err.println("METHOD NOT OVERRIDDEN: STATUS -1000");
    }

    //*Returns Users Queue Menu Selection*/
    protected static int getOp() {
        System.err.println("METHOD NOT OVERRIDDEN: STATUS -1000");
        return -1000;
    }

    /* Menu Display*/
    protected static void showToolMenu() {
        System.err.println("METHOD NOT OVERRIDDEN: STATUS -1000");
    }
}
