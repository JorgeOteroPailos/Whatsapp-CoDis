package codis.whatsapp.Aplicacion;

public class Usuario {
    public String nombre;

    private ICliente ORemoto;

    public Usuario(String nombre){
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

}
