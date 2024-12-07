package codis.whatsapp.Aplicacion;

import codis.whatsapp.BD.DAOUsuarios;

import java.rmi.Naming;
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

    public String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws Exception {
        String codigo=daoUsuarios.iniciarSesion(nombre, contrasena);
        genteConectada.put(nombre, new Usuario(nombre, (ICliente)  Naming.lookup("rmi://" + IP + ":" + puerto + "/"+nombre)));

        return codigo;
    }

    public void registrarse(String nombre, String contrasena) throws Exception {
        daoUsuarios.registrarse(nombre, contrasena);
    }

    //TODO todos los try catch y modificación aplicada al arraylist de usuarios conectados

    public void crear_solicitud(String solicitante, String solicitado) throws Exception {
        daoUsuarios.crear_solicitud(solicitante, solicitado);
    }

    public void aceptar_solicitud(String solicitante, String solicitado) throws Exception {
        daoUsuarios.aceptar_solicitud(solicitante, solicitado);
    }

    public void rechazar_solicitud(String solicitante, String solicitado) throws Exception {
        daoUsuarios.crear_solicitud(solicitante, solicitado);
    }

    public List<String> mostrar_solicitudes(String usuario) throws Exception {
        return daoUsuarios.mostrar_solicitudes(usuario);
    }

    public void borrar_amistad(String usuario, String amistad) throws Exception {
        daoUsuarios.borrar_amistad(usuario, amistad);
    }

    public List<Usuario> obtener_lista_amigos(Usuario usuario) throws RemoteException, SQLException {
        List<Usuario> amigos=daoUsuarios.obtener_lista_amigos(usuario);
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

    public void cerrar_sesion(String nombre) throws Exception {
        daoUsuarios.cerrar_sesion(nombre);
    }
}