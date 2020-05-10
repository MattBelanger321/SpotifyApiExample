import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

class QueueTool{
    private static SpotifyApi spotifyApi;
    private static Device device;
    public static void tool(SpotifyApi spotifyApi1, Device device1) {
        spotifyApi = spotifyApi1;
        device = device1;

        int cont = 0;
        while(cont >=0){
            showQueueMenu();
            cont = getQueueOp();
        }
    }

    //*Returns Users Queue Menu Selection*/
    private static int getQueueOp() {
        int cont = 0;
        switch(App.getInt()){
            case 1:
                while(cont >= 0){
                    cont = addToQueue(App.getString());
                }
                break;
            case -1: cont = -1; break;
            default: System.err.println("INVALID INPUT: STATUS -100"); return -100;
        }
        return cont;
    }

    /*Queue Menu Display*/
    private static void showQueueMenu(){
        System.out.println("" +
                "Selection Value     Operation Description\n"+
                "01:                 Add Song to End of Queue\n"
        );
    }

    /*Creates a Search Request with Song as the Input Value
     * Then Adds the First Track To the End of the Users Playback Queue*/
    private static int addToQueue(String song){
        if(song.equals("-1")||song.equals("")){
            return -1;
        }
        //Creates SearchRequest
        SearchTracksRequest str = spotifyApi.searchTracks(song)
                .limit(1)
                .build();
        Track track;
        if(device != null){
            //Adds First Track
            try {
                track = str.execute().getItems()[0];
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
                System.err.println("SEARCH DISRUPTED: STATUS -1\nRETURNING TO: MAIN MENU");
                return -1;
            }catch(ArrayIndexOutOfBoundsException e){
                System.err.println("TRACK NOT FOUND: STATUS 1\nRETURNING TO: ADD TO QUEUE");
                return 1;
            }

            System.out.println(getSong(track)+" Was Found! Add it To Queue? (Enter 'y' to confirm)> ");
            char c; //Confirmation to add selected song to user queue
            try {
                c = (char) System.in.read();
            } catch (IOException e) {
                System.err.println("READ ERROR:");
                c = 'n';
            }
            if(c != 'y'){
                System.err.println("Add to Queue Canceled: STATUS 10");
                return 10;
            }

            String uri = track.getUri();
            try {
                spotifyApi.addItemToUsersPlaybackQueue(uri).device_id(device.getId()).build().execute(); //adds to queue
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("FAILED TO ADD \""+song+"\": STATUS -2\n RETURNING TO: MAIN MENU");
                return -2;
            }catch(SpotifyWebApiException e){
                System.err.println("AUTHORIZATION EXPIRATION: PLEASE RELAUNCH APP\nEXIT STATUS: -2");
                System.exit(-2);
            }
        }else{//Device is null
            System.err.println("DEVICE NOT AVAILABLE: STATUS -3\n RETURNING TO: MAIN MENU");
            return -3;
        }
        System.out.println( getSong(track)+" WAS ADDED SUCCESSFULLY");
        return 0;
    }

    public static String getSong(Track track){
        return "\""+track.getName()+"\" by: \""+track.getArtists()[0].getName()+"\"";
    }
}
