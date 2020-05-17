import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MusicSender {
    //Server Fields
    private static final int PORT = 55888;
    private  DataOutputStream dos;
    private ServerSocket serverSocket;

    //Methods
    public MusicSender() throws IOException {
        File file = new File("song.mp3");
        byte[] b = Files.readAllBytes(Paths.get("song.mp3"));
        try{
            serverSocket = new ServerSocket(PORT);
            while(true){
                System.out.println("Awaiting Connection...");
                Socket sock = serverSocket.accept();
                System.out.println("Connected");
                dos = new DataOutputStream(sock.getOutputStream());
                dos.write(b,0, (int) file.length());
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new MusicSender();
    }
}
