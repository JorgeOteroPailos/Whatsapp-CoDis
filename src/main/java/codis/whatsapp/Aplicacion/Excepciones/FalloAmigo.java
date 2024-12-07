package codis.whatsapp.Aplicacion.Excepciones;

import java.io.Serializable;

public class FalloAmigo extends Exception implements Serializable {
    public FalloAmigo(String handleMessage){
        super(handleMessage);
    }
}
