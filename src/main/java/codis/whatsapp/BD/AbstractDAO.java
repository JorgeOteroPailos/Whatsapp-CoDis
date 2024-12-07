/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package codis.whatsapp.BD;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author basesdatos
 */
public abstract class AbstractDAO {
    private java.sql.Connection conexion;

    protected java.sql.Connection getConexion() throws SQLException {

        if (this.conexion == null || this.conexion.isClosed()) {
            // Reestablecer la conexión
            this.conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Ickki2p2??");
        }
        return this.conexion;
    }

    protected void setConexion(java.sql.Connection conexion){
        this.conexion=conexion;
    }
}