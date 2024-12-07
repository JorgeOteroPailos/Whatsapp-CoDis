package codis.whatsapp.Aplicacion;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private final List<Mensaje> mensajes;

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public Chat(){
        mensajes=new ArrayList<>();
    }
}
