import db.Dbcomponent;
import db.PostgresAdapter;
import db.Transaction;

public class Main {
    public static void main(String[] args) {
        try {
            Dbcomponent<PostgresAdapter> db = new Dbcomponent<>(
                new PostgresAdapter(), 
                "localhost", 5432, "tienda", "postgres", "chakakugo7458"
            );

            // Query normal
            db.query("get_productos");

            // Transacción
            Transaction t = db.transaction();
            t.execute("insert_log", "Prueba de transaccion");
            t.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}