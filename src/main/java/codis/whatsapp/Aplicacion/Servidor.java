package codis.whatsapp.Aplicacion;

import codis.whatsapp.BD.DAOUsuarios;
import codis.whatsapp.Aplicacion.Excepciones.*;

import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

public class Servidor extends UnicastRemoteObject implements IServidor {
    DAOUsuarios daoUsuarios;

    private final HashMap<String, Usuario> genteConectada;

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
        genteConectada=new HashMap<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws SQLException, ContrasenaErronea, FalloUsuario, RemoteException, MalformedURLException, NotBoundException {
        String codigo=daoUsuarios.iniciarSesion(nombre, contrasena);
        Usuario solicitante=new Usuario(nombre, (ICliente)  Naming.lookup("rmi://" + IP + ":" + puerto + "/"+nombre));
        genteConectada.put(nombre, solicitante);


        for(Usuario u : obtener_lista_amigos(solicitante, contrasena)){
            u.getORemoto().informarDeAmigoOnline(solicitante);
        }

        return codigo;
    }

    public void registrarse(String nombre, String contrasena) throws SQLException, FalloUsuario, RemoteException{
        daoUsuarios.registrarse(nombre, contrasena);
    }

    public void crear_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, RemoteException{
        daoUsuarios.crear_solicitud(solicitante, solicitado, contrasena);
    }

    public void aceptar_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud,ContrasenaErronea, RemoteException{
        daoUsuarios.aceptar_solicitud(solicitante, solicitado, contrasena);
    }

    public void rechazar_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, RemoteException{
        daoUsuarios.crear_solicitud(solicitante, solicitado, contrasena);
    }

    public List<String> mostrar_solicitudes(String usuario, String contrasena) throws SQLException, ContrasenaErronea, RemoteException{
        return daoUsuarios.mostrar_solicitudes(usuario, contrasena);
    }

    public void borrar_amistad(String usuario, String amistad, String contrasena) throws SQLException, FalloAmigo, ContrasenaErronea, RemoteException{
        daoUsuarios.borrar_amistad(usuario, amistad, contrasena);
    }

    public List<Usuario> obtener_lista_amigos(Usuario usuario, String contrasena) throws SQLException,ContrasenaErronea, RemoteException{
        List<Usuario> amigos=daoUsuarios.obtener_lista_amigos(usuario, contrasena);
        ListIterator<Usuario> i = amigos.listIterator();
        List<Usuario> res=new ArrayList<>();
        while(i.hasNext()){
            Usuario amigo = genteConectada.get(i.next().nombre);
            if(amigo!=null){
                res.add(amigo);
            }
        }
        return res;
    }

    public void cerrar_sesion(String nombre, String contrasena) throws SQLException, ContrasenaErronea, RemoteException{
        daoUsuarios.cerrar_sesion(nombre, contrasena);
        genteConectada.remove(nombre);
    }
}