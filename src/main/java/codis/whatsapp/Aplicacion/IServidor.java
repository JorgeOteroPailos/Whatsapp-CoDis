package codis.whatsapp.Aplicacion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import codis.whatsapp.Aplicacion.Excepciones.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public interface IServidor extends Remote {
    String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws SQLException,ContrasenaErronea, FalloUsuario, RemoteException, MalformedURLException, NotBoundException;
    void registrarse(String nombre, String contrasena) throws SQLException, FalloUsuario, RemoteException;
    void crear_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, FalloAmigo, FalloUsuario, RemoteException;
    Usuario aceptar_solicitud(String solicitante, String solicitao, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, RemoteException; //TODO
    void rechazar_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, RemoteException;
    List<String> mostrar_solicitudes(String usuario, String contrasena) throws SQLException, ContrasenaErronea,RemoteException;
    void borrar_amistad(String usuario, String amistad, String contrasena) throws SQLException, FalloAmigo, ContrasenaErronea,RemoteException;
    List<Usuario> obtener_lista_amigos(Usuario usuario, String contrasena) throws SQLException, ContrasenaErronea,RemoteException;
    void cerrar_sesion(String nombre, String contrasena) throws SQLException, ContrasenaErronea, RemoteException;
}
