package codis.whatsapp.Aplicacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {
    void recibir(String m, Usuario remitente, String codigo) throws RemoteException;

    void informarDeAmigoOnline(Usuario amigo) throws RemoteException;//TODO revisar la seguridad
}
