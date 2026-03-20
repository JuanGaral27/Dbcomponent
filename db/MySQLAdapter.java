package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLAdapter implements IAdapter {
    @Override
    public Connection getConnection(String url, String user, String pass) throws SQLException {
        try {
            // Carga del driver de MySQL (Connector/J)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado en el classpath", e);
        }
        return DriverManager.getConnection(url, user, pass);
    }
}