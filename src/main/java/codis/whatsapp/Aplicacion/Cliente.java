package codis.whatsapp.Aplicacion;

import codis.whatsapp.GUI.ControladorPrincipal;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;
import static codis.whatsapp.Aplicacion.Utils.startRegistry;

public class Cliente extends UnicastRemoteObject implements ICliente{
    private final Usuario user;
    private final ControladorPrincipal cp;
    private IServidor servidor;
    private final String contrasena;
    private List<Usuario> amigosOnline;

    private final Map<Usuario, Chat> chats = new HashMap<>(); // Mapa para manejar chats
    public void recibir(String texto, Usuario remitente, String codigo) throws RemoteException {
        // Buscar al remitente en la lista de amigos online
        Usuario r = amigosOnline.stream()
                .filter(remitente::equals)
                .findFirst()
                .orElse(null);

        if (r == null || !Objects.equals(r.getCodigoSesion(), remitente.getCodigoSesion())) {
            return;
        }

        Mensaje m = new Mensaje(texto, LocalDateTime.now(), user);
        cp.getChatSeleccionado().getMensajes().add(m);
        cp.agregarMensaje(m);
    }
    @Override
    public void informarDeAmigoOnline(Usuario amigo) {
        debugPrint("Añadiendo el chat de "+amigo.nombre);
        amigosOnline.add(amigo);
        chats.put(amigo,new Chat());
        cp.agregarChat(amigo);
    }

    public void enviarMensaje(String mensaje, Usuario destino) throws RemoteException {
        Usuario d = amigosOnline.stream()
                .filter(destino::equals)
                .findFirst()
                .orElse(null);
        if(d==null){return;}
        d.getORemoto().recibir(mensaje, user, this.user.getCodigoSesion());
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
        for (Usuario u : amigosOnline){
            chats.put(u, new Chat());
            cp.agregarChat(u);
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
            amigosOnline=servidor.obtener_lista_amigos(user, contrasena);
        }catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            debugPrint(Arrays.toString(e.getStackTrace()));
        }

    }

    private void iniciarSesion(IServidor servidor, String ip, int puerto, String contrasena) throws Exception {
         user.setCodigoSesion(servidor.iniciar_sesion(ip, puerto, user.nombre, contrasena));
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
