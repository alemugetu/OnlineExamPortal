package com.mycompany.onleexamproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    
    
    @Override
    public void start(Stage stage) throws Exception{        
        Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
        scene = new Scene(login, 941, 563);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
       launch();
    }

}