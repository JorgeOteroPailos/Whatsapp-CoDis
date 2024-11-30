package codis.whatsapp.Aplicacion;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class Mensaje{
    public final String texto;
    public final LocalDateTime tiempo;
    public final Usuario remitente;

    public Mensaje(String texto, LocalDateTime tiempo, Usuario remitente){
        this.remitente=remitente;
        this.texto=texto;
        this.tiempo=tiempo;
    }
}
