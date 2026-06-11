package com.mycompany.onlineexamproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;

public class StudentResultListController {

    @FXML
    private FlowPane resultContainer;

    @FXML
    public void initialize() {
        loadResults();
    }

    private void loadResults() {

        new Thread(() -> {

            try {

                Socket socket = new Socket("localhost", 2222);

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                dos.writeUTF("GET_ALL_RESULTS");
                dos.writeInt(Session.getStudentId());
                dos.flush();

                while (true) {

                    String title = dis.readUTF();

                    if (title.equals("END")) break;


                    String courseName = dis.readUTF();
                    int examId = dis.readInt();
                    float score = dis.readFloat();

                    Platform.runLater(() -> {

                        VBox card = new VBox();
                        card.getStyleClass().add("course-card");
                        card.setPrefWidth(280); 
                        card.setMinWidth(280);
                        card.setAlignment(Pos.TOP_LEFT);
                        
                        Label courseTitle = new Label(courseName);
                        courseTitle.getStyleClass().add("card-course-title");
                        
                        Label subtitle = new Label(title);
                        subtitle.getStyleClass().add("card-subtitle");
                        
                        Label t = new Label("Score: " + score);
                        t.getStyleClass().add("card-title");
                        t.setWrapText(true);
                        
                        Separator sep = new Separator();

                        Label t2 = new Label("FINISHED");
                        t2.getStyleClass().add("card-title");
                        card.getChildren().addAll(courseTitle, subtitle, t, sep, t2);

                        card.setOnMouseClicked(e -> openDetail(examId));

                        resultContainer.getChildren().add(card);
                    });
                }

                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void openDetail(int examId) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentresultdetail.fxml"));

            Parent root = loader.load();

            StudentresultdetailController controller = loader.getController();

            controller.loadSingleResult(examId);

            Stage stage =  (Stage) resultContainer.getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("studentdashboard.fxml"));

            Stage stage = (Stage) resultContainer.getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}