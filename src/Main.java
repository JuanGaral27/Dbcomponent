import db.Dbcomponent; // Asegúrate que la 'C' sea mayúscula si renombraste el archivo
import db.PostgresAdapter;
import db.Transaction;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Creamos el adaptador primero
            PostgresAdapter adapter = new PostgresAdapter();

            // 2. Instanciamos el componente indicando la ruta del archivo como 7mo parámetro
            // He añadido "data/queries.json" (cámbialo por la ruta real de tu archivo)
            Dbcomponent<PostgresAdapter> db = new Dbcomponent<>(
                adapter, 
                "localhost", 
                5432, 
                "tienda", 
                "postgres", 
                "chakakugo7458",
                "data/queries.json" 
            );

            // 3. Ejecución de Query normal
            // Asegúrate que "get_productos" esté definido dentro de tu JSON o Properties
            db.query("get_productos");

            // 4. Uso de Transacción
            try (Transaction t = db.transaction()) {
                t.execute("insert_log", "Prueba de transaccion");
                t.commit();
                System.out.println("Transacción completada con éxito.");
            } catch (Exception e) {
                System.err.println("Error en la transacción: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error de conexión o configuración:");
            e.printStackTrace();
        }
    }
}