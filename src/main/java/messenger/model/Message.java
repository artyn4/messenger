package messenger.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int senderId;
    private Integer roomId;
    private Integer receiverId;
    private String text;
    private LocalDateTime sentAt;

    public Message() {

    }
    public Message(int id, int senderId, Integer roomId, Integer receiverId, String text, LocalDateTime sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.roomId =  roomId;
        this.receiverId = receiverId;
        this.text = text;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId){
        this.receiverId = receiverId;
    }
}