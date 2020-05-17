import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static SpotifyApi spotifyApi;   //Users Spotify Account
    public static Device device;    //Will Hold Raspberry Pi Name

    public App(SpotifyApi spotifyApi, String device_name,boolean init){
        App.spotifyApi = spotifyApi;
        if(init){
            System.out.println("SIGN IN SUCCESSFUL");
            refreshAccess();
        }else{
            System.out.println("WELCOME BACK!");
        }
        device = findDevice(device_name);
    }

    /*SEARCHES USERS ACCOUNT FOR AVAILABLE DEVICES AND RETURNS THE TARGET DEVICE*/
    private Device findDevice(String device_name) {
        Device[] deviceList =  null;
        try {
            deviceList = spotifyApi.getUsersAvailableDevices().build().execute();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("DEVICE SEARCH ERROR: EXIT -20");
            System.exit(-20);
        }catch(SpotifyWebApiException e){
            System.err.println("AUTHORIZATION FAILED: RETRYING...");
            refreshAccess();
            try {
                deviceList = spotifyApi.getUsersAvailableDevices().build().execute();
            } catch (IOException | ParseException | SpotifyWebApiException ex) {
                System.err.println("AUTHORIZATION REVOKED: EXIT STATUS -500");
                System.exit(-500);
            }
        }
        for(Device d: deviceList){
            if(d.getName().equals(device_name))
                return d;
        }

        System.err.println("DEVICE SEARCH ERROR: Retrying...\nLAUNCHING SPOTIFY FOR WINDOWS");
        try {
            openSpotify();
            deviceList = spotifyApi.getUsersAvailableDevices().build().execute();
        } catch (IOException ex) {
            System.err.println("UNABLE TO OPEN SPOTIFY: EXIT STATUS: -20");
            System.exit(-20);
        } catch (ParseException | SpotifyWebApiException ex) {
            System.err.println("UNABVLE TO FIND DEVICE: EXIT STATUS -21");
            System.exit(-21);
        }
        for(Device d: deviceList){
            if(d.getName().equals(device_name))
                return d;
        }
        System.exit(-20);
        return null;
    }

    private void openSpotify() throws IOException {
        Runtime.getRuntime().exec("Spotify.exe");
        try {
            System.out.println("WAITING FOR SPOTIFY TO OPEN...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void refreshAccess() {
        try {
            spotifyApi.setAccessToken(spotifyApi.authorizationCodeRefresh().build().execute().getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    /* Main Program starts here*/
    public void launch() {
        int cont = 0;
        while(cont >=0){
            showMenu();
            cont = getTool();
        }
    }

    /*The selected tool is started*/
    private int getTool() {
        switch(getInt()){
            case 1: QueueTool.tool(spotifyApi,device); break;
            case 2: PlaybackTool.tool(spotifyApi,device); break;
            case -1: return -1;
            default: System.err.println("INVALID INPUT: STATUS -100"); return -100;
        }
        return 0;
    }

    /*Prompts User for Selector Value*/
    public static int getInt() {
        Scanner getter = new Scanner(System.in);
        String value;
        int parsedValue;
        while(true){
            System.out.print("\nEnter Selector Value: ");
            value = getter.nextLine();
            try{
                parsedValue = Integer.parseInt(value);
                break;
            }catch(NumberFormatException e){
                System.err.println("INVALID INPUT: STATUS -1");
            }
        }
        return parsedValue;
    }

    /*Prompts User for Search String and Returns it*/
    public static String getString() {
        Scanner search = new Scanner(System.in);

        String value = null;
        System.out.print("\nEnter Search Value (-1 to Return to Previous Menu): ");
        while(value==null || value.equals("")){
            value = search.nextLine();
        }
        return value;
    }

    /*Main Menu Display*/
    public static void showMenu() {
        System.out.println("" +
                "Selection Value     Tool Description\n"+
                "01:                 Queue\n" +
                "02:                 Playback Control"
        );
    }
}
