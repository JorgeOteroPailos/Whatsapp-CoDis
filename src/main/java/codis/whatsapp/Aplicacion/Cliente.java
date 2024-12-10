package codis.whatsapp.Aplicacion;

import codis.whatsapp.Aplicacion.Excepciones.ContrasenaErronea;
import codis.whatsapp.Aplicacion.Excepciones.FalloSolicitud;
import codis.whatsapp.GUI.ControladorPrincipal;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;
import static codis.whatsapp.Aplicacion.Utils.startRegistry;

public class Cliente extends UnicastRemoteObject implements ICliente{
    private final Usuario user;
    private final ControladorPrincipal cp;
    private IServidor servidor;
    private final String contrasena;
    private final Map<Usuario, Chat> chats = new HashMap<>(); // Mapa para manejar chats
    private List<Usuario> solicitudesPendientes = new LinkedList<>(); // Simula solicitudes pendientes

    public List<Usuario> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public void setSolicitudesPendientes(List<Usuario> solicitudesPendientes){
        this.solicitudesPendientes=solicitudesPendientes;
    }

    public void recibir(String texto, Usuario remitente) throws RemoteException {
        // Buscar al remitente en la lista de amigos online
        Usuario r = chats.keySet().stream()
                .filter(remitente::equals)
                .findFirst()
                .orElse(null);

        if (r == null || !r.getCodigoSesion().equals( remitente.getCodigoSesion())) {
            debugPrint("Por alguna razón, returneo de recibir");
            return;
        }

        Mensaje m = new Mensaje(texto, LocalDateTime.now(), remitente);
        chats.get(r).anadirMensaje(m);
        cp.agregarMensaje(chats.get(r), m);
    }

    public void informarDeAmigoOnline(Usuario amigo) {
        if(amigo==null){return;}
        debugPrint("Añadiendo el chat de "+amigo.nombre);
        chats.put(amigo,new Chat());
        cp.agregarChat(amigo);
    }

    public void informarDeAmigoDesconectado(Usuario amigo){
        if(!Objects.equals(Objects.requireNonNull(chats.keySet().stream()
                .filter(amigo::equals)
                .findFirst()
                .orElse(null)).getCodigoSesion(), amigo.getCodigoSesion())){
            return;
        }
        chats.remove(amigo);
        cp.eliminarChat(amigo);
    }

    public void aceptarAmistad(String solicitante) throws Exception {
        Usuario u = servidor.aceptar_solicitud(solicitante, user.nombre, contrasena);
        if(u==null){return;}
        chats.put(u, new Chat());
        cp.agregarChat(u);
    }

    public void rechazarAmistad(String solicitante) throws Exception {
        servidor.rechazar_solicitud(solicitante, user.nombre, contrasena);
    }

    public void cerrarSesion(){
        try{
            for(Usuario u:chats.keySet()){
                u.getORemoto().informarDeAmigoDesconectado(this.user);
            }
            servidor.cerrar_sesion(user.nombre, contrasena);
        }catch(Exception ignored){

        }

    }

    public void enviarMensaje(String mensaje, Usuario destino) throws RemoteException {
        Usuario d = chats.keySet().stream()
                .filter(destino::equals)
                .findFirst()
                .orElse(null);
        if(d==null){return;}
        d.getORemoto().recibir(mensaje, user);
    }

    public String getContrasena(){return contrasena;}

    public Usuario getUser(){
        return user;
    }

    public Cliente(Usuario user, String IP, int puerto, ControladorPrincipal cp, int puertoServidor, String ipServidor, String contrasena) throws Exception {
        super();
        this.user=user;
        this.cp=cp;
        this.contrasena=contrasena;
        inicializarMovidasRemotas(IP, puerto, puertoServidor, ipServidor, contrasena);
        inicializarChats();

        debugPrint("Cliente creado correctamente (milagro)");
    }

    public Map<Usuario, Chat> getChats() {
        return chats;
    }

    public void inicializarChats() {
        for (Usuario u : chats.keySet()){
            chats.put(u, new Chat());
            cp.agregarChat(u);
            debugPrint("añadiendo el chat "+u.nombre);
        }
    }

    private void inicializarMovidasRemotas(String IP, int puerto, int puertoServidor, String ipServidor, String contrasena) throws Exception {
        publicarse(IP, puerto);
        debugPrint("Cliente publicado correctamente");

        servidor=inicializarServidor(puertoServidor, ipServidor);
        debugPrint("Conexión con el servidor realizada");

        iniciarSesion(servidor, IP, puerto, contrasena);
        debugPrint("sesión iniciada");

        obtenerAmigosOnline(servidor, contrasena);
        debugPrint("Lista de amigos obtenida correctamente");


    }

    private void obtenerAmigosOnline(IServidor servidor, String contrasena) throws Exception {
        try{
            List<Usuario> aux=servidor.obtener_lista_amigos(user, contrasena);
            for(Usuario u:aux){
                chats.put(u, new Chat());
                debugPrint("añadiendo el chat "+u.nombre);
                if(chats.containsKey(u)){debugPrint("CUANDO LO INTRODUZCO, existe el chat "+u);}
            }
        }catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            debugPrint(Arrays.toString(e.getStackTrace()));
        }

    }

    private void iniciarSesion(IServidor servidor, String ip, int puerto, String contrasena) throws Exception {
         user.setCodigoSesion(servidor.iniciar_sesion(ip, puerto, user.nombre, contrasena));
         for(String nombre : servidor.mostrar_solicitudes(user.nombre, contrasena)){
             solicitudesPendientes.add(new Usuario(nombre));
         }
    }

    public static IServidor inicializarServidor(int puertoServidor, String ipServidor) throws Exception {
        return (IServidor) Naming.lookup("rmi://" + ipServidor + ":" + puertoServidor + "/servidor");
    }

    private void publicarse(String IP, int puerto) throws RemoteException, MalformedURLException{
        debugPrint("iniciando publicacion del cliente");
        startRegistry(puerto);
        String registryURL = "rmi://"+IP+":" + puerto + "/"+user.nombre;
        Naming.rebind(registryURL, this);
    }

    public IServidor getServidor(){
        return servidor;
    }
}
