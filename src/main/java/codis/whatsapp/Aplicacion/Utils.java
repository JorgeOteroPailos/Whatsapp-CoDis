package codis.whatsapp.Aplicacion;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Utils {
    private static final boolean estoyDebuggeando=false;
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    public static void debugPrint(String mensaje){
        if(estoyDebuggeando){
            System.out.println(mensaje);
        }
    }

    static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
            // The above call will throw an exception
            // if the registry does not already exist
        } catch (RemoteException ex) {
            // No valid registry at that port.
            System.out.println(
                    "RMI registry cannot be located at port " + RMIPortNum);
            LocateRegistry.createRegistry(RMIPortNum);
            System.out.println(
                    "RMI registry created at port " + RMIPortNum);
        }
    }

    public static void validarIP(String ip) throws IllegalArgumentException{
        if (ip == null || ip.isEmpty()) {
            throw new IllegalArgumentException("La dirección IP no puede estar vacía");
        }

        String regex = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)|localhost$";
        if (!ip.matches(regex)) {
            throw new IllegalArgumentException("La dirección IP no es válida");
        }
    }

    public static int validarPuerto(String puerto) throws IllegalArgumentException{
        if (puerto == null || puerto.isEmpty()) {
            throw new IllegalArgumentException("El puerto no puede estar vacío");
        }

        try {
            int puertoInt = Integer.parseInt(puerto);
            if (puertoInt < MIN_PORT || puertoInt > MAX_PORT) {
                throw new IllegalArgumentException("El puerto debe estar entre " + MIN_PORT + " y " + MAX_PORT + ".");
            }
            return puertoInt;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número de puerto debe ser un número válido");
        }
    }

    public static void validarContrasena(String contrasena) throws IllegalArgumentException{ //TODO MANEJAR EXCEPCIONES
        if (contrasena.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        if (contrasena.contains(" ") || contrasena.contains("\"") || contrasena.contains("\\")) {
            throw new IllegalArgumentException("La contraseña no debe contener espacios, comillas o \\.");
        }

        boolean tieneLetra = contrasena.matches(".*[a-zA-Z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");
        boolean tieneSimbolo = contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=~`\\[\\]].*");

        if (!tieneLetra || !tieneNumero || !tieneSimbolo) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra, un número y un símbolo.");
        }
    }


}

