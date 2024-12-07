package codis.whatsapp.Aplicacion.Excepciones;

import java.io.Serializable;

public class FalloUsuario extends Exception implements Serializable {
    public FalloUsuario(String handleMessage){
        super(handleMessage);
    }
}
