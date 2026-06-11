package com.mycompany.onlineexamproject;

import shared.Requests;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentdashboardController implements Initializable {

    @FXML
    private FlowPane courseContainer; // Changed from VBox to FlowPane to match new FXML

    @FXML
    private MenuItem  logoutButton;
    
    @FXML
    private MenuButton studentMenu;

    @FXML
    private void groupMember(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("groupMember.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Group Members");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    @FXML
    private void handleAvailableCourses(ActionEvent event) {
        // 1. Get the parent of the courseContainer (The VBox holding the content)
        VBox parent = (VBox) courseContainer.getParent();

        // 2. Remove any "Detail Views" that were added dynamically
    
        parent.getChildren().removeIf(node -> node != courseContainer);

        // 3. Make the grid visible again
        courseContainer.setVisible(true);
        courseContainer.setManaged(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread t = new Thread(() -> {
            loadCourses();
        });

        t.start();
        
        studentMenu.setText(Session.getStudentName());
        
        
    }

    private void loadCourses() {
        try {
            Socket socket = new Socket("localhost", 2222);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF(Requests.GET_COURSES);
            dos.flush();

            while (true) {
                String courseName = dis.readUTF();
                if (courseName.equals("END")) break;

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    VBox card = createCourseCard(courseName);
                    courseContainer.getChildren().add(card);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     
    @FXML
    private void handleMyResults(ActionEvent event) {

      try {

          FXMLLoader loader = new FXMLLoader(getClass().getResource("studentresult.fxml"));

          Parent root = loader.load();

          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

          stage.setScene(new Scene(root));

      } catch (Exception e) {
          e.printStackTrace();
      }
    }
    @FXML
    private void handleLogout(ActionEvent event) {

        Alert a = new Alert( Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES,ButtonType.NO);

        a.setHeaderText(null);
        a.setTitle("Logout");

        Optional<ButtonType> result = a.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {

            Session.clearSession();

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));

                Parent root = loader.load();

                MenuItem item = (MenuItem) event.getSource();

                Stage stage = (Stage) item.getParentPopup().getOwnerWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

private void showCourseDetail(String courseName) {

    // 1. Create the Detail Container
    VBox detailView = new VBox(20);
    detailView.getStyleClass().add("main-content");
    detailView.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));

    // 2. Back Button
    Button backBtn = new Button("Back to Dashboard");
    backBtn.getStyleClass().add("nav-button-back");

    backBtn.setOnAction(e -> {

        courseContainer.setVisible(true);
        courseContainer.setManaged(true);

        VBox parent = (VBox) courseContainer.getParent();

        parent.getChildren().remove(detailView);
    });

    // 3. Course Title
    Label title = new Label(courseName);
    title.getStyleClass().add("show-title");

    // 4. Grid
    FlowPane optionsGrid = new FlowPane(20, 20);


    try {

        Socket socket = new Socket("localhost", 2222);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        DataInputStream dis = new DataInputStream(socket.getInputStream());

        
        dos.writeUTF("GET_EXAMS");

        dos.writeUTF(courseName);
        
        int studentId = Session.getStudentId();
        dos.writeInt(studentId);

        dos.flush();

        // receive exams
        while (true) {

            String examTitle = dis.readUTF();
            int examId = dis.readInt();

            // stop
            if (examTitle.equals("END")) {

                break;
            }

            // no exam
            if (examTitle.equals("NO_EXAM")) {

                Label noExam = new Label("No Exam");

                optionsGrid.getChildren().add(noExam);

                break;
            }

            // get status
            String status = dis.readUTF();

            VBox examCard = null;

            // finished
            if (status.equals("FINISHED")) {

                examCard = createOptionCard(examTitle,"Exam Finished");

                examCard.setOnMouseClicked(e -> {

                    try {

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("studentresultdetail.fxml"));

                        Parent root = loader.load();

                        StudentresultdetailController controller = loader.getController();

                        controller.loadSingleResult(examId);

                        Stage stage = (Stage) courseContainer.getScene().getWindow();

                        stage.setScene(new Scene(root));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }         // available
            else if(status.equals("AVAILABLE")) {

                examCard = createOptionCard(examTitle, "Start Exam");

                examCard.setOnMouseClicked(e -> {

                    TextInputDialog dialog = new TextInputDialog();

                    dialog.setTitle("Exam Verification");

                    dialog.setHeaderText(null);

                    dialog.setContentText("Enter Exam Code:");

                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(code -> {

                        verifyExamCode(examId,code);
                    });
                });
            }else if(status.equals("MISSED")) {

                    examCard = createOptionCard(examTitle,"Missed Exam");

                    examCard.setDisable(true);
            }
            
            if(examCard != null) {

                optionsGrid.getChildren().add(examCard);
            }
        }

        socket.close();

    } catch (Exception e) {

        e.printStackTrace();
    }

    // 5. Build View
    detailView.getChildren().addAll(
            backBtn,
            title,
            new Separator(),
            optionsGrid
    );

    // 6. Switch Visibility
    VBox parent = (VBox) courseContainer.getParent();

    courseContainer.setVisible(false);

    courseContainer.setManaged(false);

    parent.getChildren().add(detailView);
}

private VBox createOptionCard(String title, String status) {
    VBox card = new VBox(10);
    card.getStyleClass().add("course-card"); // Reuse your card CSS
    card.setPrefWidth(200);
    card.setAlignment(Pos.CENTER);
    
    Label lblTitle = new Label(title);
    lblTitle.getStyleClass().add("card-title");
    
    Label lblStatus = new Label(status);
    lblStatus.getStyleClass().add("card-subtitle");
    
    card.getChildren().addAll(lblTitle, lblStatus);
    return card;
}

private VBox createCourseCard(String courseName) {
    
    VBox card = new VBox(10); 
    card.getStyleClass().add("course-card");
    card.setPrefWidth(280); // Modern fixed width for grid look
    card.setMinWidth(280);
    card.setAlignment(Pos.TOP_LEFT);

    Label subtitle = new Label("COURSE MODULE");
    subtitle.getStyleClass().add("card-subtitle");

    Label title = new Label(courseName);
    title.getStyleClass().add("card-title");
    title.setWrapText(true);

    Separator sep = new Separator();

    Button link = new Button("Go to Exam ");
    link.getStyleClass().add("nav-button-back"); // Style this in CSS
    
    link.setOnAction(e -> showCourseDetail(courseName));

    card.getChildren().addAll(subtitle, title, sep, link);
    return card;
}

private void verifyExamCode(int examId, String code) {

try {

    Socket socket = new Socket("localhost", 2222);

    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

    DataInputStream dis = new DataInputStream(socket.getInputStream());

    // SEND REQUEST
    dos.writeUTF("VERIFY_EXAM_CODE");

    dos.writeInt(examId);

    dos.writeUTF(code);

    dos.flush();

    // SERVER RESPONSE
    String response = dis.readUTF();

    if(response.equals("SUCCESS")) {

        openExamScene(examId);

    } else {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(null);

        alert.setContentText("Invalid Exam Code");

        alert.showAndWait();
    }

    socket.close();

} catch(Exception e) {

    e.printStackTrace();
}
}


private void openExamScene(int examId) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("exam.fxml"));

            Parent root = loader.load();

            ExamController controller = loader.getController();

            controller.loadExam(examId);

            Stage stage = (Stage) courseContainer.getScene().getWindow();

            stage.setScene(new Scene(root));

            stage.show();

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}