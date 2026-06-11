/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onlineexamserver;

/**
 *
 * @author hp
 *///package server;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection(){
        
        String url = "jdbc:mysql://localhost:3306/onlineexam?zeroDateTimeBehavior=CONVERT_TO_NULL";
        String user = "root";
        String pass = "";

        try{

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url,user,pass);

        }catch(Exception e){

            e.printStackTrace();

            return null;
        }
    }
}