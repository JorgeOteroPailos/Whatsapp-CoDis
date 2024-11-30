package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.Chat;
import codis.whatsapp.Aplicacion.Cliente;
import codis.whatsapp.Aplicacion.Mensaje;
import codis.whatsapp.Aplicacion.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    private Cliente user;
    private Chat chatSeleccionado;
    private final Map<String, Chat> chats = new HashMap<>(); // Mapa para manejar chats

    @FXML
    public void initialize() {

        scrollPaneMensajes.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 0.05; // Aumenta o disminuye este factor según lo necesario
            double value = scrollPaneMensajes.getVvalue();
            scrollPaneMensajes.setVvalue(value - deltaY);
            event.consume();
        });
        // Desplazar automáticamente al final al agregar un mensaje
        scrollPaneMensajes.vvalueProperty().bind(listaMensajes.heightProperty());
        // Desplazar automáticamente al final al agregar un mensaje
        scrollPaneMensajes.vvalueProperty().bind(listaMensajes.heightProperty());
        // Configuración del botón de enviar
        sendButton.setOnAction(event -> enviarMensaje());


    }

    public void inicializarChats() {

        // Crear chats de ejemplo con listas inicializadas
        Usuario user1=new Usuario("amigo1");
        Chat chat1 = new Chat(user1);
        chat1.getMensajes().add(new Mensaje("Hola, ¿cómo estás?", LocalDateTime.now().minusMinutes(10), user1));
        chat1.getMensajes().add(new Mensaje("¡Bien! ¿Y tú?", LocalDateTime.now().minusMinutes(5), user.getUser()));

        if(user==null){System.out.println("POLLA");}

        Usuario user2=new Usuario("amigo2");
        Chat chat2 = new Chat(user2);
        chat2.getMensajes().add(new Mensaje("¿Qué tal tu día?", LocalDateTime.now().minusMinutes(20), user2));

        // Agregar chats al mapa
        chats.put(chat1.getUser().getNombre(), chat1);
        chats.put(chat2.getUser().getNombre(), chat2);

        // Mostrar chats en la lista
        agregarChat(chat1);
        agregarChat(chat2);
    }

    private void agregarChat(Chat chat) {
        String nombreChat=chat.getUser().getNombre();
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
        VBox mensajeVBox = new VBox(5);

        // Nombre del remitente
        Label remitenteLabel = new Label(mensaje.remitente.getNombre());
        remitenteLabel.setStyle("-fx-font-size: 12; -fx-text-fill: grey;");

        // Texto del mensaje
        Text textoMensaje = new Text(mensaje.texto);
        textoMensaje.setStyle("-fx-font-size: 16;");

        // Hora del mensaje
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Label horaLabel = new Label(mensaje.tiempo.format(formatter));
        horaLabel.setStyle("-fx-font-size: 10; -fx-text-fill: grey;");

        // Agrupar en el VBox
        mensajeVBox.getChildren().addAll(remitenteLabel, textoMensaje, horaLabel);

        // Aplicar estilos según el remitente
        if (mensaje.remitente.equals(user.getUser())) {
            mensajeVBox.setStyle("-fx-background-color: lightblue; -fx-padding: 10; -fx-background-radius: 10;");
        } else {
            mensajeVBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-background-radius: 10;");
        }

        listaMensajes.getChildren().add(mensajeVBox);
    }


    public void configurarParametros(String ip, String puerto, String ipServidor, String puertoServidor, Usuario user) {
        //TODO
        this.user=new Cliente(user, ip, Integer.parseInt(puerto));
    }
}
