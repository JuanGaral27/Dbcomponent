package db;

import java.sql.Connection;
import java.sql.SQLException;

public interface IAdapter {
    void connect(String host, int port, String dbName, String user, String password);
    Connection getConnection() throws SQLException;
    void releaseConnection(Connection connection) throws SQLException;
}