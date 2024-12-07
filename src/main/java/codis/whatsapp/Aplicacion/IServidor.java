package codis.whatsapp.Aplicacion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

public interface IServidor extends Remote {
    String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws Exception; //TODO todos lanzan más excepciones
    void registrarse(String nombre, String contrasena) throws Exception;
    void crear_solicitud(String solicitante, String solicitado) throws Exception;
    void aceptar_solicitud(String solicitante, String solicitao) throws Exception;
    void rechazar_solicitud(String solicitante, String solicitado) throws Exception;
    List<String> mostrar_solicitudes(String usuario) throws Exception;
    void borrar_amistad(String usuario, String amistad) throws Exception;
    List<Usuario> obtener_lista_amigos(Usuario usuario) throws RemoteException, SQLException;
    void cerrar_sesion(String nombre) throws Exception;
}
