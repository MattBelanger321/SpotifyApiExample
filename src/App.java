import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class App {
    public SpotifyApi spotifyApi;
    private final String device_name = "LAPTOP-42MARVS2";
    public App(SpotifyApi spotifyApi){
        this.spotifyApi = spotifyApi;
    }

    public void addToQueue(String song) throws ParseException, SpotifyWebApiException, IOException {
        //Creates SearchRequest
        SearchTracksRequest str = spotifyApi.searchTracks(song)
                .limit(1)
                .build();

        Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();

        String device_id = null;
        for(Device d: devices){
            if(device_name.equals(d.getName()))
                device_id = d.getId();
        }
        if(device_id != null){
            //Add track
            Track track = str.execute().getItems()[0];
            assert track != null;
            String uri = track.getUri();
            spotifyApi.addItemToUsersPlaybackQueue(uri).device_id(device_id).build().execute();
        }else{
            System.err.println("Decice not avalible");
        }
    }
}
