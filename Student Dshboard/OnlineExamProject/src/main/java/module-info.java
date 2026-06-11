module com.mycompany.onlineexamproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.mycompany.onlineexamproject to javafx.fxml;
    exports com.mycompany.onlineexamproject;
}
