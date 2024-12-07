package codis.whatsapp.GUI;

import codis.whatsapp.Aplicacion.Cliente;
import codis.whatsapp.Aplicacion.IServidor;
import codis.whatsapp.Aplicacion.Usuario;
import codis.whatsapp.MainCliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

import static codis.whatsapp.Aplicacion.Utils.*;

public class ControladorLogin {

    // Constantes para valores predeterminados y rangos válidos
    private static final String DEFAULT_IP = "localhost";
    private static final String DEFAULT_SERVER_IP = "localhost";
    private static final int DEFAULT_PORT = 8081;
    private static final int DEFAULT_SERVER_PORT = 9090;



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
        campoPuertoServidor.setText(String.valueOf(DEFAULT_SERVER_PORT));
        campoPuerto.setText(String.valueOf(DEFAULT_PORT));
        campoIP.setText(DEFAULT_IP);
        campoIPServidor.setText(DEFAULT_SERVER_IP);
    }

    @FXML
    public void mostrarAjustes() {
        boolean visible = seccionAjustes.isVisible();
        seccionAjustes.setVisible(!visible);
        seccionAjustes.setManaged(!visible);
    }

    @FXML
    public void iniciarSesion(ActionEvent event) {

        abrirAplicacion(event);
    }

    private void abrirAplicacion(ActionEvent event) {
        try {
            // Cargar la vista principal
            debugPrint("Iniciando cambio de ventana");
            FXMLLoader loader = new FXMLLoader(MainCliente.class.getResource("principal.fxml"));
            Parent root = loader.load();
            debugPrint("FXML cargado");

            ControladorPrincipal controladorPrincipal = loader.getController();

            Runnable tareaCrearCliente = () -> {
                PantallaCarga pantallaCarga = new PantallaCarga();
                try{

                    Platform.runLater(pantallaCarga::mostrarPantalla);
                    Cliente cliente = new Cliente(new Usuario(getNombre()),getIP(),getPuerto(),controladorPrincipal,getPuertoServidor(),getIpServidor(), getContrasenaSinValidar());
                    controladorPrincipal.setCliente(cliente);
                }catch (Exception e){
                    System.err.println("Error en la creación del cliente: "+e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(()->{
                        Platform.runLater(pantallaCarga::cerrarPantalla);
                        Popup.show("Error","Error en la creación del cliente: "+e.getMessage(), Alert.AlertType.ERROR);
                    });
                    return;
                }
                Platform.runLater(()->{
                    pantallaCarga.cerrarPantalla();
                    Stage nuevaVentana = new Stage();
                    nuevaVentana.setTitle("Aplicación Principal");
                    nuevaVentana.setScene(new Scene(root, 800, 600));
                    nuevaVentana.show();

                    // Cerrar la ventana actual
                    ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                });

            };
            Thread hilo= new Thread(tareaCrearCliente);
            hilo.start();


        } catch (IOException e) {
            System.err.println("Error al abrir la ventana principal: " + e.getMessage());
        }
    }

    public void crearCuenta(){
        PantallaCarga pantallaCarga=new PantallaCarga();
        pantallaCarga.mostrarPantalla();
        Runnable accion = () -> {
            try {
                IServidor servidor = Cliente.inicializarServidor(getPuertoServidor(), getIpServidor());
                servidor.registrarse(getNombre(), getContrasena());
                debugPrint("Cuenta creada correctamente (sin excepciones, al menos");
                Platform.runLater(() -> {
                    Popup.show("Cuenta creada", "Usuario registrado correctamente", Alert.AlertType.CONFIRMATION);
                    pantallaCarga.cerrarPantalla();
                });
            }catch (IllegalArgumentException e){
                System.err.println("Argumento incorrecto: "+e.getMessage());
                Platform.runLater(()->Popup.show("Error","El nombre de usuario no puede estar vacío", Alert.AlertType.ERROR));
            } catch (Exception e) {
                System.err.println("Error al crear la cuenta: "+e.getMessage());
                Platform.runLater(()-> {
                    Popup.show("Error","Error al crear la cuenta: "+e.getMessage(), Alert.AlertType.ERROR);
                    pantallaCarga.cerrarPantalla();
                });
            }
        };
        new Thread(accion).start();

    }

    private String getIpServidor() throws IllegalArgumentException{
        String ip = campoIPServidor.getText().isEmpty() ? DEFAULT_SERVER_IP : campoIPServidor.getText();
        validarIP(ip);
        return ip;
    }

    private int getPuertoServidor() throws IllegalArgumentException{
        return campoPuertoServidor.getText().isEmpty() ? DEFAULT_SERVER_PORT : validarPuerto(campoPuertoServidor.getText());
    }

    private String getContrasena() throws IllegalArgumentException{
        String contrasena=cuadroTextoContrasena.getText();
        validarContrasena(contrasena);
        return contrasena;
    }

    private String getContrasenaSinValidar(){
        return cuadroTextoContrasena.getText();
    }

    private int getPuerto() throws IllegalArgumentException{
        return campoPuerto.getText().isEmpty() ? DEFAULT_PORT : validarPuerto(campoPuerto.getText());
    }

    private String getNombre(){
        if (cuadroTextoNombreUsuario.getText().isEmpty()){
             throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        return cuadroTextoNombreUsuario.getText();
    }

    private String getIP(){
        String IP=campoIP.getText().isEmpty() ? DEFAULT_IP : campoIP.getText();
        validarIP(IP);
        return IP;
    }




}
