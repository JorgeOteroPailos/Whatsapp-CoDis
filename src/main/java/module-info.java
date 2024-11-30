module codis.whatsapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens codis.whatsapp to javafx.fxml;
    exports codis.whatsapp.GUI;
    opens codis.whatsapp.GUI to javafx.fxml;
    exports codis.whatsapp.Aplicacion;
    opens codis.whatsapp.Aplicacion to javafx.fxml;
    exports codis.whatsapp;
}