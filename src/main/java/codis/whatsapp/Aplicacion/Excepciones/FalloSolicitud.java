package codis.whatsapp.Aplicacion.Excepciones;

import java.io.Serializable;

public class FalloSolicitud extends Exception implements Serializable {
    public FalloSolicitud(String handleMessage){
        super(handleMessage);
    }
}
