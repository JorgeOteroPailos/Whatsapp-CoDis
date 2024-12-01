package codis.whatsapp.Aplicacion;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Cliente extends UnicastRemoteObject implements ICliente{
    private Usuario user;
    private String IP;
    private int puerto;
    private ArrayList<Cliente> amigosEnLinea;
    private String codigo;

    public void enviar(String m, Usuario remitente, String codigo){
        //TODO
    }

    public Usuario getUser(){
        return user;
    }

    public Cliente(Usuario user, String IP, int puerto) throws RemoteException {
        super();
        this.user=user;
        this.IP=IP;
        this.puerto=puerto;
    }

    public ArrayList<Cliente> getAmigosEnLinea() {
        return amigosEnLinea;
    }

    public void setAmigosEnLinea(ArrayList<Cliente> amigosEnLinea) {
        this.amigosEnLinea = amigosEnLinea;
    }
}
