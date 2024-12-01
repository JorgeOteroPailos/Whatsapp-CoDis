package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.Chat;
import codis.whatsapp.Aplicacion.Cliente;
import codis.whatsapp.Aplicacion.Mensaje;
import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.MainCliente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    private Cliente user;
    private Chat chatSeleccionado;
    private final Map<String, Chat> chats = new HashMap<>(); // Mapa para manejar chats

    @FXML
    public void initialize() {
        // Configurar el comportamiento del scroll manualmente
        scrollPaneMensajes.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 0.05; // Ajusta la sensibilidad
            double value = scrollPaneMensajes.getVvalue();
            scrollPaneMensajes.setVvalue(value - deltaY);
            event.consume();
        });

        // Configurar el botón de enviar
        sendButton.setOnAction(event -> enviarMensaje());
    }

    public void inicializarChats() {
        Usuario user1 = new Usuario("amigo1");
        Chat chat1 = new Chat(user1);
        chat1.getMensajes().add(new Mensaje("Hola, ¿cómo estás?", LocalDateTime.now().minusMinutes(10), user1));
        chat1.getMensajes().add(new Mensaje("¡Bien! ¿Y tú?", LocalDateTime.now().minusMinutes(5), user.getUser()));

        Usuario user2 = new Usuario("amigo2");
        Chat chat2 = new Chat(user2);
        chat2.getMensajes().add(new Mensaje("¿Qué tal tu día?", LocalDateTime.now().minusMinutes(20), user2));

        chats.put(chat1.getUser().getNombre(), chat1);
        chats.put(chat2.getUser().getNombre(), chat2);

        agregarChat(chat1);
        agregarChat(chat2);
    }

    private void agregarChat(Chat chat) {
        String nombreChat = chat.getUser().getNombre();
        Label chatLabel = new Label(nombreChat);
        chatLabel.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-color: lightgray; -fx-border-color: grey;");
        chatLabel.setOnMouseClicked(event -> seleccionarChat(nombreChat));
        listaChats.getChildren().add(chatLabel);
    }

    private void seleccionarChat(String nombreChat) {
        chatSeleccionado = chats.get(nombreChat);
        currentChatLabel.setText("Chat con " + nombreChat);
        actualizarMensajes();
    }

    private void actualizarMensajes() {
        listaMensajes.getChildren().clear();
        if (chatSeleccionado != null) {
            for (Mensaje mensaje : chatSeleccionado.getMensajes()) {
                agregarMensaje(mensaje);
            }
            // Desplaza automáticamente al final después de cargar los mensajes
            scrollToBottom();
        }
    }

    private void enviarMensaje() {
        String texto = messageTextField.getText();
        if (!texto.isBlank() && chatSeleccionado != null) {
            Mensaje mensaje = new Mensaje(texto, LocalDateTime.now(), user.getUser());
            chatSeleccionado.getMensajes().add(mensaje);
            agregarMensaje(mensaje);
            messageTextField.clear();
        }
    }

    private void agregarMensaje(Mensaje mensaje) {
        HBox contenedorMensaje = new HBox();
        VBox mensajeVBox = new VBox(5);
        mensajeVBox.setMaxWidth(300); // Limitar el ancho máximo de la burbuja del mensaje

        // Etiqueta del remitente
        Label remitenteLabel = new Label(mensaje.remitente.getNombre());
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
        if (mensaje.remitente.equals(user.getUser())) {
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
    }

    private void scrollToBottom() {
        // Desplazar automáticamente al final del ScrollPane
        Platform.runLater(() -> scrollPaneMensajes.setVvalue(1.0));
    }

    public void configurarParametros(String ip, String puerto, String ipServidor, String puertoServidor, Usuario user) {
        try{
            this.user = new Cliente(user, ip, Integer.parseInt(puerto));
        }catch(RemoteException e){
            System.err.println("Error en la creación del cliente");
            System.exit(-1);
        }

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
            ventanaAmigos = new Stage();
            debugPrint("Stage creado");
            ventanaAmigos.setTitle("Gestión de Amigos");
            ventanaAmigos.setScene(new Scene(root));
            ventanaAmigos.setOnCloseRequest(event -> ventanaAmigos = null); // Limpiar referencia al cerrar
            ventanaAmigos.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al abrir la ventana de amigos: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Error inesperado al abrir la ventana de amigos: "+e.getMessage());
        }
    }
}
