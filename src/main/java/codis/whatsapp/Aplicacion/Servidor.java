package codis.whatsapp.Aplicacion;

import codis.whatsapp.Aplicacion.Excepciones.ContrasenaErronea;
import codis.whatsapp.Aplicacion.Excepciones.FalloAmigo;
import codis.whatsapp.Aplicacion.Excepciones.FalloSolicitud;
import codis.whatsapp.Aplicacion.Excepciones.FalloUsuario;
import codis.whatsapp.BD.DAOUsuarios;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;

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


    public static String hashearContrasena(String texto) {
        return String.valueOf(texto.hashCode());
    }

    public String iniciar_sesion(String IP, int puerto, String nombre, String contrasena) throws SQLException, ContrasenaErronea, FalloUsuario, RemoteException, MalformedURLException, NotBoundException {
        String nuevaContrasena=hashearContrasena(contrasena);

        debugPrint("ENTRAMOS A INICIARSESION con el nombre "+nombre+" y la contra "+contrasena);
        String codigo=daoUsuarios.iniciarSesion(nombre, nuevaContrasena);
        Usuario solicitante=new Usuario(nombre, (ICliente)  Naming.lookup("rmi://" + IP + ":" + puerto + "/"+nombre));
        solicitante.setCodigoSesion(codigo);
        genteConectada.put(nombre, solicitante);



        for(Usuario u : obtener_lista_amigos(solicitante, contrasena)){
            debugPrint("ENTRAMOS A OBTENERLISTAAMIGOS con el nombre "+nombre+" y la contra "+contrasena);
            u.getORemoto().informarDeAmigoOnline(solicitante);
        }

        return codigo;
    }

    public void registrarse(String nombre, String contrasena) throws SQLException, FalloUsuario, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        debugPrint("Almacenando " + contrasena + "como contraseña");
        daoUsuarios.registrarse(nombre, nuevaContrasena);
    }

    public void crear_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, FalloAmigo, FalloUsuario, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        if(solicitante.equals(solicitado)){throw new FalloAmigo("No puedes agregarte a ti mismo");}
        daoUsuarios.crear_solicitud(solicitante, solicitado, nuevaContrasena);
    }

    public Usuario aceptar_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud,ContrasenaErronea, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        daoUsuarios.aceptar_solicitud(solicitante, solicitado, nuevaContrasena);
        Usuario usolicitante= genteConectada.get(solicitante);
        usolicitante.getORemoto().informarDeAmigoOnline(genteConectada.get(solicitado));
        return usolicitante;
    }

    public void rechazar_solicitud(String solicitante, String solicitado, String contrasena) throws SQLException, FalloSolicitud, ContrasenaErronea, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        daoUsuarios.rechazar_solicitud(solicitante, solicitado, nuevaContrasena);
    }

    public List<String> mostrar_solicitudes(String usuario, String contrasena) throws SQLException, ContrasenaErronea, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        return daoUsuarios.mostrar_solicitudes(usuario, nuevaContrasena);
    }

    public void borrar_amistad(String usuario, String amistad, String contrasena) throws SQLException, FalloAmigo, ContrasenaErronea, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        daoUsuarios.borrar_amistad(usuario, amistad, nuevaContrasena);
    }

    public List<Usuario> obtener_lista_amigos(Usuario usuario, String contrasena) throws SQLException,ContrasenaErronea, RemoteException{
        String nuevaContrasena=hashearContrasena(contrasena);

        List<Usuario> amigos=daoUsuarios.obtener_lista_amigos(usuario, nuevaContrasena);
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
        String nuevaContrasena=hashearContrasena(contrasena);

        daoUsuarios.cerrar_sesion(nombre, nuevaContrasena);
        genteConectada.remove(nombre);
    }
}