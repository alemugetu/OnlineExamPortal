/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.onlineexamproject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class ExamController {

     @FXML private Label lblExamTitle;
    @FXML private Label lblProgress;
    @FXML private Label lblTimer;

    @FXML private Label lblQuestionNumber;
    @FXML private Label lblQuestionText;

    @FXML private RadioButton optA;
    @FXML private RadioButton optB;
    @FXML private RadioButton optC;
    @FXML private RadioButton optD;

    @FXML private ToggleGroup tgOptions;

    private int examId;
    private int currentIndex = 0;

    private final List<Question> questions = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();

    private Timeline timeline;
    private int remainingSeconds;

    // =========================
    // LOAD EXAM
    // =========================
    public void loadExam(int examId) {

        this.examId = examId;

        try (Socket socket = new Socket("localhost", 2222)) {

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("GET_EXAM_QUESTIONS");
            dos.writeInt(examId);
            dos.flush();

           String title = dis.readUTF();
            lblExamTitle.setText(title);

            int timeLimit = dis.readInt();
            remainingSeconds = timeLimit * 60 ;

            startTimer();

           while (true) {

            int questionId = dis.readInt();

            if (questionId == -1)
                break;

            String text = dis.readUTF();

            String a = dis.readUTF();
            String b = dis.readUTF();
            String c = dis.readUTF();
            String d = dis.readUTF();

            questions.add(
                    new Question(
                            questionId,
                            text,
                            a,
                            b,
                            c,
                            d
                    )
            );

            answers.add(null);
        }
                    showQuestion(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SHOW QUESTION
    // =========================
    private void showQuestion(int index) {

        if (index < 0 || index >= questions.size()) return;

        currentIndex = index;

        Question q = questions.get(index);

        lblQuestionNumber.setText("QUESTION " + (index + 1));
        lblQuestionText.setText(q.text);

        lblProgress.setText((index + 1) + "/" + questions.size());

        optA.setText(q.a);
        optB.setText(q.b);
        optC.setText(q.c);
        optD.setText(q.d);

        tgOptions.selectToggle(null);

        String ans = answers.get(index);

        if (ans != null) {

            switch (ans) {

                case "A":
                    optA.setSelected(true);
                    break;

                case "B":
                    optB.setSelected(true);
                    break;

                case "C":
                    optC.setSelected(true);
                    break;

                case "D":
                    optD.setSelected(true);
                    break;
            }
        }
    }

    // =========================
    // NEXT
    // =========================
    @FXML
    private void nextQuestion() {
        saveAnswer();
        if (currentIndex < questions.size() - 1)
            showQuestion(currentIndex + 1);
    }

    // =========================
    // PREVIOUS
    // =========================
    @FXML
    private void previousQuestion() {
        saveAnswer();
        if (currentIndex > 0)
            showQuestion(currentIndex - 1);
    }

    // =========================
    // SAVE ANSWER
    // =========================
    private void saveAnswer() {

    RadioButton selected =
            (RadioButton) tgOptions.getSelectedToggle();

    if (selected == null)
        return;

    String value =
            selected == optA ? "A" :
            selected == optB ? "B" :
            selected == optC ? "C" : "D";

    answers.set(currentIndex, value);

    int questionId =
            questions.get(currentIndex).questionId;

    try (
            Socket socket =
                    new Socket("localhost", 2222)
    ) {

        DataOutputStream dos =
                new DataOutputStream(
                        socket.getOutputStream()
                );

        dos.writeUTF("SAVE_ANSWER");

        dos.writeInt(Session.getStudentId());

        dos.writeInt(examId);

        dos.writeInt(questionId);

        dos.writeUTF(value);

        dos.flush();

    } catch (Exception e) {

        e.printStackTrace();
    }
}
    // =========================
    // TIMER
    // =========================
    private void startTimer() {

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {

                    remainingSeconds--;

                    int min = remainingSeconds / 60;
                    int sec = remainingSeconds % 60;

                    lblTimer.setText(String.format("%02d:%02d", min, sec));

                    if (remainingSeconds <= 0) {
                        timeline.stop();
                        autoSubmit();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // =========================
    // AUTO SUBMIT
    // =========================
   private void autoSubmit() {

    saveAnswer();

    try (
            Socket socket =
                    new Socket("localhost", 2222)
    ) {

        DataOutputStream dos =
                new DataOutputStream(
                        socket.getOutputStream()
                );

        DataInputStream dis =
                new DataInputStream(
                        socket.getInputStream()
                );

        dos.writeUTF("SUBMIT_EXAM");

        dos.writeInt(Session.getStudentId());

        dos.writeInt(examId);

        dos.flush();

        String response = dis.readUTF();

        if (response.equals("SUCCESS")) {

            javafx.application.Platform.runLater(() -> {

                try {

                    Alert alert =
                            new Alert(Alert.AlertType.INFORMATION);

                    alert.setHeaderText(null);

                    alert.setContentText(
                            "Time is up!\nExam submitted automatically."
                    );

                    alert.show();

                    FXMLLoader loader =
                            new FXMLLoader(
                                    getClass().getResource(
                                            "studentdashboard.fxml"
                                    )
                            );

                    Parent root = loader.load();

                    Stage stage =
                            (Stage) lblExamTitle
                                    .getScene()
                                    .getWindow();

                    stage.setScene(new Scene(root));

                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}

    // =========================
    // SUBMIT
    // =========================
    @FXML
private void submitExam() {

    saveAnswer();

    try (
            Socket socket =
                    new Socket("localhost", 2222)
    ) {

        DataOutputStream dos =
                new DataOutputStream(
                        socket.getOutputStream()
                );

        DataInputStream dis =
                new DataInputStream(
                        socket.getInputStream()
                );

        dos.writeUTF("SUBMIT_EXAM");

        dos.writeInt(Session.getStudentId());

        dos.writeInt(examId);

        dos.flush();

        String response = dis.readUTF();

        if(response.equals("SUCCESS")) {

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setHeaderText(null);

            alert.setContentText(
                    "Exam Submitted!"
            );

            alert.showAndWait();

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "studentdashboard.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) lblExamTitle
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));

            stage.show();
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}

    // =========================
    // MODEL
    // =========================
static class Question {

    int questionId;

    String text, a, b, c, d;

    Question(
            int questionId,
            String t,
            String a,
            String b,
            String c,
            String d
    ) {

        this.questionId = questionId;

        this.text = t;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
}