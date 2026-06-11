/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onlineexamserver;

/**
 *
 * @author hp
 */
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class ClientHandler extends Thread{

    Socket socket;

    public ClientHandler(Socket socket){

        this.socket = socket;
    }

    @Override
public void run(){

    try{

        DataInputStream dis = new DataInputStream(socket.getInputStream());

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        String request = dis.readUTF();

        

        if(request.equals("STUDENT_LOGIN")) {

            String username = dis.readUTF();
            String password = dis.readUTF();

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM users_student WHERE username=? AND password=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                String status = rs.getString("status");

                if(status.equals("INACTIVE")) {
                    dos.writeUTF("INACTIVE");
                }
                else {
                    int studentId = rs.getInt("student_id");
                    
                    dos.writeUTF("SUCCESS");
                    dos.writeInt(studentId);
                }

            } else {
                dos.writeUTF("FAILED");
            }

            dos.flush();
        }      
        else if(request.equals("STAFF_LOGIN")) {

            String username = dis.readUTF();
            String password = dis.readUTF();

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM userstaff WHERE username=? AND password=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                String status = rs.getString("status");
                int instructor_id = rs.getInt("instructor_id");

                if(status.equals("INACTIVE")) {
                    dos.writeUTF("INACTIVE");
                }
                else {
                    dos.writeUTF("SUCCESS");
                    dos.writeInt(instructor_id);
                }

            } else {
                dos.writeUTF("FAILED");
            }

            dos.flush();
        }
        
        else if(request.equals("GET_COURSES")){

        Connection con = DBConnection.getConnection();

        String sql = "SELECT course_name FROM course";

        PreparedStatement pst = con.prepareStatement(sql);

        ResultSet rs = pst.executeQuery();

        while(rs.next()){
            dos.writeUTF( rs.getString("course_name"));
        }

        dos.writeUTF("END");

        dos.flush();
    }
        else if(request.equals("PUBLISH_EXAM")) {

   //  READ EXAM INFO

   String courseName = dis.readUTF();
   int instructorId = dis.readInt();
   String examTitle  = dis.readUTF();
   String examCode   = dis.readUTF();
   int timeLimit     = dis.readInt();
   int totalMarks    = dis.readInt();
   String examDate   = dis.readUTF();

   Connection con = DBConnection.getConnection();

   //  GET COURSE ID 

   String courseSql =
           "SELECT course_id FROM course WHERE course_name=?";

   PreparedStatement pstCourse =
           con.prepareStatement(courseSql);

   pstCourse.setString(1, courseName);

   ResultSet rsCourse = pstCourse.executeQuery();

   int courseId = 0;

   if(rsCourse.next()) {

       courseId = rsCourse.getInt("course_id");
   }

   // INSERT EXAM

   String examSql =
           "INSERT INTO exams(course_id,instructor_id, title, time_limit, total_marks, exam_date, exam_code) " +
           "VALUES(?,?,?,?,?,?,?)";

   PreparedStatement pstExam =
           con.prepareStatement(examSql,
                   Statement.RETURN_GENERATED_KEYS);

   pstExam.setInt(1, courseId);
   pstExam.setInt(2, instructorId);
   pstExam.setString(3, examTitle);
   pstExam.setInt(4, timeLimit);
   pstExam.setInt(5, totalMarks);
   pstExam.setString(6, examDate);
   pstExam.setString(7, examCode);

   pstExam.executeUpdate();

   // GET GENERATED EXAM ID 

   ResultSet generatedKeys =
           pstExam.getGeneratedKeys();

   int examId = 0;

   if(generatedKeys.next()) {

       examId = generatedKeys.getInt(1);
   }

   // ================= READ QUESTION COUNT =================

   int questionCount = dis.readInt();

   // ================= INSERT QUESTIONS =================

   String questionSql =
           "INSERT INTO questions(" +
           "exam_id, question_text, option_a, option_b, " +
           "option_c, option_d, correct_answer, `mark`) " +
           "VALUES(?,?,?,?,?,?,?,?)";

   PreparedStatement pstQuestion =
           con.prepareStatement(questionSql);

   for(int i = 0; i < questionCount; i++) {

       int qId = dis.readInt();

       String content = dis.readUTF();

       String optionA = dis.readUTF();
       String optionB = dis.readUTF();
       String optionC = dis.readUTF();
       String optionD = dis.readUTF();
       float mark = dis.readFloat();
       String correctAnswer = dis.readUTF();

       pstQuestion.setInt(1, examId);
       pstQuestion.setString(2, content);
       pstQuestion.setString(3, optionA);
       pstQuestion.setString(4, optionB);
       pstQuestion.setString(5, optionC);
       pstQuestion.setString(6, optionD);
       pstQuestion.setString(7, correctAnswer);
       pstQuestion.setFloat(8, mark);

       pstQuestion.executeUpdate();
   }

   dos.writeUTF("SUCCESS");
   dos.flush();
}
        else if(request.equals("GET_EXAMS")){

        String courseName = dis.readUTF();
        int studentId = dis.readInt();

        Connection con = DBConnection.getConnection();

        
        // GET COURSE ID

        String sql1 = "SELECT * FROM course WHERE course_name=?";

        PreparedStatement pst1 =  con.prepareStatement(sql1);

        pst1.setString(1, courseName);

        ResultSet rs1 = pst1.executeQuery();

        if(rs1.next()){

            int courseId = rs1.getInt("course_id");

           
            // GET EXAMS
            String sql2 =
                    "SELECT * FROM exams " +
                    "WHERE course_id=?";

            PreparedStatement pst2 = con.prepareStatement(sql2);

            pst2.setInt(1, courseId);

            ResultSet rs2 = pst2.executeQuery();

            boolean found = false;

            while(rs2.next()){

                found = true;

                String examTitle = rs2.getString("title");

                int examId = rs2.getInt("exam_id");

                Date examDate =  rs2.getDate("exam_date");

                dos.writeUTF(examTitle);

                dos.writeInt(examId);


                // CHECK IF ALREADY SUBMITTED
              
                String sql3 =
                        "SELECT * FROM student_exams " +
                        "WHERE exam_id=? AND student_id=?";

                PreparedStatement pst3 = con.prepareStatement(sql3);

                pst3.setInt(1, examId);

                pst3.setInt(2, studentId);

                ResultSet rs3 = pst3.executeQuery();

               // IF SUBMITTED

                if(rs3.next()) {

                    dos.writeUTF("FINISHED");
                }

               
                // NOT SUBMITTED
                else {

                    java.util.Date currentDate = new java.util.Date();

                    // exam still active
                    if(examDate.after(currentDate)) {

                        dos.writeUTF("AVAILABLE");
                    }

                    // exam missed
                    else {

                        dos.writeUTF("MISSED");
                    }
                }
            }

           
            // NO EXAM
          

            if(!found){

                dos.writeUTF("NO_EXAM");
                dos.writeInt(-1);
            }

            dos.writeUTF("END");
            dos.writeInt(-1);

            dos.flush();
        }
}
        
        else if(request.equals("SAVE_ANSWER")) {

            int studentId = dis.readInt();

            int examId = dis.readInt();

            int questionId = dis.readInt();

            String answer = dis.readUTF();

            Connection con = DBConnection.getConnection();

            // CHECK IF ALREADY ANSWERED
            String checkSql =
                    "SELECT * FROM student_answers " +
                    "WHERE student_id=? " +
                    "AND exam_id=? " +
                    "AND question_id=?";

            PreparedStatement checkPst = con.prepareStatement(checkSql);

            checkPst.setInt(1, studentId);

            checkPst.setInt(2, examId);

            checkPst.setInt(3, questionId);

            ResultSet rs = checkPst.executeQuery();

            // UPDATE
            if(rs.next()) {

                String updateSql =
                        "UPDATE student_answers " +
                        "SET answer=? " +
                        "WHERE student_id=? " +
                        "AND exam_id=? " +
                        "AND question_id=?";

                PreparedStatement updatePst =
                        con.prepareStatement(updateSql);

                updatePst.setString(1, answer);

                updatePst.setInt(2, studentId);

                updatePst.setInt(3, examId);

                updatePst.setInt(4, questionId);

                updatePst.executeUpdate();

            } else {

                // INSERT
                String insertSql =
                        "INSERT INTO student_answers " +
                        "(student_id, exam_id, question_id, answer) " +
                        "VALUES (?, ?, ?, ?)";

                PreparedStatement insertPst =
                        con.prepareStatement(insertSql);

                insertPst.setInt(1, studentId);

                insertPst.setInt(2, examId);

                insertPst.setInt(3, questionId);

                insertPst.setString(4, answer);

                insertPst.executeUpdate();
            }

            dos.writeUTF("SUCCESS");

            dos.flush();
        }
        
        else if(request.equals("GET_RESULT")) {

            int studentId = dis.readInt();

            int examId = dis.readInt();

            Connection con = DBConnection.getConnection();

           
            // RESULT SUMMARY

            String sql =
                    "SELECT e.title, se.score " +
                    "FROM student_exams se " +
                    "JOIN exams e ON se.exam_id=e.exam_id " +
                    "WHERE se.student_id=? " +
                    "AND se.exam_id=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, studentId);

            pst.setInt(2, examId);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                dos.writeUTF(
                        rs.getString("title")
                );

                dos.writeFloat(
                        rs.getFloat("score")
                );

            }

            // QUESTION DETAILS

            String sql2 =
                "SELECT " +
                "q.question_text, " +
                "q.option_a, " +
                "q.option_b, " +
                "q.option_c, " +
                "q.option_d, " +
                "q.correct_answer, " +
                "sa.answer " +
                "FROM student_answers sa " +
                "JOIN questions q " +
                "ON sa.question_id=q.question_id " +
                "WHERE sa.student_id=? " +
                "AND sa.exam_id=?";

            PreparedStatement pst2 =
                    con.prepareStatement(sql2);

            pst2.setInt(1, studentId);

            pst2.setInt(2, examId);

            ResultSet rs2 =
                    pst2.executeQuery();

            while(rs2.next()) {

                dos.writeUTF(rs2.getString("question_text"));

                dos.writeUTF(rs2.getString("option_a"));

                dos.writeUTF(rs2.getString("option_b"));

                dos.writeUTF(rs2.getString("option_c"));

                dos.writeUTF(rs2.getString("option_d"));

                dos.writeUTF(rs2.getString("answer"));

                dos.writeUTF(rs2.getString("correct_answer"));
                            }

            // END
            dos.writeUTF("END");

            dos.flush();
        }
        else if(request.equals("GET_ALL_RESULTS")) {

            int studentId = dis.readInt();
            
            
             Connection con = DBConnection.getConnection();

           String sql =
                "SELECT se.exam_id, se.score, e.title, c.course_name " +
                "FROM student_exams se " +
                "JOIN exams e ON se.exam_id = e.exam_id " +
                "JOIN course c ON e.course_id = c.course_id " +
                "WHERE se.student_id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, studentId);

            ResultSet rs = pst.executeQuery();

            while(rs.next()) {

                dos.writeUTF(rs.getString("title"));
                dos.writeUTF(rs.getString("course_name"));
                dos.writeInt(rs.getInt("exam_id"));
                dos.writeFloat(rs.getFloat("score"));
            }

            dos.writeUTF("END");
            dos.flush();
        }
        
        else if(request.equals("LOAD_INSTRUCTOR_COURSES")) {

    try {

        int instructorId = dis.readInt();

        Connection con = DBConnection.getConnection();

         String sql = """
            SELECT c.course_code, c.course_name
            FROM instructor_course ic
            JOIN course c
            ON ic.course_id = c.course_id
            WHERE ic.instructor_id = ?
            ORDER BY c.course_code
        """;

        PreparedStatement pst = con.prepareStatement(sql);

        pst.setInt(1, instructorId);

        ResultSet rs = pst.executeQuery();
            
        // STORE COURSES FIRST
        ArrayList<String> courses = new ArrayList<>();

        while(rs.next()) {

            String course = rs.getString("course_name");

            courses.add(course);
        }


        // SEND COUNT
        dos.writeInt(courses.size());

        // SEND COURSES
        for(String c : courses) {

            dos.writeUTF(c);
        }

        dos.flush();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
        
        else if(request.equals("VERIFY_EXAM_CODE")) {

            int examId = dis.readInt();
            String code = dis.readUTF();

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT exam_code FROM exams WHERE exam_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, examId);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                String correctCode =
                        rs.getString("exam_code");

                if(correctCode.equals(code)) {

                    dos.writeUTF("SUCCESS");

                } else {

                    dos.writeUTF("FAILED");
                }

            } else {

                dos.writeUTF("FAILED");
            }

            dos.flush();
        }
        
        else if(request.equals("GET_EXAM_QUESTIONS")) {

        int examId = dis.readInt();

        Connection con = DBConnection.getConnection();

        // =========================
        // GET EXAM INFO
        // =========================
        String sql1 =
                "SELECT * FROM exams WHERE exam_id=?";

        PreparedStatement pst1 =
                con.prepareStatement(sql1);

        pst1.setInt(1, examId);

        ResultSet rs1 = pst1.executeQuery();

        if(rs1.next()) {

            String title =
                    rs1.getString("title");

            int timeLimit =
                    rs1.getInt("time_limit");

            dos.writeUTF(title);
            dos.writeInt(timeLimit);

            // =========================
            // GET QUESTIONS
            // =========================

            String sql2 =
                    "SELECT * FROM questions WHERE exam_id=?";

            PreparedStatement pst2 =
                    con.prepareStatement(sql2);

            pst2.setInt(1, examId);

            ResultSet rs2 =
                    pst2.executeQuery();

            boolean found = false;

            while(rs2.next()) {

                found = true;

                int questionId =
                        rs2.getInt("question_id");

                String question =
                        rs2.getString("question_text");

                String a = rs2.getString("option_a");
                String b = rs2.getString("option_b");
                String c = rs2.getString("option_c");
                String d = rs2.getString("option_d");

                // SEND QUESTION DATA
                dos.writeInt(questionId);
                dos.writeUTF(question);
                dos.writeUTF(a);
                dos.writeUTF(b);
                dos.writeUTF(c);
                dos.writeUTF(d);
            }

            // END MARKER
            dos.writeInt(-1);

            if(!found) {
                dos.writeUTF("NO_QUESTIONS");
            }

        } else {
            dos.writeUTF("EXAM_NOT_FOUND");
        }

        dos.flush();
    }
        
       else if(request.equals("SUBMIT_EXAM")) {

            int studentId = dis.readInt();
            int examId = dis.readInt();

            Connection con = DBConnection.getConnection();

            // GET ALL ANSWERS + CORRECT ANSWERS
            String sql =
                "SELECT sa.answer, q.correct_answer, q.mark " +
                "FROM student_answers sa " +
                "JOIN questions q ON sa.question_id = q.question_id " +
                "WHERE sa.student_id=? AND sa.exam_id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, studentId);
            pst.setInt(2, examId);

            ResultSet rs = pst.executeQuery();

            // CALCULATE TOTAL SCORE
            float score = 0;

            while(rs.next()) {

                String studentAnswer = rs.getString("answer");
                String correctAnswer = rs.getString("correct_answer");
                float mark = rs.getFloat("mark");

                if(studentAnswer != null &&
                   studentAnswer.equalsIgnoreCase(correctAnswer)) {

                    score += mark;
                }
            }

            // SAVE SCORE ONLY
            String insertSql =
                "INSERT INTO student_exams (student_id, exam_id, score) VALUES (?, ?, ?)";

            PreparedStatement insertPst = con.prepareStatement(insertSql);
            insertPst.setInt(1, studentId);
            insertPst.setInt(2, examId);
            insertPst.setFloat(3, score);

            insertPst.executeUpdate();

            // RESPONSE
            dos.writeUTF("SUCCESS");
            dos.flush();
        }

        else if(request.equals("LOAD_AVAILABLE_EXAMS")) {

            int instructorId = dis.readInt();

            Connection con =
                    DBConnection.getConnection();

            // GET EXAMS CREATED BY INSTRUCTOR
            
            String sql =
                    "SELECT title " +
                    "FROM exams " +
                    "WHERE instructor_id=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, instructorId);

            ResultSet rs = pst.executeQuery();

            // =====================================
            // STORE EXAMS
            // =====================================

            ArrayList<String> exams =
                    new ArrayList<>();

            while(rs.next()) {

                exams.add(rs.getString("title"));
            }

            // =====================================
            // SEND COUNT
            // =====================================

            dos.writeInt(exams.size());

            // =====================================
            // SEND EXAMS
            // =====================================

            for(String exam : exams) {

                dos.writeUTF(exam);
            }

            dos.flush();
        }
        
        else if(request.equals("LOAD_EXAM_RESULTS")) {

            String examTitle = dis.readUTF();

            Connection con =
                    DBConnection.getConnection();

            // =====================================
            // GET STUDENTS + SCORE
            // =====================================

            String sql =
                "SELECT s.full_name, s.student_id, se.score " +
                "FROM student_exams se " +
                "JOIN students s " +
                "ON se.student_id = s.student_id " +
                "JOIN exams e " +
                "ON se.exam_id = e.exam_id " +
                "WHERE e.title=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(1, examTitle);

            ResultSet rs =
                    pst.executeQuery();

            // =====================================
            // STORE RESULTS
            // =====================================

            ArrayList<String[]> results =
                    new ArrayList<>();

            while(rs.next()) {

                String studentName =
                        rs.getString("full_name");

                String studentId =
                        rs.getString("student_id");

                String score =
                        rs.getString("score");

                results.add(new String[] {
                        studentName,
                        studentId,
                        score
                });
            }

            // =====================================
            // SEND COUNT
            // =====================================

            dos.writeInt(results.size());

            // =====================================
            // SEND DATA
            // =====================================

            for(String[] r : results) {

                dos.writeUTF(r[0]); // name

                dos.writeUTF(r[1]); // id

                dos.writeUTF(r[2]); // score
            }

            dos.flush();
        }
        
        else if(request.equals("LOAD_MANAGE_EXAMS")) {

            int instructorId = dis.readInt();

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT exam_id, title, course_id, exam_date " +
                    "FROM exams " +
                    "WHERE instructor_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, instructorId);

            ResultSet rs = pst.executeQuery();

            // count rows first
            java.util.ArrayList<String[]> exams =
                    new java.util.ArrayList<>();

            while(rs.next()) {

                String[] row = {

                    String.valueOf(rs.getInt("exam_id")),

                    rs.getString("title"),

                    String.valueOf(rs.getInt("course_id")),

                    rs.getString("exam_date")
                };

                exams.add(row);
            }

            // SEND COUNT
            dos.writeInt(exams.size());

            // SEND DATA
            for(String[] row : exams) {

                dos.writeInt(Integer.parseInt(row[0]));

                dos.writeUTF(row[1]);

                dos.writeInt(Integer.parseInt(row[2]));

                dos.writeUTF(row[3]);
            }

            dos.flush();
        }


    }catch(Exception e){

        e.printStackTrace();
    }
}
}













