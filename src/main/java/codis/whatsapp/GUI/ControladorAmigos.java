package codis.whatsapp.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ControladorAmigos {

    @FXML
    private TextField campoAgregarAmigo;

    @FXML
    private VBox listaSolicitudes;

    private final List<String> solicitudesPendientes = new ArrayList<>(); // Simula solicitudes pendientes

    @FXML
    public void initialize() {
        // Población de ejemplo para solicitudes
        solicitudesPendientes.add("amigo1");
        solicitudesPendientes.add("amigo2");
        actualizarSolicitudes();
    }

    @FXML
    public void agregarAmigo() {
        String nuevoAmigo = campoAgregarAmigo.getText().trim();
        if (!nuevoAmigo.isEmpty()) {
            System.out.println("Solicitud enviada a: " + nuevoAmigo);
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
        System.out.println("Solicitud aceptada: " + solicitud);
        solicitudesPendientes.remove(solicitud);
        actualizarSolicitudes();
    }

    private void rechazarSolicitud(String solicitud) {
        System.out.println("Solicitud rechazada: " + solicitud);
        solicitudesPendientes.remove(solicitud);
        actualizarSolicitudes();
    }
}

