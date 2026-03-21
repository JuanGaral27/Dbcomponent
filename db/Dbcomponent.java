package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Dbcomponent<T extends IAdapter> {
    private final T adapter;
    private final Properties queries = new Properties();

    // El constructor recibe los datos y establece la conexión inicial [cite: 15]
    public Dbcomponent(T adapter, String host, int port, String dbName, String user, String password) {
        this.adapter = adapter;
        this.adapter.connect(host, port, dbName, user, password);
        loadQueries();
    }

    // Carga las consultas desde el archivo properties (o .json/.yaml) [cite: 13, 14]
    private void loadQueries() {
        try (FileInputStream fis = new FileInputStream("data/queries.properties")) {
            queries.load(fis);
        } catch (IOException e) {
            System.err.println("No se pudo cargar data/queries.properties: " + e.getMessage());
        }
    }

    // Ejecuta una query predefinida solicitando y liberando conexión automáticamente [cite: 7, 10]
    public void query(String queryKey, Object... params) {
        String sql = queries.getProperty(queryKey);
        if (sql == null) {
            System.out.println("Error: La query '" + queryKey + "' no existe en la configuración.");
            return;
        }

        try (Connection conn = adapter.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Comienza una transacción devolviendo un objeto Transaction [cite: 7]
    public Transaction transaction() throws SQLException {
        return new Transaction(adapter.getConnection(), this.queries);
    }
}