
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */
package codis.whatsapp.BD;

import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.Aplicacion.Excepciones.*;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;

    public class DAOUsuarios extends AbstractDAO {

        public DAOUsuarios(Connection conexion) {
            super.setConexion(conexion);
        }

        public String iniciarSesion(String nombre, String contrasena) throws ContrasenaErronea, FalloUsuario, SQLException{
            Connection con;
            PreparedStatement stmUsuario;
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
                    throw new FalloUsuario("Usuario no registrado");
                }
            }catch (SQLException e){
                System.err.println(e.getMessage());
                throw e;
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

        public void registrarse(String nombre, String contrasena) throws FalloUsuario, SQLException {
            try (Connection con = this.getConexion();
                 PreparedStatement stmUsuario1 = con.prepareStatement("select * from usuarios where nombre = ?");
                 PreparedStatement stmUsuario2 = con.prepareStatement("insert into usuarios(nombre,contrasena) values(?,?)")) {

                // Verificar si el usuario ya existe
                stmUsuario1.setString(1, nombre);
                try (ResultSet rsUsuario = stmUsuario1.executeQuery()) {
                    if (rsUsuario.next()) {
                        throw new FalloUsuario("Ya existe un usuario registrado con el susodicho nombre de usuario!");
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


        public void crear_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, ContrasenaErronea, FalloSolicitud, FalloAmigo, FalloUsuario {
            String verificarUsuario = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?";
            String verificarSolicitado = "SELECT * FROM usuarios WHERE nombre = ?";
            String verificarAmigos = "SELECT * FROM amigos WHERE (amigo1 = ? AND amigo2 = ?) OR (amigo1 = ? AND amigo2 = ?)";
            String verificarSolicitudes = "SELECT * FROM solicitudes WHERE (solicitante = ? AND solicitado = ?) OR (solicitante = ? AND solicitado = ?)";
            String insertarSolicitud = "INSERT INTO solicitudes (solicitante, solicitado) VALUES (?, ?)";

            try (Connection con = this.getConexion();
                 PreparedStatement stmUsuario = con.prepareStatement(verificarUsuario)) {

                // Verificar que el solicitante existe y su contraseña es correcta
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, contrasena);

                try (ResultSet rsUsuario = stmUsuario.executeQuery()) {
                    if (!rsUsuario.next()) {
                        throw new ContrasenaErronea("Contraseña errónea");
                    }
                }

                try(PreparedStatement stmSolicitado = con.prepareStatement(verificarSolicitado)) {
                    // Verificar que el solicitante existe y su contraseña es correcta
                    stmSolicitado.setString(1, solicitado);

                    try (ResultSet rsUsuario = stmSolicitado.executeQuery()) {
                        if (!rsUsuario.next()) {
                            throw new FalloUsuario("El usuario no existe en la base de datos");
                        }
                    }
                }


                // Verificar si ya son amigos
                try (PreparedStatement stmAmigos = con.prepareStatement(verificarAmigos)) {
                    stmAmigos.setString(1, solicitante);
                    stmAmigos.setString(2, solicitado);
                    stmAmigos.setString(3, solicitado);
                    stmAmigos.setString(4, solicitante);

                    try (ResultSet rsAmigos = stmAmigos.executeQuery()) {
                        if (rsAmigos.next()) {
                            throw new FalloAmigo("Ya eres amigo de este usuario");
                        }
                    }
                }

                // Verificar si ya existe una solicitud
                try (PreparedStatement stmSolicitudes = con.prepareStatement(verificarSolicitudes)) {
                    stmSolicitudes.setString(1, solicitante);
                    stmSolicitudes.setString(2, solicitado);
                    stmSolicitudes.setString(3, solicitado);
                    stmSolicitudes.setString(4, solicitante);

                    try (ResultSet rsSolicitudes = stmSolicitudes.executeQuery()) {
                        if (rsSolicitudes.next()) {
                            throw new FalloSolicitud("Esta solicitud ya existe");
                        }
                    }
                }

                // Insertar la nueva solicitud
                try (PreparedStatement stmInsertar = con.prepareStatement(insertarSolicitud)) {
                    stmInsertar.setString(1, solicitante);
                    stmInsertar.setString(2, solicitado);
                    stmInsertar.executeUpdate();
                }

            } catch (SQLException e) {
                System.err.println("Error en crear_solicitud: " + e.getMessage());
                throw e;
            }
        }

        public void aceptar_solicitud(String solicitante, String solicitado, String contrasena) throws FalloSolicitud, ContrasenaErronea, SQLException{
            Connection con;
            PreparedStatement stmUsuario;
            ResultSet rsUsuario;

            con = this.getConexion();
            try{
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, solicitado);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }
                stmUsuario=con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                rsUsuario=stmUsuario.executeQuery();
                if (rsUsuario.next()) {
                    stmUsuario=con.prepareStatement("insert into amigos(amigo1, amigo2) values(?,?)");
                    stmUsuario.setString(1, solicitante);
                    stmUsuario.setString(2, solicitado);
                    stmUsuario.executeUpdate();
                    stmUsuario=con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?");
                    stmUsuario.setString(1, solicitante);
                    stmUsuario.setString(2, solicitado);
                    stmUsuario.executeUpdate();
                }else{
                    throw new FalloSolicitud("No existe la solicitud que estás intentando aceptar");
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }

        public void rechazar_solicitud(String solicitante, String solicitado, String contrasena) throws FalloSolicitud, ContrasenaErronea, SQLException{
            Connection con;
            PreparedStatement stmUsuario;
            ResultSet rsUsuario;

            con = this.getConexion();
            try{
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, solicitado);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }
                stmUsuario=con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
                stmUsuario.setString(1, solicitante);
                stmUsuario.setString(2, solicitado);
                rsUsuario=stmUsuario.executeQuery();
                if (rsUsuario.next()) {
                    stmUsuario=con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?");
                    stmUsuario.setString(1, solicitante);
                    stmUsuario.setString(2, solicitado);
                    stmUsuario.executeUpdate();
                }else{
                    throw new FalloSolicitud("No existe la solicitud que estás intentando aceptar");
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }

        public ArrayList<String> mostrar_solicitudes(String usuario, String contrasena) throws ContrasenaErronea,SQLException{
            Connection con;
            PreparedStatement stmUsuario;
            ResultSet rsUsuario;
            ArrayList<String> resultado=new ArrayList<>();

            con = this.getConexion();
            try{
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, usuario);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }

                stmUsuario=con.prepareStatement("select * from solicitudes where solicitado = ?");
                stmUsuario.setString(1, usuario);
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
                throw e;
            }
            return resultado;
        }

        public void borrar_amistad(String usuario, String amigo, String contrasena) throws FalloAmigo,ContrasenaErronea, SQLException{
            Connection con;
            PreparedStatement stmUsuario;
            ResultSet rsUsuario;

            con = this.getConexion();
            try{
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, usuario);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }
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
                    stmUsuario.executeUpdate();
                }else{
                    throw new FalloAmigo("El usuario que se está intentando eliminar de amigos no pertenece a la lista de amistades");
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }

        public ArrayList<Usuario> obtener_lista_amigos(Usuario usuario, String contrasena) throws SQLException, ContrasenaErronea, RemoteException {

            debugPrint("entrando a ObtenerListaAmigos con la contra "+contrasena);

            ArrayList<Usuario> resultado = new java.util.ArrayList<>();
            Usuario usuarioActual;
            Connection con;
            PreparedStatement stmUsuarios;
            ResultSet rsUsuario;

            con = this.getConexion();

            String consulta = "select * from amigos where amigo1 = ? or amigo2 = ? ";

            try {
                stmUsuarios=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuarios.setString(1, usuario.nombre);
                stmUsuarios.setString(2, contrasena);
                rsUsuario=stmUsuarios.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }

                stmUsuarios = con.prepareStatement(consulta);
                stmUsuarios.setString(1, usuario.nombre);
                stmUsuarios.setString(2, usuario.nombre);

                rsUsuario = stmUsuarios.executeQuery();
                while (rsUsuario.next()) {
                    if (rsUsuario.getString("amigo1").equals(usuario.nombre)) {
                        usuarioActual = new Usuario(rsUsuario.getString("amigo2"));
                    } else {
                        usuarioActual = new Usuario(rsUsuario.getString("amigo1"));
                    }
                    resultado.add(usuarioActual);
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
            return resultado;
        }

        public void cerrar_sesion(String nombre, String contrasena) throws SQLException, ContrasenaErronea{
            Connection con;
            PreparedStatement stmUsuario;
            ResultSet rsUsuario;

            con = this.getConexion();
            try{
                stmUsuario=con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?");
                stmUsuario.setString(1, nombre);
                stmUsuario.setString(2, contrasena);
                rsUsuario=stmUsuario.executeQuery();
                if(!rsUsuario.next()){
                    throw new ContrasenaErronea("Contraseña errónea");
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }