package codis.whatsapp.Aplicacion;

import java.rmi.Remote;

public interface ICliente extends Remote {
    public void enviar(String m, Usuario remitente, String codigo);



}
