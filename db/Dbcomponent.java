package db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Dbcomponent<T extends IAdapter> {
    private final T adapter;
    private final Properties queries = new Properties();

    /**
     * @param configPath Ruta del archivo (.properties o .json)
     */
    public Dbcomponent(T adapter, String host, int port, String dbName, String user, String password, String configPath) {
        this.adapter = adapter;
        this.adapter.connect(host, port, dbName, user, password);
        loadQueries(configPath);
    }

    private void loadQueries(String path) {
        try {
            if (path.endsWith(".properties")) {
                try (FileInputStream fis = new FileInputStream(path)) {
                    queries.load(fis);
                    System.out.println("Consultas cargadas desde Properties: " + path);
                }
            } else if (path.endsWith(".json")) {
                loadFromJsonNativo(path);
                System.out.println("Consultas cargadas desde JSON: " + path);
            } else {
                System.err.println("Formato no soportado. Use .json o .properties");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de configuración: " + e.getMessage());
        }
    }

    // Método para procesar JSON simple sin librerías externas
    private void loadFromJsonNativo(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
        }

        // Limpiamos los brackets iniciales y finales
        String content = sb.toString();
        content = content.substring(content.indexOf("{") + 1, content.lastIndexOf("}"));

        // Dividimos por comas, pero considerando que las consultas SQL pueden tener comas internas
        // Un split simple por "," fallaría si la query tiene comas. 
        // Esta es una versión mejorada para pares "llave": "valor"
        String[] pairs = content.split("\","); 

        for (String pair : pairs) {
            String[] keyValue = pair.split("\":");
            if (keyValue.length == 2) {
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();
                // Si el valor terminaba con la comilla del split, se la quitamos
                if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
                
                queries.put(key, value);
            }
        }
    }

    public void query(String queryKey, Object... params) {
        String sql = queries.getProperty(queryKey);
        if (sql == null) {
            System.err.println("Error: La query '" + queryKey + "' no existe.");
            return;
        }

        try (Connection conn = adapter.getConnection()) {
            if (conn == null) {
                System.err.println("Error: No hay conexión a la base de datos.");
                return;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.execute();
            }
        } catch (SQLException e) {
            System.err.println("Error en SQL (" + queryKey + "): " + e.getMessage());
        }
    }

    public Transaction transaction() throws SQLException {
        return new Transaction(adapter.getConnection(), this.queries);
    }
}