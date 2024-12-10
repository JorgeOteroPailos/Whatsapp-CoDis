package codis.whatsapp.Aplicacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {
    void recibir(String m, Usuario remitente) throws RemoteException;

    void informarDeAmigoOnline(Usuario amigo) throws RemoteException;//TODO revisar la seguridad

    void informarDeAmigoDesconectado(Usuario amigo) throws RemoteException;
}
