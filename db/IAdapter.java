package db;

import java.sql.Connection;
import java.sql.SQLException;

public interface IAdapter {
    Connection getConnection(String url, String user, String pass) throws SQLException;
}