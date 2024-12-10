package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.Cliente;
import codis.whatsapp.Aplicacion.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;

public class ControladorAmigos {

    @FXML
    private TextField campoAgregarAmigo;

    @FXML
    private VBox listaSolicitudes;

    private Cliente user;



    @FXML
    public void initialize() {
    }


    @FXML
    public void agregarAmigo() {
        String nuevoAmigo = campoAgregarAmigo.getText().trim();
        if (!nuevoAmigo.isEmpty()) {
            try {
                user.getServidor().crear_solicitud(user.getUser().nombre, nuevoAmigo, user.getContrasena());
            }catch(Exception e){
                System.err.println("Remote Exception : " + e);
                Popup.show("Error", "Error: "+e.getMessage(), Alert.AlertType.ERROR);
                return;
            }
            Popup.show("Guay","Solicitud enviada correctamente", Alert.AlertType.INFORMATION);
            campoAgregarAmigo.clear();
        } else {
            System.out.println("El nombre del amigo no puede estar vacío.");
        }
    }

    public void actualizarSolicitudes() {
        listaSolicitudes.getChildren().clear();
        try {
            LinkedList<Usuario> solicitudes=new LinkedList<>();
            List<String> aux=user.getServidor().mostrar_solicitudes(user.getUser().nombre, user.getContrasena());
            for(String solicitud : aux){
                solicitudes.add(new Usuario(solicitud));
            }
            user.setSolicitudesPendientes(solicitudes);
            for (Usuario solicitud : user.getSolicitudesPendientes()) {
                System.out.println("Hay solicitudes pendientes");
                HBox solicitudBox = crearSolicitudBox(solicitud.nombre);
                listaSolicitudes.getChildren().add(solicitudBox);
            }
        }catch(Exception ignored){

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
        try{
            user.getSolicitudesPendientes().remove(new Usuario(solicitud));
            actualizarSolicitudes();
            user.aceptarAmistad(solicitud);
        } catch (Exception e) {
            Popup.show("Error", "Error al aceptar la solicitud: "+e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void rechazarSolicitud(String solicitud) {
        try{
            System.out.println("Solicitud rechazada: " + solicitud);
            user.getSolicitudesPendientes().remove(new Usuario(solicitud));
            actualizarSolicitudes();
            user.rechazarAmistad(solicitud);
        } catch (Exception e) {
            Popup.show("Error", "Error al rechazar la solicitud: "+e.getMessage(), Alert.AlertType.ERROR);
        }


    }

    public void setCliente(Cliente user){
        this.user=user;
    }
}

