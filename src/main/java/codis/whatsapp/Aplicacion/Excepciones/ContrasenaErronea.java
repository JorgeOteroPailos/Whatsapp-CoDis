package codis.whatsapp.Aplicacion.Excepciones;

import java.io.Serializable;

public class ContrasenaErronea extends Exception implements Serializable {
    public ContrasenaErronea(String handleMessage){
        super(handleMessage);
    }
}
