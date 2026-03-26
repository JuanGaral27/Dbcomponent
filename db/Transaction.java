package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

// Añadimos implements AutoCloseable para que el try-with-resources funcione
public class Transaction implements AutoCloseable {
    private final Connection connection;
    private final Properties queries;

    public Transaction(Connection connection, Properties queries) throws SQLException {
        this.connection = connection;
        this.queries = queries;
        this.connection.setAutoCommit(false); // Iniciamos la transacción 
    }

    public void execute(String queryKey, Object... params) throws SQLException {
        String sql = queries.getProperty(queryKey);
        if (sql == null) throw new SQLException("Query no definida: " + queryKey);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        }
    }

    public void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public void rollback() {
        try {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Este es el método que pide AutoCloseable
    @Override
    public void close() throws SQLException {
        if (connection != null) {
            // Es buena práctica asegurarse de que el autoCommit vuelva a true
            // antes de cerrar o devolver la conexión al pool
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
            connection.close();
        }
    }
}