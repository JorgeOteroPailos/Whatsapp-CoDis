package codis.whatsapp.Aplicacion;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Servidor extends UnicastRemoteObject implements IServidor{

    protected Servidor() throws RemoteException {
        super();
    }

    @Override
    public void inicializar(Cliente cliente) {
        ArrayList<Cliente> amigosEnLinea=cliente.getAmigosEnLinea();


    }
}
