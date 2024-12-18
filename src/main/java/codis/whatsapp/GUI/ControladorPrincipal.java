package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.*;
import codis.whatsapp.MainCliente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;

public class ControladorPrincipal {

    @FXML
    private VBox listaChats;
    @FXML
    private VBox listaMensajes;
    @FXML
    private ScrollPane scrollPaneMensajes;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Label currentChatLabel;
    private Stage ventanaAmigos;
    private Cliente cliente;
    private Chat chatSeleccionado;

    private String nombreChatSeleccionado;

    @FXML
    public void initialize() {
        // Configurar el comportamiento del scroll manualmente
        scrollPaneMensajes.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 0.05; // Ajusta la sensibilidad
            double value = scrollPaneMensajes.getVvalue();
            scrollPaneMensajes.setVvalue(value - deltaY);
            event.consume();
        });

        messageTextField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                enviarMensaje(); // Llama al método para enviar el mensaje
                messageTextField.requestFocus(); // Mantener el cursor en el TextField
            }
        });

        // Configurar el botón de enviar
        sendButton.setOnAction(event -> enviarMensaje());
    }


    public void agregarChat(Usuario user) {
        Platform.runLater(()->{
            String nombreChat = user.nombre;
            Label chatLabel = new Label(nombreChat);
            chatLabel.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-color: lightgray; -fx-border-color: grey;");
            chatLabel.setOnMouseClicked(event -> seleccionarChat(nombreChat));
            listaChats.getChildren().add(chatLabel);
        });
    }

    public void cerrarSesion(){
        cliente.cerrarSesion();
    }

    public void eliminarChat(Usuario user) {
        Platform.runLater(() -> {
            String nombreChat = user.nombre;
            Label chatAEliminar = null;

            // Buscar el Label correspondiente al chat en la lista de chats
            for (var nodo : listaChats.getChildren()) {
                if (nodo instanceof Label label) {
                    if (label.getText().equals(nombreChat)) {
                        chatAEliminar = label;
                        break;
                    }
                }
            }
            // Si se encontró, eliminar el chat
            if (chatAEliminar != null) {
                listaChats.getChildren().remove(chatAEliminar);
                debugPrint("Chat eliminado: " + nombreChat);
            } else {
                debugPrint("No se encontró el chat para eliminar: " + nombreChat);
            }
        });
    }

    private void seleccionarChat(String nombreChat) {
        try{
            chatSeleccionado = cliente.getChats().get(new Usuario(nombreChat));
            nombreChatSeleccionado=nombreChat;
            currentChatLabel.setText("Chat con " + nombreChat);

            // Restaurar estilo del chat seleccionado
            for (var nodo : listaChats.getChildren()) {
                if (nodo instanceof Label label) {
                    if (label.getText().equals(nombreChat)) {
                        label.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-color: lightgray; -fx-border-color: grey;");
                        break;
                    }
                }
            }

            actualizarMensajes();
        }catch (Exception e){
            System.err.println(e.getMessage());
            Popup.show(e);
        }

    }

    private void actualizarMensajes() {
        listaMensajes.getChildren().clear();
        if (chatSeleccionado != null) {
            for (Mensaje mensaje : chatSeleccionado.getMensajes()) {
                agregarMensaje(chatSeleccionado, mensaje);
            }
            // Desplaza automáticamente al final después de cargar los mensajes
            scrollToBottom();
        }
    }

    private void enviarMensaje() {
        String texto = messageTextField.getText();
        if (!texto.isBlank() && chatSeleccionado != null) {
            Mensaje mensaje = new Mensaje(texto, LocalDateTime.now(), cliente.getUser());
            chatSeleccionado.getMensajes().add(mensaje);
            agregarMensaje(chatSeleccionado, mensaje);
            messageTextField.clear();
            try {
                cliente.enviarMensaje(texto,new Usuario(nombreChatSeleccionado));
            } catch (RemoteException e) {
                System.err.println("Error en el envío del mensaje: "+e.getMessage());
                Popup.show("Error","Error en el envío del mensaje, inténtelo de nuevo más tarde", Alert.AlertType.ERROR);
            }
            debugPrint("Mensaje enviado guay");

        }
    }

    private void destacarChat(Usuario user) {
        String nombreChat = user.nombre;

        for (var nodo : listaChats.getChildren()) {
            if (nodo instanceof Label label) {
                if (label.getText().equals(nombreChat)) {
                    label.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-color: yellow; -fx-border-color: grey;");
                    break;
                }
            }
        }
    }

    public void agregarMensaje(Chat c, Mensaje mensaje) {
        if(c!=chatSeleccionado){
            Platform.runLater(() -> {
                try {
                    destacarChat(new Usuario(mensaje.remitente.nombre));
                } catch (RemoteException ignored) {

                }
            });
            return;
        }
        Platform.runLater(()->{
            HBox contenedorMensaje = new HBox();
            VBox mensajeVBox = new VBox(5);
            mensajeVBox.setMaxWidth(300); // Limitar el ancho máximo de la burbuja del mensaje

            // Etiqueta del remitente
            Label remitenteLabel = new Label(mensaje.remitente.nombre);
            remitenteLabel.setStyle("-fx-font-size: 12; -fx-text-fill: grey;");

            // Contenido del mensaje
            Label textoMensaje = new Label(mensaje.texto);
            textoMensaje.setWrapText(true);
            textoMensaje.setStyle("-fx-font-size: 16;");

            // Hora del mensaje
            Label horaLabel = new Label(mensaje.tiempo.format(DateTimeFormatter.ofPattern("HH:mm")));
            horaLabel.setStyle("-fx-font-size: 10; -fx-text-fill: grey;");

            // Añadir contenido al VBox
            mensajeVBox.getChildren().addAll(remitenteLabel, textoMensaje, horaLabel);

            // Estilizar y alinear según el remitente
            if (mensaje.remitente.equals(cliente.getUser())) {
                mensajeVBox.setStyle("-fx-background-color: lightblue; -fx-padding: 10; -fx-background-radius: 10;");
                contenedorMensaje.setAlignment(Pos.CENTER_RIGHT); // Mensajes enviados a la derecha
            } else {
                mensajeVBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-background-radius: 10;");
                contenedorMensaje.setAlignment(Pos.CENTER_LEFT); // Mensajes recibidos a la izquierda
            }

            contenedorMensaje.getChildren().add(mensajeVBox);
            listaMensajes.getChildren().add(contenedorMensaje);

            // Desplaza automáticamente al final después de agregar un mensaje
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        // Desplazar automáticamente al final del ScrollPane
        Platform.runLater(() -> scrollPaneMensajes.setVvalue(1.0));
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @FXML
    public void abrirVentanaAmigos() {
        if (ventanaAmigos != null && ventanaAmigos.isShowing()) {
            System.out.println("La ventana de amigos ya está abierta.");
            return; // No abre otra ventana si ya hay una abierta
        }

        try {
            FXMLLoader loader = new FXMLLoader(MainCliente.class.getResource("amigos.fxml"));
            debugPrint("loader creado");
            Parent root = loader.load();
            debugPrint("loader cargado");

            ControladorAmigos controladorAmigos = loader.getController();
            controladorAmigos.setCliente(cliente);
            controladorAmigos.actualizarSolicitudes();

            ventanaAmigos = new Stage();
            debugPrint("Stage creado");
            ventanaAmigos.setTitle("Gestión de Amigos");
            ventanaAmigos.setScene(new Scene(root));
            ventanaAmigos.setOnCloseRequest(event -> ventanaAmigos = null); // Limpiar referencia al cerrar
            ventanaAmigos.show();
        }  catch (Exception e){
            debugPrint(Arrays.toString(e.getStackTrace()));
            System.err.println("Error inesperado al abrir la ventana de amigos: "+e.getMessage());
            ventanaAmigos.close();
            Popup.show("Error","Error al abrir la gestión de amistades", Alert.AlertType.ERROR);
        }
    }
}
