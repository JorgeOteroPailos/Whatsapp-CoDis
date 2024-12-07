package codis.whatsapp.GUI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PantallaCarga {
    private Stage stage;

    public void mostrarPantalla() {
        Platform.runLater(() -> {
            stage = new Stage();
            StackPane root = new StackPane(new ProgressIndicator());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Conectando movidas...");
            stage.show();
        });
    }

    public void cerrarPantalla() {
        Platform.runLater(() -> {
            if (stage != null) {
                stage.close();
            }
        });
    }
}
