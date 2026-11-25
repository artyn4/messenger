package messenger.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/messenger?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = " //// ";

    private final String url;
    private final String user;
    private final String password;

    public Database() {
        Properties fileProps = loadProperties();
        this.url = System.getProperty("db.url",
                System.getenv().getOrDefault("DB_URL", fileProps.getProperty("db.url", DEFAULT_URL)));
        this.user = System.getProperty("db.user",
                System.getenv().getOrDefault("DB_USER", fileProps.getProperty("db.user", DEFAULT_USER)));
        this.password = System.getProperty("db.password",
                System.getenv().getOrDefault("DB_PASSWORD", fileProps.getProperty("db.password", DEFAULT_PASSWORD)));
    }

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password != null ? password : "";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static Properties loadProperties() {

        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);

        } catch (IOException ignored) {
        }

        return props;
    }
}