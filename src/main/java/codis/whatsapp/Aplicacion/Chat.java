package codis.whatsapp.Aplicacion;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<Mensaje> mensajes;

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public Chat(){
        mensajes=new ArrayList<Mensaje>();
    }

    public void anadirMensaje(Mensaje mensaje){
        mensajes.add(mensaje);
    }
}
