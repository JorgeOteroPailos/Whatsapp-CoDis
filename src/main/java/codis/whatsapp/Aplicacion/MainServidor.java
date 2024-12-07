package codis.whatsapp.Aplicacion;

import java.rmi.*;
import java.util.Scanner;

import static codis.whatsapp.Aplicacion.Utils.*;

public class MainServidor {

    public static void main(String[] args) {
        int portNum;
        String host;
        try {
            Scanner escaner = new Scanner(System.in);

            // Solicitar IP
            while (true) {
                System.out.println("Por favor, introduzca la IP:");
                host = escaner.nextLine();
                try {
                    validarIP(host);
                    break; // Salir del bucle si es válida
                } catch (IllegalArgumentException e) {
                    System.err.println("IP no válida: " + e.getMessage());
                }
            }

            // Solicitar número de puerto
            while (true) {
                System.out.println("Por favor, introduzca el número de puerto:");
                try {
                    portNum = escaner.nextInt();
                    validarPuerto(String.valueOf(portNum));
                    break; // Salir del bucle si es válido
                } catch (IllegalArgumentException e) {
                    System.err.println("Puerto no válido: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error: Introduzca un número válido.");
                    escaner.next(); // Limpiar entrada incorrecta
                }
            }

            Servidor exportedObj = new Servidor("servidor");
            startRegistry(portNum);

            // Registrar el objeto en el registro RMI
            String registryURL = "rmi://" + host + ":" + portNum + "/" + exportedObj.getNombre();
            Naming.rebind(registryURL, exportedObj);

            System.out.println("Some Server ready.");
        } catch (Exception e) {
            System.out.println("Remote Exception : " + e);
        }
    }


}
