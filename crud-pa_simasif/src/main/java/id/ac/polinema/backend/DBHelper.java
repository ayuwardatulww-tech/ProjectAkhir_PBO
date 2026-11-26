package id.ac.polinema.backend;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBHelper {
    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        Properties props = new Properties();
        try (InputStream in = DBHelper.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "config.properties tidak ditemukan. Salin dari config.properties.example");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca config.properties", e);
        }
        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASS = props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void isiParameter(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public static int insertQueryGetId(String sql, Object... params) {
        try (var c = getConnection();
             var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            isiParameter(ps, params);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean executeQuery(String sql, Object... params) {
        try (var c = getConnection();
             var ps = c.prepareStatement(sql)) {
            isiParameter(ps, params);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}