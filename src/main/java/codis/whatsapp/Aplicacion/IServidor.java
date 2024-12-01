package codis.whatsapp.Aplicacion;

import java.rmi.Remote;

public interface IServidor extends Remote {
    public void inicializar(Cliente cliente);
}
