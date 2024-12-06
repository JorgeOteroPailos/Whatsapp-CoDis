package codis.whatsapp;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

public class MainServidor {
    public Servidor() throws RemoteException {
        super();
    }

    public static void main(String args[]) {
        int portNum;
        String host;
        try {
            // code for port number value to be supplied
            System.out.println("Por favor, introduzca la IP");
            Scanner escaner=new Scanner(System.in);
            host=escaner.nextLine();

            System.out.println("Por favor, introduzca el puerto");
            escaner=new Scanner(System.in);
            portNum=escaner.nextInt();

            SomeImpl exportedObj = new SomeImpl();
            startRegistry(portNum);
            // register the object under the name “some”
            String registryURL = "rmi://"+host+":" + portNum + "/some";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Some Server ready.");
        } catch (Exception e) {
            System.out.println("Remote Exception : " + e);
        }
    }

    private static void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
            // The above call will throw an exception
            // if the registry does not already exist
        } catch (RemoteException ex) {
            // No valid registry at that port.
            System.out.println(
                    "RMI registry cannot be located at port " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println(
                    "RMI registry created at port " + RMIPortNum);
        }
    }
}
