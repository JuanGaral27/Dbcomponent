package db;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Properties;
import java.io.FileInputStream;

public class Dbcomponent<T extends IAdapter> {
    // El set de conexiones que se van a reciclar
    private final ArrayBlockingQueue<Connection> pool;
    private final Properties internalQueries = new Properties();

    // El constructor recibe los datos directamente (Punto 15)
    public Dbcomponent(T adapter, String url, String user, String pass, int poolSize, String queriesFile) throws Exception {
        this.pool = new ArrayBlockingQueue<>(poolSize);
        
        // Llenar el pool inicial (Punto 10)
        for (int i = 0; i < poolSize; i++) {
            pool.add(adapter.getConnection(url, user, pass));
        }

        // Importar lista de queries desde archivo .properties (Punto 13, 14)
        try (FileInputStream fis = new FileInputStream(queriesFile)) {
            internalQueries.load(fis);
        }
    }

    public ResultSet query(String key, Object... params) throws Exception {
        // SOLICITAR: Se saca una conexión del set para usarla
        Connection conn = pool.take(); 
        
        try {
            String sql = internalQueries.getProperty(key);
            if (sql == null) throw new SQLException("La clave de query no existe: " + key);

            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } finally {
            // DEVOLVER: La conexión regresa al pool para ser reciclada (Punto 10)
            pool.offer(conn);
        }
    }

    public void transaction() {
        // Punto 7 del esquema base
        System.out.println("Iniciando bloque de transacción...");
    }
}