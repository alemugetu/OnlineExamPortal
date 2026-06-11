/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.onleexamproject;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;



public class Instructor_dashboardController implements Initializable {
    
    @FXML
    private MenuItem  logoutButton;
    
    @FXML
    private MenuButton staffMenu;
    
    

    // ---------------- Tabs ----------------
    @FXML private TabPane mainTabPane;

    @FXML
    private Tab createExamTab;
    
    @FXML
    private Tab resultsTab;

    //  Create Exam tab 
    @FXML private ComboBox<String> courseComboBox;
    @FXML private TextField txtExamTitle;
    @FXML private TextField txtExamCode;
    @FXML private TextField txtTimeLimit;
    @FXML private TextField txtTotalMarks;
    @FXML private DatePicker examDatePicker;

    // Add-question section
    @FXML private TextField txtQuestion;
    @FXML private TextField txtOptionA;
    @FXML private TextField txtOptionB;
    @FXML private TextField txtOptionC;
    @FXML private TextField txtOptionD;
    @FXML private TextField txtQnMark;
    @FXML private ComboBox<String> correctAnswerComboBox;

    // Questions table
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, Integer> colQId;
    @FXML private TableColumn<Question, String>  colQContent;
    @FXML private TableColumn<Question, String>  colQAnswer;
    @FXML private TableColumn<Question, Void>    colQAction;
    
    @FXML
    private ListView<String> examListView;

    @FXML
    private Label lblSelectedExam;

    @FXML
    private TableView<ResultModel> resultTable;

    @FXML
    private TableColumn<ResultModel, String> colStudentName;

    @FXML
    private TableColumn<ResultModel, String> colStudentId;

    @FXML
    private TableColumn<ResultModel, String> colStudentScore;

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
    @FXML
    private void groupMember(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("groupMember.fxml")
            );

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
    private void goToCreateExam() {
        mainTabPane.getSelectionModel().select(createExamTab);
    }

    @FXML
    private void goToResults() {
        mainTabPane.getSelectionModel().select(resultsTab);
    }

    // The list that backs the questions table
    private final ObservableList<Question> questions = FXCollections.observableArrayList();
    private int nextQuestionId = 1;
// =================================================================
    // INITIALIZE
    // =================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setupCorrectAnswerCombo();
        setupQuestionTable();
        setupResultTable();

        loadAvailableExams();

        examListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, selectedExam) -> {

            if(selectedExam != null) {

                lblSelectedExam.setText(selectedExam);

                loadStudentsForExam(selectedExam);
            }
        });
        
        staffMenu.setText(Session.getCurrentUserName());

        new Thread(() -> {

        loadCourses();
        //loadManageExams();
        //loadResults();

    }).start();
    }

  
    // =================================================================
    // COURSES
    // =================================================================
   private void loadCourses() {

    ObservableList<String> courses = FXCollections.observableArrayList();

    try {

        Socket socket = new Socket(Session.getIp(), 2222);

        DataOutputStream dos =
                new DataOutputStream(socket.getOutputStream());

        DataInputStream dis =
                new DataInputStream(socket.getInputStream());

        int instructorId = Session.getCurrentUserID();

        // SEND REQUEST
        dos.writeUTF("LOAD_INSTRUCTOR_COURSES");
        dos.writeInt(instructorId);

        dos.flush();

        // RECEIVE COURSE COUNT
        int count = dis.readInt();

        // RECEIVE COURSES
        for (int i = 0; i < count; i++) {

            String course = dis.readUTF();

            courses.add(course);
        }

        courseComboBox.setItems(courses);

        dis.close();
        dos.close();
        socket.close();

    } catch (Exception e) {

        e.printStackTrace();

        showError("Failed to load assigned courses.");
    }
}
   
    private void setupCorrectAnswerCombo() {
        ObservableList<String> opts = FXCollections.observableArrayList("A", "B", "C", "D");
        correctAnswerComboBox.setItems(opts);
    }

    // =================================================================
    // DASHBOARD STATS
    // =================================================================
    

    // =================================================================
    // QUESTIONS TABLE
    // =================================================================
    private void setupQuestionTable() {
        colQId.setCellValueFactory(new PropertyValueFactory<Question, Integer>("id"));
        colQContent.setCellValueFactory(new PropertyValueFactory<Question, String>("content"));
        colQAnswer.setCellValueFactory(new PropertyValueFactory<Question, String>("correctAnswer"));

        // Add a "Remove" button to every row in the Action column
        colQAction.setCellFactory(new javafx.util.Callback<TableColumn<Question, Void>, TableCell<Question, Void>>() {
            @Override
            public TableCell<Question, Void> call(TableColumn<Question, Void> param) {
                final TableCell<Question, Void> cell = new TableCell<Question, Void>() {
                    private final Button removeBtn = new Button("Remove");
                    {
                        removeBtn.getStyleClass().add("ghost-btn");
                        removeBtn.setOnAction(e -> {
                            Question q = getTableView().getItems().get(getIndex());
                            questions.remove(q);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(removeBtn);
                        }
                    }
                };
                return cell;
            }
        });

        questionTable.setItems(questions);
    }

    // =================================================================
    // BUTTON HANDLERS - Create Exam
    // =================================================================
    @FXML
    private void handleAddQuestion() {
        String text = txtQuestion.getText();
        String a = txtOptionA.getText();
        String b = txtOptionB.getText();
        String c = txtOptionC.getText();
        String d = txtOptionD.getText();
        float m = Float.parseFloat(txtQnMark.getText());
        String correct = correctAnswerComboBox.getValue();
      if (text == null || text.trim().isEmpty()) {
            showError("Please type the question text.");
            return;
        }
        if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            showError("Please fill in all four options.");
            return;
        }
        if (correct == null) {
            showError("Please select the correct answer.");
            return;
        }

        Question q = new Question(nextQuestionId, text, a, b, c, d, m, correct);
        questions.add(q);
        nextQuestionId = nextQuestionId + 1;

        handleClearQuestion();
    }

    @FXML
    private void handleClearQuestion() {
        txtQuestion.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        correctAnswerComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearForm() {
        courseComboBox.getSelectionModel().clearSelection();
        txtExamTitle.clear();
        txtExamCode.clear();
        txtTimeLimit.clear();
        txtTotalMarks.clear();
        examDatePicker.setValue(null);
        questions.clear();
        nextQuestionId = 1;
        handleClearQuestion();
    }

    @FXML
private void handlePublishExam() {

    // ================= VALIDATION =================

    String course = courseComboBox.getValue();
    String title  = txtExamTitle.getText();
    String code  = txtExamCode.getText();
    String time   = txtTimeLimit.getText();
    String marks  = txtTotalMarks.getText();

    if (course == null) {
        showError("Please select a course.");
        return;
    }

    if (title == null || title.trim().isEmpty()) {
        showError("Please enter exam title.");
        return;
    }
    
    if (code == null || code.trim().isEmpty()) {
        showError("Please enter exam code.");
        return;
    }

    if (!isPositiveInteger(time)) {
        showError("Invalid time limit.");
        return;
    }

    if (!isPositiveInteger(marks)) {
        showError("Invalid total marks.");
        return;
    }

    if (examDatePicker.getValue() == null) {
        showError("Please select exam date.");
        return;
    }

    if (questions.isEmpty()) {
        showError("Add at least one question.");
        return;
    }

    // ================= SOCKET SEND =================

    try {

        Socket socket = new Socket(Session.getIp(), 2222);

        // OUTPUT STREAM
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        int instructorId = Session.getCurrentUserID();
        // ================= SEND EXAM INFO =================
        
        dos.writeUTF("PUBLISH_EXAM");
        
        dos.writeUTF(course);
        dos.writeInt(instructorId);
        dos.writeUTF(title);
        dos.writeUTF(code);
        dos.writeInt(Integer.parseInt(time));
        dos.writeInt(Integer.parseInt(marks));
        dos.writeUTF(examDatePicker.getValue().toString());

        // ================= SEND QUESTION COUNT =================

        dos.writeInt(questions.size());

        // ================= SEND QUESTIONS =================

        for (Question q : questions) {

            dos.writeInt(q.getId());

            dos.writeUTF(q.getContent());

            dos.writeUTF(q.getOptionA());
            dos.writeUTF(q.getOptionB());
            dos.writeUTF(q.getOptionC());
            dos.writeUTF(q.getOptionD());
            dos.writeFloat(q.getQnMark());
            dos.writeUTF(q.getCorrectAnswer());
        }

        dos.flush();

        String response = dis.readUTF();

        if(response.equals("SUCCESS")) {

            showInfo("Exam Published Successfully!");

        } else {

            showError("Failed to publish exam.");
        }
        // CLOSE CONNECTION
        dos.close();
        socket.close();

        // CLEAR FORM
        handleClearForm();

    } catch (Exception e) {

        e.printStackTrace();

        showError("Failed to connect to server.");
    }
}

    

    @FXML
    private void handleSidebarResults() {
        showInfo("View Results screen will be added in the next module.");
    }

    

    // =================================================================
    // HELPERS
    // =================================================================
    private boolean isPositiveInteger(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            int n = Integer.parseInt(s.trim());
            return n > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Error");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Information");
        a.showAndWait();
    }

    // =================================================================
    // SIMPLE MODEL CLASSES
    // (PropertyValueFactory needs public getters that match the names)
    // =================================================================
    public static class Question {
        private final int id;
        private final String content;
        private final String optionA;
        private final String optionB;
        private final String optionC;
        private final String optionD;
        private final float mark;
        private final String correctAnswer;

        public Question(int id, String content, String a, String b, String c, String d, float m, String correctAnswer) {
            this.id = id;
            this.content = content;
            this.optionA = a;
            this.optionB = b;
            this.optionC = c;
            this.optionD = d;
            this.mark = m;
            this.correctAnswer = correctAnswer;
        }

        public int getId()             { return id; }
        public String getContent()     { return content; }
        public String getOptionA()     { return optionA; }
        public String getOptionB()     { return optionB; }
        public String getOptionC()     { return optionC; }
        public String getOptionD()     { return optionD; }
        public float getQnMark()     { return mark; }
        public String getCorrectAnswer(){ return correctAnswer; }
    }

    public static class ExamRecord {
        private final String exam;
        private final String course;
        private final String date;
        
        public ExamRecord(String exam, String course, String date) {
            this.exam = exam;
            this.course = course;
            this.date = date;
        }
    public String getExam()   { return exam; }
        public String getCourse() { return course; }
        public String getDate()   { return date; }
    }
    
    public static class ResultModel {

    private final String studentName;
    private final String studentId;
    private final String score;

    public ResultModel(String studentName,
                       String studentId,
                       String score) {

        this.studentName = studentName;
        this.studentId = studentId;
        this.score = score;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getScore() {
        return score;
    }
}
    
     
      private void setupResultTable() {

    colStudentName.setCellValueFactory(
            new PropertyValueFactory<>("studentName"));

    colStudentId.setCellValueFactory(
            new PropertyValueFactory<>("studentId"));

    colStudentScore.setCellValueFactory(
            new PropertyValueFactory<>("score"));

    resultTable.setItems(resultList);
}
 
    private final ObservableList<ResultModel> resultList =
        FXCollections.observableArrayList();

@FXML
private void loadAvailableExams() {

    ObservableList<String> exams = FXCollections.observableArrayList();

    try {

        Socket socket =
                new Socket(Session.getIp(), 2222);

        DataOutputStream dos =
                new DataOutputStream(socket.getOutputStream());

        DataInputStream dis =
                new DataInputStream(socket.getInputStream());

        dos.writeUTF("LOAD_AVAILABLE_EXAMS");

        dos.writeInt(Session.getCurrentUserID());

        dos.flush();

        int count = dis.readInt();

        for(int i = 0; i < count; i++) {

            exams.add(dis.readUTF());
        }

        examListView.setItems(exams);

        dis.close();
        dos.close();
        socket.close();

    } catch(Exception e) {

        e.printStackTrace();

        showError("Failed to load exams.");
    }
}

private void loadStudentsForExam(String examTitle) {

    resultList.clear();

    try {

        Socket socket =
                new Socket(Session.getIp(), 2222);

        DataOutputStream dos =
                new DataOutputStream(socket.getOutputStream());

        DataInputStream dis =
                new DataInputStream(socket.getInputStream());

        dos.writeUTF("LOAD_EXAM_RESULTS");

        dos.writeUTF(examTitle);

        dos.flush();

        int count = dis.readInt();

        for(int i = 0; i < count; i++) {

            String studentName = dis.readUTF();

            String studentId = dis.readUTF();

            String score = dis.readUTF();

            resultList.add(

                new ResultModel(
                        studentName,
                        studentId,
                        score
                )
            );
        }

        dis.close();
        dos.close();
        socket.close();

    } catch(Exception e) {

        e.printStackTrace();

        showError("Failed to load exam results.");
    }
}

public static class ResultRecord {

    private final String student;
    private final String exam;
    private final String course;
    private final String score;

    public ResultRecord(String student,
                        String exam,
                        String course,
                        String score) {

        this.student = student;
        this.exam = exam;
        this.course = course;
        this.score = score;
    }

    public String getStudent() { return student; }

    public String getExam() { return exam; }

    public String getCourse() { return course; }

    public String getScore() { return score; }
}
}