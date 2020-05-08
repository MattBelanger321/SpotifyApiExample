import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private SpotifyApi spotifyApi;   //Users Spotify Account
    private final Device device;    //Will Hold Raspberry Pi Name

    public App(SpotifyApi spotifyApi, String device_name){
        this.spotifyApi = spotifyApi;
        this.device = findDevice(device_name);
    }

    /*SEARCHES USERS ACCOUNT FOR AVAILABLE DEVICES AND RETURNS THE TARGET DEVICE*/
    private Device findDevice(String device_name) {
        Device[] deviceList =  null;
        try {
            deviceList = spotifyApi.getUsersAvailableDevices().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            System.err.println("DEVICE SEARCH ERROR: EXIT -20");
            System.exit(-20);
        }

        for(Device d: deviceList){
            if(d.getName().equals(device_name))
                return d;
        }
        System.err.println("DEVICE NOT FOUND");
        return null;
    }

    /*Creates a Search Request with Song as the Input Value
    * Then Adds the First Track To the End of the Users Playback Queue*/
    public int addToQueue(String song){
        //Creates SearchRequest
        SearchTracksRequest str = spotifyApi.searchTracks(song)
                .limit(1)
                .build();

        if(device != null){
            //Adds First Track
            Track track;
            try {
                track = str.execute().getItems()[0];
            } catch (IOException | SpotifyWebApiException | ParseException  e) {
                e.printStackTrace();
                System.err.println("SEARCH DISRUPTED: STATUS -1");
                return -1;
            }catch(ArrayIndexOutOfBoundsException e){
                System.err.println("TRACK NOT FOUND: STATUS 1");
                return 1;
            }

            assert track != null;
            String uri = track.getUri();
            try {
                spotifyApi.addItemToUsersPlaybackQueue(uri).device_id(device.getId()).build().execute(); //adds to queue
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
                System.err.println("FAILED TO ADD \""+song+"\": STATUS -2");
                return -2;
            }
        }else{//Device is null
            System.err.println("DEVICE NOT AVAILABLE: STATUS -3");
            return -3;
        }
        System.out.println("\""+song+"\" WAS ADDED SUCCESSFULLY");
        return 0;
    }

    public void launch() {
        int cont = 0;
        while(cont >=0){
            showMenu();
            cont = getTool();
        }
    }

    /*Prompts User For Selector Value*/
    private int getTool() {
        Scanner tool = new Scanner(System.in);
        System.out.print("Enter Selector Value: ");
        switch(tool.nextInt()){
            case 1: queueTool(); break;
            case -1: return -1;
        }
        return 0;
    }

    /*TODO: MAKE ME A CLASS*/
    private void queueTool() {
        int cont = 0;
        while(cont >=0){
            showQueueMenu();
            cont = getQueueOp();
        }
    }

    /*Prompts User for Selector Value*/
    private int getQueueOp() {
        int cont = 0;
        Scanner queue = new Scanner(System.in);
        System.out.print("Enter Selector Value (-1 to Return to Previous Menu): ");
        switch(queue.nextInt()){
            case 1:
                while(cont >= 0){
                    cont = addToQueue(getSearchValue());
                }
                break;
            case -1: cont = -1; break;
        }
        return cont;
    }

    /*Prompts User for Search String and Returns it*/
    private String getSearchValue() {
        Scanner search = new Scanner(System.in);

        String value = null;
        while(value==null || value.equals("")){
            System.out.print("Enter Search Value (-1 to Return to Previous Menu): ");
            value = search.nextLine();
        }
        return value;
    }
    /*Queue Menu Display*/
    private void showQueueMenu(){
        System.out.println("" +
                "Selection Value     Operation Description\n"+
                "01:                 Add Song to End of Queue\n"
        );
    }

    /*Main Menu Display*/
    private void showMenu() {
        System.out.println("" +
                "Selection Value     Tool Description\n"+
                "01:                 Queue\n"
        );
    }
}
