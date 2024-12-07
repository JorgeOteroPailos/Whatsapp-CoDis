package codis.whatsapp.BD;

import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.Aplicacion.Excepciones.ContrasenaErronea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DAOUsuarios extends AbstractDAO {

    public DAOUsuarios(Connection conexion) {
        super.setConexion(conexion);
    }

    public String iniciarSesion(String nombre, String contrasena) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmUsuario = con.prepareStatement("select * from usuarios where nombre = ?");
             PreparedStatement stmContrasena = con.prepareStatement("select * from usuarios where nombre = ? and contrasena = ?")) {

            stmUsuario.setString(1, nombre);
            try (ResultSet rsUsuario = stmUsuario.executeQuery()) {
                if (rsUsuario.next()) {
                    stmContrasena.setString(1, nombre);
                    stmContrasena.setString(2, contrasena);
                    try (ResultSet rsContrasena = stmContrasena.executeQuery()) {
                        if (!rsContrasena.next()) {
                            throw new ContrasenaErronea("Contraseña errónea");
                        }
                    }
                } else {
                    throw new Exception("Usuario no registrado");
                }
            }
        }

        String posiblesCaracteres = "abcdefghijklmnñopqrstuvwxyzQWERTYUIOPASDFGHJKLÑZXCVBNM1234567890,.-+</*!·$%&()=?¿>;:_{}[]#";
        StringBuilder codigo = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            int indice = rand.nextInt(posiblesCaracteres.length());
            codigo.append(posiblesCaracteres.charAt(indice));
        }

        return codigo.toString();
    }

    public void registrarse(String nombre, String contrasena) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmUsuario1 = con.prepareStatement("select * from usuarios where nombre = ?");
             PreparedStatement stmUsuario2 = con.prepareStatement("insert into usuarios(nombre,contrasena) values(?,?)")) {

            stmUsuario1.setString(1, nombre);
            try (ResultSet rsUsuario = stmUsuario1.executeQuery()) {
                if (rsUsuario.next()) {
                    throw new Exception("Ya existe un usuario registrado con el susodicho nombre de usuario!");
                }
            }

            stmUsuario2.setString(1, nombre);
            stmUsuario2.setString(2, contrasena);
            stmUsuario2.executeUpdate();
        }
    }

    public void crear_solicitud(String solicitante, String solicitado) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmSolicitud = con.prepareStatement("select * from solicitudes where (solicitante = ? and solicitado = ?) or (solicitante = ? and solicitado = ?)");
             PreparedStatement stmInsertar = con.prepareStatement("insert into solicitudes(solicitante, solicitado) values(?,?)")) {

            stmSolicitud.setString(1, solicitante);
            stmSolicitud.setString(2, solicitado);
            stmSolicitud.setString(3, solicitado);
            stmSolicitud.setString(4, solicitante);

            try (ResultSet rsSolicitud = stmSolicitud.executeQuery()) {
                if (rsSolicitud.next()) {
                    throw new Exception("Solicitud ya existente.");
                }
            }

            stmInsertar.setString(1, solicitante);
            stmInsertar.setString(2, solicitado);
            stmInsertar.executeUpdate();
        }
    }

    public void aceptar_solicitud(String solicitante, String solicitado) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmSolicitud = con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
             PreparedStatement stmInsertar = con.prepareStatement("insert into amigos(amigo1, amigo2) values(?,?)");
             PreparedStatement stmEliminar = con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?")) {

            stmSolicitud.setString(1, solicitante);
            stmSolicitud.setString(2, solicitado);

            try (ResultSet rsSolicitud = stmSolicitud.executeQuery()) {
                if (rsSolicitud.next()) {
                    stmInsertar.setString(1, solicitante);
                    stmInsertar.setString(2, solicitado);
                    stmInsertar.executeUpdate();

                    stmEliminar.setString(1, solicitante);
                    stmEliminar.setString(2, solicitado);
                    stmEliminar.executeUpdate();
                } else {
                    throw new Exception("Solicitud no encontrada.");
                }
            }
        }
    }

    //TODO debería pedir la contraseña del solicitado
    public void rechazar_solicitud(String solicitante, String solicitado) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmSolicitud = con.prepareStatement("select * from solicitudes where solicitante = ? and solicitado = ?");
             PreparedStatement stmEliminar = con.prepareStatement("delete from solicitudes where solicitante = ? and solicitado = ?")) {

            stmSolicitud.setString(1, solicitante);
            stmSolicitud.setString(2, solicitado);

            try (ResultSet rsSolicitud = stmSolicitud.executeQuery()) {
                if (rsSolicitud.next()) {
                    stmEliminar.setString(1, solicitante);
                    stmEliminar.setString(2, solicitado);
                    stmEliminar.executeUpdate();
                } else {
                    throw new Exception("Solicitud no encontrada.");
                }
            }
        }
    }

    public ArrayList<String> mostrar_solicitudes(String usuario) throws Exception {
        ArrayList<String> resultado = new ArrayList<>();
        try (Connection con = this.getConexion();
             PreparedStatement stmSolicitud = con.prepareStatement("select * from solicitudes where solicitante = ? or solicitado = ?")) {

            stmSolicitud.setString(1, usuario);
            stmSolicitud.setString(2, usuario);

            try (ResultSet rsSolicitud = stmSolicitud.executeQuery()) {
                while (rsSolicitud.next()) {
                    if (rsSolicitud.getString("solicitante").equals(usuario)) {
                        resultado.add(rsSolicitud.getString("solicitado"));
                    } else {
                        resultado.add(rsSolicitud.getString("solicitante"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
        return resultado;
    }

    public void borrar_amistad(String usuario, String amigo) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmBusqueda = con.prepareStatement("select * from amigos where (amigo1 = ? and amigo2 = ?) or (amigo1=? and amigo2=?)");
             PreparedStatement stmEliminar = con.prepareStatement("delete from amigos where (amigo1 = ? and amigo2 = ?) or (amigo1=? and amigo2=?)")) {

            stmBusqueda.setString(1, usuario);
            stmBusqueda.setString(2, amigo);
            stmBusqueda.setString(3, amigo);
            stmBusqueda.setString(4, usuario);

            try (ResultSet rsBusqueda = stmBusqueda.executeQuery()) {
                if (rsBusqueda.next()) {
                    stmEliminar.setString(1, usuario);
                    stmEliminar.setString(2, amigo);
                    stmEliminar.setString(3, amigo);
                    stmEliminar.setString(4, usuario);
                    stmEliminar.executeUpdate();
                } else {
                    throw new Exception("Amistad no encontrada.");
                }
            }
        }
    }

    public List<Usuario> obtener_lista_amigos(Usuario usuario) throws SQLException {
        ArrayList<Usuario> resultado = new ArrayList<>();
        try (Connection con = this.getConexion();
             PreparedStatement stmAmigos = con.prepareStatement("select * from amigos where amigo1 = ? or amigo2 = ?")) {

            stmAmigos.setString(1, usuario.nombre);
            stmAmigos.setString(2, usuario.nombre);

            try (ResultSet rsAmigos = stmAmigos.executeQuery()) {
                while (rsAmigos.next()) {
                    if (rsAmigos.getString("amigo1").equals(usuario.nombre)) {
                        resultado.add(new Usuario(rsAmigos.getString("amigo2")));
                    } else {
                        resultado.add(new Usuario(rsAmigos.getString("amigo1")));
                    }
                }
            }
        }
        return resultado;
    }

    public void cerrar_sesion(String nombre) throws Exception {
        try (Connection con = this.getConexion();
             PreparedStatement stmCerrar = con.prepareStatement("update usuarios set online = false where nombre = ? and online = true")) {

            stmCerrar.setString(1, nombre);
            int filasActualizadas = stmCerrar.executeUpdate();

            if (filasActualizadas == 0) {
                throw new Exception("El usuario no está en línea.");
            }
        }
    }
}
