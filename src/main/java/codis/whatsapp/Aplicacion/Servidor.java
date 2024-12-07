package codis.whatsapp.Aplicacion;

import codis.whatsapp.BD.DAOUsuarios;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Servidor extends UnicastRemoteObject implements IServidor {
    DAOUsuarios daoUsuarios;

    private final List<ICliente> genteConectada;

    Connection conn;
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final String nombre;

    public Servidor(String nombre) throws RemoteException {
        super();
        this.nombre=nombre;
        try {
            // Conexión a la base de datos
            conn=DriverManager.getConnection(URL, "postgres", "Ickki2p2??");
            System.out.println("Conexión exitosa a la base de datos.");

        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            java.lang.System.exit(-1);
        }
        daoUsuarios=new DAOUsuarios(conn);
        genteConectada=new LinkedList<>();
    }

    public String getNombre() {
        return nombre;
    }

    //TODO quitar los catch->throw triviales (avisa el compilador)

    public String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws Exception {
        try {
            String codigo=daoUsuarios.iniciarSesion(nombre, contrasena);
            genteConectada.add((ICliente)  Naming.lookup("rmi://" + IP + ":" + puerto + "/"+nombre));

            return codigo;
        }catch(Exception e){
            throw e;
        }
    }

    public void registrarse(String nombre, String contrasena) throws Exception {
        try {
            daoUsuarios.registrarse(nombre, contrasena);
        }catch(Exception e){
            throw e;
        }
    }

    //TODO todos los try catch y modificación aplicada al arraylist de usuarios conectados

    public void crear_solicitud(String solicitante, String solicitado) throws Exception {
        try {
            daoUsuarios.crear_solicitud(solicitante, solicitado);
        }catch(Exception e){
            throw e;
        }
    }

    public void aceptar_solicitud(String solicitante, String solicitado) throws Exception {
        try {
            daoUsuarios.aceptar_solicitud(solicitante, solicitado);
        }catch(Exception e){
            throw e;
        }
    }

    public void rechazar_solicitud(String solicitante, String solicitado) throws Exception {
        try {
            daoUsuarios.crear_solicitud(solicitante, solicitado);
        }catch(Exception e){
            throw e;
        }
    }

    public List<String> mostrar_solicitudes(String usuario) throws RemoteException {
        return daoUsuarios.mostrar_solicitudes(usuario);
    }

    public void borrar_amistad(String usuario, String amistad) throws Exception {
        try {
            daoUsuarios.borrar_amistad(usuario, amistad);
        }catch(Exception e){
            throw e;
        }
    }

    public List<Usuario> obtener_lista_amigos(Usuario usuario) throws RemoteException {
        return daoUsuarios.obtener_lista_amigos(usuario);
    }

    public void cerrar_sesion(String nombre) throws Exception {
        try {
            daoUsuarios.cerrar_sesion(nombre);
        }catch(Exception e){
            throw e;
        }
    }
}