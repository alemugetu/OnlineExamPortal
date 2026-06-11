package com.mycompany.onlineexamproject;

//import static com.mycompany.onlineexamproject.App.getConnection;
import java.io.*;
import java.net.Socket;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
//import shared.Requests;


public class LoginController {

    @FXML
    private VBox card;

    @FXML
    private ImageView logo;
    
    @FXML
    private Label err;
    
    @FXML
    private PasswordField pw;

    @FXML
    private TextField un;

@FXML
void loginbtn(ActionEvent event) {

    String username = un.getText();
    String password = pw.getText();

    if(username.isEmpty() || password.isEmpty()){
        err.setText("Fill all fields");
        return;
    }

    try {

        Socket socket = new Socket("localhost", 2222);

        DataOutputStream dos =
                new DataOutputStream(socket.getOutputStream());

        DataInputStream dis =
                new DataInputStream(socket.getInputStream());

        dos.writeUTF("STUDENT_LOGIN");

        dos.writeUTF(username);
        dos.writeUTF(password);

        dos.flush();

        String response = dis.readUTF();

        if(response.equals("SUCCESS")){
            int studentId = dis.readInt();
            Session.setStudentId(studentId);
            Session.setStudentName(username);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("studentdashboard.fxml")));

            stage.setScene(scene);
            stage.setTitle("Student Dashboard");
            stage.show();

        }
        else if(response.equals("INACTIVE")){
            err.setText("Account is inactive");
        }
        else{
            err.setText("Invalid username or password");
        }

        socket.close();

    } catch(Exception e){
        err.setText("Server error");
    }
}
}




