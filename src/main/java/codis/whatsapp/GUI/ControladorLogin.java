package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.MainCliente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static codis.whatsapp.Aplicacion.Utils.debugPrint;

public class ControladorLogin {

    // Constantes para valores predeterminados y rangos válidos
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String DEFAULT_SERVER_IP = "192.168.1.1";
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_SERVER_PORT = 9090;
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    @FXML
    private Label textoVentana;
    @FXML
    private Label etiquetaNombreUsuario;
    @FXML
    private Label etiquetaContrasena;
    @FXML
    private TextField cuadroTextoNombreUsuario;
    @FXML
    private PasswordField cuadroTextoContrasena;

    // Elementos de ajustes
    @FXML
    private VBox seccionAjustes;
    @FXML
    private TextField campoIP;
    @FXML
    private TextField campoPuerto;
    @FXML
    private TextField campoIPServidor;
    @FXML
    private TextField campoPuertoServidor;

    @FXML
    public void initialize() {
        // Ocultar la sección de ajustes inicialmente
        seccionAjustes.setVisible(false);
        seccionAjustes.setManaged(false);
    }

    @FXML
    public void mostrarAjustes() {
        boolean visible = seccionAjustes.isVisible();
        seccionAjustes.setVisible(!visible);
        seccionAjustes.setManaged(!visible);
    }

    @FXML
    public void iniciarSesion(ActionEvent event) {
        textoVentana.setText("Inicio de sesión");
        etiquetaContrasena.setText("Contraseña");
        etiquetaNombreUsuario.setText("Nombre de usuario");

        String contrasena = cuadroTextoContrasena.getText();
        String nombreUsuario = cuadroTextoNombreUsuario.getText();

        try {
            // Validar y abrir la aplicación
            Usuario user = new Usuario(nombreUsuario);
            abrirAplicacion(event, user);
        } catch (IllegalArgumentException e) {
            Popup.show("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirAplicacion(ActionEvent event, Usuario user) {
        try {

            // Recoge los valores ingresados o usa los valores predeterminados
            String ip = campoIP.getText().isEmpty() ? DEFAULT_IP : campoIP.getText();
            String puerto = campoPuerto.getText().isEmpty() ? String.valueOf(DEFAULT_PORT) : campoPuerto.getText();
            String ipServidor = campoIPServidor.getText().isEmpty() ? DEFAULT_SERVER_IP : campoIPServidor.getText();
            String puertoServidor = campoPuertoServidor.getText().isEmpty() ? String.valueOf(DEFAULT_SERVER_PORT) : campoPuertoServidor.getText();


            // Validar IP y puerto antes de proceder
            validarIP(ip);
            validarPuerto(puerto);
            validarIP(ipServidor);
            validarPuerto(puertoServidor);

            // Cargar la vista principal
            debugPrint("Iniciando cambio de ventana");
            FXMLLoader loader = new FXMLLoader(MainCliente.class.getResource("principal.fxml"));
            Parent root = loader.load();
            debugPrint("FXML cargado");

            ControladorPrincipal controladorPrincipal = loader.getController();
            controladorPrincipal.configurarParametros(ip, puerto, ipServidor, puertoServidor,user);
            controladorPrincipal.inicializarChats();


            Stage nuevaVentana = new Stage();
            nuevaVentana.setTitle("Aplicación Principal");
            nuevaVentana.setScene(new Scene(root, 800, 600));
            nuevaVentana.show();

            // Cerrar la ventana actual
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Error al abrir la ventana principal: " + e.getMessage());
        }
    }

    public void crearCuenta(ActionEvent event){
        textoVentana.setText("Creación de cuenta");
        etiquetaContrasena.setText("Nueva contraseña");
        etiquetaNombreUsuario.setText("Nuevo nombre de usuario");
        String contrasena=cuadroTextoContrasena.getText();
        if(!validarContrasena(contrasena)){return;}
        String nombreUsuario=cuadroTextoNombreUsuario.getText();
        //TODO ver que está todo correcto tal

        Usuario user=new Usuario(nombreUsuario);

        try {
            // Validar y abrir la aplicación
            abrirAplicacion(event, user);
        } catch (IllegalArgumentException e) {
            Popup.show("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void validarIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            throw new IllegalArgumentException("La dirección IP no puede estar vacía.");
        }

        String regex = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)|localhost$";
        if (!ip.matches(regex)) {
            throw new IllegalArgumentException("La dirección IP no es válida: " + ip);
        }
    }

    private void validarPuerto(String puerto) {
        if (puerto == null || puerto.isEmpty()) {
            throw new IllegalArgumentException("El puerto no puede estar vacío.");
        }

        try {
            int puertoInt = Integer.parseInt(puerto);
            if (puertoInt < MIN_PORT || puertoInt > MAX_PORT) {
                throw new IllegalArgumentException("El puerto debe estar entre " + MIN_PORT + " y " + MAX_PORT + ".");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El puerto debe ser un número válido.");
        }
    }

    public static boolean validarContrasena(String contrasena) {
        if (contrasena.length() < 8) {
            Popup.show("Error", "La contraseña debe tener al menos 8 caracteres.", Alert.AlertType.ERROR);
            return false;
        }

        if (contrasena.contains(" ") || contrasena.contains("\"") || contrasena.contains("\\")) {
            Popup.show("Error", "La contraseña no debe contener espacios, comillas o \\.", Alert.AlertType.ERROR);
            return false;
        }

        boolean tieneLetra = contrasena.matches(".*[a-zA-Z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");
        boolean tieneSimbolo = contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=~`\\[\\]].*");

        if (!tieneLetra || !tieneNumero || !tieneSimbolo) {
            Popup.show("Error", "La contraseña debe contener al menos una letra, un número y un símbolo.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }
}
