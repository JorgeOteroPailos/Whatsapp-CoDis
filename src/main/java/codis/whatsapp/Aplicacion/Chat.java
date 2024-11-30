package codis.whatsapp.Aplicacion;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Mensaje> mensajes;
    private Usuario user;

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public Chat(Usuario user){
        this.user=user;
        mensajes=new ArrayList<Mensaje>();
    }

    public Usuario getUser(){
        return user;
    }

    public void anadirMensaje(Mensaje mensaje){
        mensajes.add(mensaje);
    }
}
