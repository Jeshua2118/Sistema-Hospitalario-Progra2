
package hospitalswing.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
        "jdbc:mysql://localhost:3306/hospital_db";

    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public Connection conectar() throws SQLException {

        Connection conexion = DriverManager.getConnection(
            URL, USUARIO, PASSWORD
        );

        System.out.println("Conexion a MySQL exitosa.");

        return conexion;
    }
}
