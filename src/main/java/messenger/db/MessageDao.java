package messenger.db;

import messenger.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MessageDao {

    private final Database database;


    public MessageDao(Database database) {


        this.database = database;
    }


    public Message saveRoomMessage(int senderId, int roomId, String text) {

        String sql = "INSERT INTO messages (sender_id, room_id, receiver_id, text) VALUES (?, ?, NULL, ?)";
        try (Connection conn = database.getConnection();


             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, senderId);
            ps.setInt(2, roomId);
            ps.setString(3, text);
            ps.executeUpdate();



            int id = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    id = keys.getInt(1);
                }

            }

            return new Message(id, senderId, roomId, null, text, null);


        } catch (SQLException e) {
            throw new RuntimeException("Error saving room message", e);
        }
    }



    public Message savePrivateMessage(int senderId, int receiverId, String text) {
        String sql = "INSERT INTO messages (sender_id, room_id, receiver_id, text) VALUES (?, NULL, ?, ?)";
        try (Connection conn = database.getConnection();


             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, text);
            ps.executeUpdate();

            int id = 0;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    id = keys.getInt(1);

                }
            }


            return new Message(id, senderId, null, receiverId, text, null);

        } catch (SQLException e) {
            throw new RuntimeException("Error saving private message", e);
        }
    }

    public List<Message> findRecentByRoom(int roomId, int limit) {
        String sql = "SELECT id, sender_id, room_id, receiver_id, text, sent_at " +
                "FROM messages WHERE room_id = ? ORDER BY id DESC LIMIT ?";


        List<Message> list = new ArrayList<>();

        try (Connection conn = database.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, limit);


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

            Collections.reverse(list);
            return list;


        } catch (SQLException e) {
            throw new RuntimeException("Error loading room history", e);
        }
    }


    public List<Message> findDirectHistory(int userA, int userB, int limit) {
        String sql = "SELECT id, sender_id, room_id, receiver_id, text, sent_at " +
                "FROM messages WHERE room_id IS NULL AND (" +
                "(sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)" +
                ") ORDER BY id DESC LIMIT ?";


        List<Message> list = new ArrayList<>();
        try (Connection conn = database.getConnection();


             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userA);
            ps.setInt(2, userB);
            ps.setInt(3, userB);
            ps.setInt(4, userA);
            ps.setInt(5, limit);


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }


            Collections.reverse(list);
            return list;


        } catch (SQLException e) {

            throw new RuntimeException("Error loading direct message history", e);
        }
    }


    private Message mapRow(ResultSet rs) throws SQLException {

        Timestamp ts = rs.getTimestamp("sent_at");
        int roomVal = rs.getInt("room_id");
        Integer roomId = rs.wasNull() ? null : roomVal;
        int recvVal = rs.getInt("receiver_id");
        Integer receiverId = rs.wasNull() ? null : recvVal;

        return new Message(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                roomId,
                receiverId,
                rs.getString("text"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}