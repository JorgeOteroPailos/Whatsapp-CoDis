package codis.whatsapp.Aplicacion.Excepciones;

public class ContrasenaErronea extends Exception {
    public ContrasenaErronea(String handleMessage){
        super(handleMessage);
    }
}
