package messenger.db;

import messenger.exception.UsernameTakenException;
import messenger.model.User;

import java.sql.*;
import java.util.Optional;


public class UserDao {
    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }


    public User create(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();



            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1)).orElseThrow();
                }
            }

            throw new SQLException("Failed to retrieve generated user id");


        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                throw new UsernameTakenException("Username already taken: " + username);
            }


            throw new RuntimeException("Error creating user", e);
        }
    }

    public Optional<User> findByUsername(String username) {

        String sql = "SELECT id, username, password, created_at FROM users WHERE username = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username", e);

        }
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, created_at FROM users WHERE id = ?";

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
            throw new RuntimeException("Error finding user by id", e);

        }
    }

    private User mapRow(ResultSet rs) throws SQLException {

        Timestamp ts = rs.getTimestamp("created_at");

        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}