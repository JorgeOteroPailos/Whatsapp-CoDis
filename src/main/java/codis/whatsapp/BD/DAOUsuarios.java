/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codis.whatsapp.BD;

import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.Aplicacion.Excepciones.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class DAOUsuarios extends AbstractDAO {

    //TODO muchos try-catch se deberían cambiar por try-with resources para que no de warning

    public DAOUsuarios(Connection conexion) {
        super.setConexion(conexion);
    }

    public String iniciarSesion(String nombre, String contrasena) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from usuarios where nombre = ?");
            stmUsuario.setString(1, nombre);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, nombre);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }
            }else{
                throw new Exception("Usuario no registrado");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }

        String posiblesCaracteres="abcdefghijklmnñopqrstuvwxyzQWERTYUIOPASDFGHJKLÑZXCVBNM1234567890,.-+</*!·$%&()=?¿>;:_{}[]#";
        StringBuilder codigo= new StringBuilder();
        Random rand = new Random();

        for(int i=0;i<10;i++){
            int indice=rand.nextInt(posiblesCaracteres.length());
            codigo.append(posiblesCaracteres.charAt(indice));
        }

        return codigo.toString();
    }

    public void registrarse(String nombre, String contrasena) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmUsuario1 = con.prepareStatement("select * from usuarios where nombre = ?");
             PreparedStatement stmUsuario2 = con.prepareStatement("insert into usuarios(nombre,contrasena) values(?,?)")) {

            // Verificar si el usuario ya existe
            stmUsuario1.setString(1, nombre);
            try (ResultSet rsUsuario = stmUsuario1.executeQuery()) {
                if (rsUsuario.next()) {
                    throw new Exception("Ya existe un usuario registrado con el susodicho nombre de usuario!");
                }
            }

            // Insertar nuevo usuario
            stmUsuario2.setString(1, nombre);
            stmUsuario2.setString(2, contrasena);
            stmUsuario2.executeUpdate(); // Cambiado de `executeQuery` a `executeUpdate`
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            throw e;
        }
    }


    public void crear_solicitud(String solicitante, String solicitado) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from solicitudes where (solicitante = ? and solicitado = ?) or (solicitante = ? and solicitado = ?)");
            stmUsuario.setString(1, solicitante);
            stmUsuario.setString(2, solicitado);
            stmUsuario.setString(3, solicitado);
            stmUsuario.setString(4, solicitante);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                //TODO añade un mensaje guarro que esto luego lo debuggea peter
                throw new Exception();
            }else{
                stmUsuario=con.prepareStatement("insert into solicitudes(solicitante, solicitado) values(?,?)");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                stmUsuario.executeQuery();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }

    public void aceptar_solicitud(String solicitante, String solicitado) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
            stmUsuario.setString(1, solicitante);
            stmUsuario.setString(2, solicitado);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                stmUsuario=con.prepareStatement("insert into amigos(amigo1, amigo2) values(?,?)");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                stmUsuario.executeQuery();
                stmUsuario=con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                stmUsuario.executeQuery();
            }else{
                //TODO añade un mensaje guarro que esto luego lo debuggea peter
                throw new Exception();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }

    public void rechazar_solicitud(String solicitante, String solicitado) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
            stmUsuario.setString(1, solicitante);
            stmUsuario.setString(2, solicitado);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                stmUsuario=con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                stmUsuario.executeQuery();
            }else{
                //TODO añade un mensaje guarro que esto luego lo debuggea peter
                throw new Exception();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }

    public ArrayList<String> mostrar_solicitudes(String usuario){
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;
        ArrayList<String> resultado=new ArrayList<>();

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from solicitudes where solicitante = ? or solicitado = ?");
            stmUsuario.setString(1, usuario);
            stmUsuario.setString(2, usuario);
            rsUsuario=stmUsuario.executeQuery();
            while (rsUsuario.next()) {
                if(rsUsuario.getString("solicitante").equals(usuario)){
                    resultado.add(rsUsuario.getString("solicitado"));
                }else{
                    resultado.add(rsUsuario.getString("solicitante"));
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
        return resultado;
    }

    public void borrar_amistad(String usuario, String amigo) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from amigos where (amigo1 = ? and amigo2 = ?) or (amigo1=? and amigo2=?)");
            stmUsuario.setString(1, usuario);
            stmUsuario.setString(2, amigo);
            stmUsuario.setString(3, amigo);
            stmUsuario.setString(4, usuario);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                stmUsuario=con.prepareStatement("delete from amigos where (amigo1 = ? and amigo2 = ?) or (amigo1=? and amigo2=?)");
                stmUsuario.setString(1, usuario);
                stmUsuario.setString(2, amigo);
                stmUsuario.setString(3, amigo);
                stmUsuario.setString(4, usuario);
                stmUsuario.executeQuery();
            }else{
                //TODO añade un mensaje guarro que esto luego lo debuggea peter
                throw new Exception();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }

    public ArrayList<Usuario> obtener_lista_amigos(Usuario usuario) {

        ArrayList<Usuario> resultado = new java.util.ArrayList<>();
        Usuario usuarioActual;
        Connection con;
        PreparedStatement stmUsuarios = null;
        ResultSet rsUsuario;

        con = this.getConexion();

        String consulta = "select * from amigos where amigo1 = ? or amigo2 = ? ";

        try {
            stmUsuarios = con.prepareStatement(consulta);
            stmUsuarios.setString(1, usuario.nombre);
            stmUsuarios.setString(1, usuario.nombre);

            rsUsuario = stmUsuarios.executeQuery();
            while (rsUsuario.next()) {
                if(rsUsuario.getString("amigo1").equals(usuario.nombre)){
                    usuarioActual = new Usuario(rsUsuario.getString("amigo2"));
                }else{
                    usuarioActual = new Usuario(rsUsuario.getString("amigo1"));
                }
                resultado.add(usuarioActual);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuarios.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
        return resultado;
    }

    public void cerrar_sesion(String nombre) throws Exception{
        Connection con;
        PreparedStatement stmUsuario = null;
        ResultSet rsUsuario;

        con = this.getConexion();
        try{
            stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and online = true");
            stmUsuario.setString(1, nombre);
            rsUsuario=stmUsuario.executeQuery();
            if (rsUsuario.next()) {
                stmUsuario = con.prepareStatement("update usuarios set online = false where nombre = ?");
                stmUsuario.setString(1, nombre);
                stmUsuario.executeUpdate();
            }else{
                //TODO añade un mensaje guarro que esto luego lo debuggea peter
                throw new Exception();
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                stmUsuario.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }
}
