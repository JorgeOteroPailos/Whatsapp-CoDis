package codis.whatsapp.Aplicacion;

public class Utils {
    private static final boolean estoyDebuggeando=true;

    public static void debugPrint(String mensaje){
        if(estoyDebuggeando){
            System.out.println(mensaje);
        }
    }
}
