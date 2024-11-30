package codis.whatsapp.Aplicacion;

public class Cliente implements ICliente{
    private Usuario user;
    private String IP;
    private int puerto;

    public void enviar(String m, Usuario remitente){
        //TODO
    }

    public Usuario getUser(){
        return user;
    }

    public Cliente(Usuario user, String IP, int puerto){
        this.user=user;
        this.IP=IP;
        this.puerto=puerto;
    }
}
