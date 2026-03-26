package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLAdapter implements IAdapter {
    private String url;
    private String user;
    private String password;

    @Override
    public void connect(String host, int port, String dbName, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}