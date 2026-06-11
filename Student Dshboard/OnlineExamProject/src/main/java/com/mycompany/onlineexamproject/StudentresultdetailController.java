/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.onlineexamproject;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentresultdetailController {

    @FXML 
    private Label lblExamTitle;
    @FXML 
    private Label lblStudentId;
    @FXML 
    private Label lblScore;
    @FXML 
    private Label lblStatus;
    @FXML
    private VBox questionContainer;

    public void loadSingleResult(int examId) {

    try {

        Socket socket = new Socket("localhost", 2222);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF("GET_RESULT");

        dos.writeInt(Session.getStudentId());

        dos.writeInt(examId);

        dos.flush();

        // =========================
        // SUMMARY
        // =========================

        lblExamTitle.setText(
                dis.readUTF()
        );

        lblScore.setText(
                String.valueOf(
                        dis.readFloat()
                )
        );

        lblStatus.setText("SUBMITED");

        lblStudentId.setText(
                String.valueOf(
                        Session.getStudentId()
                )
        );

        // =========================
        // QUESTIONS
        // =========================

        int number = 1;

        while(true) {

            String question =
                    dis.readUTF();

            if(question.equals("END"))
                break;

            String optionA = dis.readUTF();

            String optionB = dis.readUTF();

            String optionC = dis.readUTF();

            String optionD = dis.readUTF();

            String studentAnswer = dis.readUTF();

            String correctAnswer = dis.readUTF();

            VBox card = new VBox(10);

            card.getStyleClass().add("question-card");

            Label qLabel = new Label(number + ". " + question);

            qLabel.getStyleClass().add("question-title");

            Label yourAnswer = new Label("Your Answer: " +studentAnswer);

            if(studentAnswer.equals(correctAnswer)) {

                yourAnswer.getStyleClass()
                        .add("correct-answer");

            } else {

                yourAnswer.getStyleClass()
                        .add("wrong-answer");
            }

            Label correct =
                    new Label(
                            "Correct Answer: " +
                            correctAnswer
                    );

            correct.getStyleClass()
                    .add("correct-answer");
            
            Label a = new Label("A. " + optionA);

            Label b = new Label("B. " + optionB);

            Label c = new Label("C. " + optionC);

            Label d = new Label("D. " + optionD);

            card.getChildren()
        .addAll(
                qLabel,
                a,
                b,
                c,
                d,
                yourAnswer,
                correct
        );
            questionContainer
                    .getChildren()
                    .add(card);

            number++;
        }

        socket.close();

    } catch (Exception e) {

        e.printStackTrace();
    }
}

    @FXML
    private void handleBack() {
        try {

            Parent root =
                    FXMLLoader.load(
                            getClass().getResource("studentresult.fxml")
                    );

            Stage stage =
                    (Stage) lblScore.getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}