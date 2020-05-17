import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MusicPlayer {
    public final String IP = "192.168.0.51";
    public final int PORT = 55888;
    private DataOutputStream out; //Information this client sends
    private AudioInputStream in; //Information this client receives

    public MusicPlayer() throws IOException {
        Socket sock = null;
        sock = new Socket(IP,PORT);
        try {
            in = AudioSystem.getAudioInputStream(sock.getInputStream());
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(in);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        clip.start();

    }

    public static void main(String args[]) throws IOException {
        MusicPlayer mp = new MusicPlayer();
    }
}
