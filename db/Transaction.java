package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Transaction {
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
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}