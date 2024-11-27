module codis.whatsapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens codis.whatsapp to javafx.fxml;
    exports codis.whatsapp;
}