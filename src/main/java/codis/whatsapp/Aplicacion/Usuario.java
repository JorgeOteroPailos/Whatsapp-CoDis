package codis.whatsapp.Aplicacion;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Usuario implements Serializable {
    public String nombre;
    private ICliente ORemoto;
    private String codigoSesion;
    public Usuario(String nombre) throws RemoteException {
        super();
        this.nombre=nombre;
    }
    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        Usuario u=(Usuario) o;
        return (u.nombre.equals(nombre));
    }

    public void setORemoto(ICliente i){
        ORemoto = i;
    }
    public String getCodigoSesion() {return codigoSesion;}
    public void setCodigoSesion(String codigoSesion) {this.codigoSesion = codigoSesion;}

    public ICliente getORemoto() {return ORemoto;}

    public Usuario(String nombre, ICliente ORemoto) throws RemoteException {
        this.nombre = nombre;
        this.ORemoto = ORemoto;
    }
}
