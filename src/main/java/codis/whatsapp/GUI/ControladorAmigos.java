package codis.whatsapp.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.rmi.Naming;

import codis.whatsapp.Aplicacion.IServidor;
import codis.whatsapp.Aplicacion.Cliente;

public class ControladorAmigos {

    @FXML
    private TextField campoAgregarAmigo;

    @FXML
    private VBox listaSolicitudes;

    private Cliente user;

    private final List<String> solicitudesPendientes = new ArrayList<>(); // Simula solicitudes pendientes

    @FXML
    public void initialize() {
        //TODO usar solicitudes reales
        solicitudesPendientes.add("amigo1");
        solicitudesPendientes.add("amigo2");
        actualizarSolicitudes();
    }

    @FXML
    public void agregarAmigo() {
        String nuevoAmigo = campoAgregarAmigo.getText().trim();
        if (!nuevoAmigo.isEmpty()) {
            System.out.println("Solicitud enviada a: " + nuevoAmigo);
            try {
                user.getServidor().crear_solicitud(user.getUser().nombre, nuevoAmigo);
            }catch(Exception e){
                System.err.println("Remote Exception : " + e);
                Popup.show("Error", "Error al publicar el cliente"+e.getMessage(), Alert.AlertType.ERROR);
            }
            campoAgregarAmigo.clear();
        } else {
            System.out.println("El nombre del amigo no puede estar vacío.");
        }
    }

    private void actualizarSolicitudes() {
        listaSolicitudes.getChildren().clear();
        for (String solicitud : solicitudesPendientes) {
            HBox solicitudBox = crearSolicitudBox(solicitud);
            listaSolicitudes.getChildren().add(solicitudBox);
        }
    }

    private HBox crearSolicitudBox(String solicitud) {
        Label nombreLabel = new Label(solicitud);
        nombreLabel.setStyle("-fx-font-size: 14; -fx-padding: 5;");

        Button botonAceptar = new Button("Aceptar");
        botonAceptar.setOnAction(event -> aceptarSolicitud(solicitud));

        Button botonRechazar = new Button("Rechazar");
        botonRechazar.setOnAction(event -> rechazarSolicitud(solicitud));

        HBox solicitudBox = new HBox(10, nombreLabel, botonAceptar, botonRechazar);
        solicitudBox.setStyle("-fx-padding: 5; -fx-background-color: lightgray; -fx-border-color: darkgray;");
        return solicitudBox;
    }

    private void aceptarSolicitud(String solicitud) {

        solicitudesPendientes.remove(solicitud);
        actualizarSolicitudes();
        /*
        try{
            //TODO
            user.getServidor().aceptar_solicitud(solicitud, user.getUser().nombre());
            solicitudesPendientes.remove(solicitud);
            actualizarSolicitudes();
            //user.anadirAmigo(solicitud);
            //TODO
        }catch (RemoteException e){
            System.err.println("Error al acepar la solcitud de "+solicitud);
            Popup.show("Error solicitud", "Error al acepar la solcitud de "+solicitud, Alert.AlertType.ERROR);
        }*/

    }

    private void rechazarSolicitud(String solicitud) {
        System.out.println("Solicitud rechazada: " + solicitud);
        solicitudesPendientes.remove(solicitud);
        try {
            //TODO
            //user.getServidor().rechazar_solicitud(solicitud,user.getUser().nombre());
        }catch(Exception e){
            System.err.println("Remote Exception : " + e);
            Popup.show("Error", "Error al publicar el cliente"+e.getMessage(), Alert.AlertType.ERROR);
        }
        actualizarSolicitudes();
    }

    public void setCliente(Cliente user){
        this.user=user;
    }
}

