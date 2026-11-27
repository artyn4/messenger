package messenger.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;



public class Recipient {

    private final Socket socket;

    private final PrintWriter out;

    private Integer userId;
    private String username;

    private String currentRoom;



    public Recipient(Socket socket) throws IOException {

        this.socket = socket;

        this.out = new PrintWriter(

                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)),
                true

        );
    }


    public synchronized void send(String line) {
        out.println(line);
    }


    public Socket getSocket() {
        return socket;
    }


    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getCurrentRoom() {
        return currentRoom;
    }


    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }



    public boolean isAuthenticated() {
        return userId != null && username != null;

        
    }
}