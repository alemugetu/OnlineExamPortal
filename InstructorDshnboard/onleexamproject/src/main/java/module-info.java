module com.mycompany.onleexamproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens com.mycompany.onleexamproject to javafx.fxml;
    exports com.mycompany.onleexamproject;
}
