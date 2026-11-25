package messenger.db;

import messenger.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RoomDao {
    private final Database database;

    public RoomDao(Database database) {
        this.database = database;
    }


    public Room createIfAbsent(String name) {
        Optional<Room> existing = findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        String sql = "INSERT IGNORE INTO rooms (name) VALUES (?)";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();

            return findByName(name).orElseThrow();


        } catch (SQLException e) {
            throw new RuntimeException("Error creating room: " + name, e);
        }
    }



    public Optional<Room> findByName(String name) {


        String sql = "SELECT id, name, created_at FROM rooms WHERE name = ?";
        try (Connection conn = database.getConnection();


             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);


            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error finding room by name", e);
        }
    }



    public Optional<Room> findById(int id) {
        String sql = "SELECT id, name, created_at FROM rooms WHERE id = ?";

        try (Connection conn = database.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);


            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error finding room by id", e);
        }
    }

    public List<Room> findAll() {


        String sql = "SELECT id, name, created_at FROM rooms ORDER BY name ASC";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                rooms.add(mapRow(rs));
            }

            return rooms;


        } catch (SQLException e) {
            throw new RuntimeException("Error listing rooms", e);

        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {


        Timestamp ts = rs.getTimestamp("created_at");

        return new Room(


                rs.getInt("id"),
                rs.getString("name"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}