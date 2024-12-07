package codis.whatsapp.Aplicacion;

import codis.whatsapp.GUI.ControladorPrincipal;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;
import static codis.whatsapp.Aplicacion.Utils.startRegistry;

public class Cliente extends UnicastRemoteObject implements ICliente{
    private final Usuario user;
    private final ControladorPrincipal cp;
    private IServidor servidor;
    private String codigoSesion;

    private final Map<Usuario, Chat> chats = new HashMap<>(); // Mapa para manejar chats

    public void recibir(String m, Usuario remitente, String codigo) throws RemoteException{
        //TODO

    }


    public Usuario getUser(){
        return user;
    }

    public Cliente(Usuario user, String IP, int puerto, ControladorPrincipal cp, int puertoServidor, String ipServidor, String contrasena) throws Exception {
        super();
        this.user=user;
        this.cp=cp;
        inicializarMovidasRemotas(IP, puerto, puertoServidor, ipServidor, contrasena);

    }

    public Map<Usuario, Chat> getChats() {
        return chats;
    }

    public void inicializarChats() {
        //TODO iniciar
    }

    private void inicializarMovidasRemotas(String IP, int puerto, int puertoServidor, String ipServidor, String contrasena) throws Exception {
        publicarse(IP, puerto);
        debugPrint("Cliente publicado correctamente");
        servidor=inicializarServidor(puertoServidor, ipServidor);
        debugPrint("Conexión con el servidor realizada");
        iniciarSesion(servidor, IP, puerto, contrasena);
        debugPrint("sesión iniciada");
        debugPrint("Cliente creado correctamente (milagro)");
    }

    public void iniciarSesion(IServidor servidor, String ip, int puerto, String contrasena) throws Exception {

        //TODO usar el codigo
        servidor.iniciar_sesion(ip, puerto, user.nombre, contrasena);

    }

    public static IServidor inicializarServidor(int puertoServidor, String ipServidor) throws Exception {
        return (IServidor) Naming.lookup("rmi://" + ipServidor + ":" + puertoServidor + "/servidor");
    }

    private void publicarse(String IP, int puerto) throws RemoteException, MalformedURLException{
        debugPrint("iniciando publicacion del cliente");
        startRegistry(puerto);
        // register the object under the name “some”
        String registryURL = "rmi://"+IP+":" + puerto + "/"+user.nombre;
        Naming.rebind(registryURL, this);
    }

    public IServidor getServidor(){
        return servidor;
    }
}
