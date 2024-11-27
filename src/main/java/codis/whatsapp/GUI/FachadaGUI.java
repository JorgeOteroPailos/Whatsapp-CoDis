package codis.whatsapp.cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FachadaGUI {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}