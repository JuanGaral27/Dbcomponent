import db.Dbcomponent;
import db.PostgresAdapter;
import java.sql.ResultSet;

public class Main {
    // El método debe ser 'public static void main' para ser el punto de entrada.
    // Si no es static, Java lo confunde con un constructor de la clase 'main'.
    public static void main(String[] args) {
        try {
            // 1. Instanciar el Adapter (Desacoplamiento) [cite: 11, 12]
            PostgresAdapter adapter = new PostgresAdapter();

            // 2. Datos de conexión directos al constructor (Punto 15) [cite: 15]
            // Nota: Ajusta los datos a tu base de datos local
            String url = "jdbc:postgresql://localhost:5432/tienda";
            String user = "postgres";
            String pass = "chakakugo7458";

            // 3. Crear el componente con pool interno [cite: 5, 10]
            Dbcomponent<PostgresAdapter> db = new Dbcomponent<>(
                adapter, 
                url, 
                user, 
                pass, 
                10, // Tamaño del pool
                "data/queries.properties" // Archivo de queries predefinidas [cite: 13, 14]
            );

            System.out.println("Componente DB iniciado con éxito.");

            // 4. Ejecutar una query de la lista interna (Punto 7 y 13) [cite: 7, 13]
            // 'obtener_todos' debe existir en tu archivo queries.properties
            ResultSet rs = db.query("obtener_todos");
            
            while (rs.next()) {
                // Ejemplo: imprimir la primera columna
                System.out.println("Registro encontrado: " + rs.getString(1));
            }

        } catch (Exception e) {
            System.err.println("Error en la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
}