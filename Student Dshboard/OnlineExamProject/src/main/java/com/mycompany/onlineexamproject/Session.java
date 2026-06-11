/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onlineexamproject;

/**
 *
 * @author hp
 */
public class Session {

    
    private static int studentId;
    private static String studentName;

    public static void setStudentId(int id) {
        studentId = id;
    }

    public static int getStudentId() {
        return studentId;
    }

    public static void setStudentName(String name) {
        studentName = name;
    }

    public static String getStudentName() {
        return studentName;
    }

    public static void clearSession() {
        studentId = 0;
        studentName="";
    }
}
