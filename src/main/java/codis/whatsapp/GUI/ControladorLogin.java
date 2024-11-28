package codis.whatsapp.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ControladorLogin {
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
    @FXML
    private Button botonIniciarSesion;

    @FXML
    public void iniciarSesion(){
        textoVentana.setText("Inicio de sesión");
        etiquetaContrasena.setText("Contraseña");
        etiquetaNombreUsuario.setText("Nombre de usuario");
        //TODO
    }

    public void crearCuenta(){
        textoVentana.setText("Creación de cuenta");
        etiquetaContrasena.setText("Nueva contraseña");
        etiquetaNombreUsuario.setText("Nuevo nombre de usuario");
        //TODO
    }

}