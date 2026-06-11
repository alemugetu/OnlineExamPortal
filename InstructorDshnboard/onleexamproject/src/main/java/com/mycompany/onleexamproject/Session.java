/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onleexamproject;

/**
 *
 * @author hp
 */
public class Session {
  
    private static String userName;
    private static int userId;
    private static final String ipAddress = "10.246.151.19";
    
    public static String getIp(){
      return ipAddress;  
    }
    

    public static void setCurrentUser(int staff_id, String username) {
        userId = staff_id;
        userName = username;
    }

    public static int getCurrentUserID() {
        return userId;
    }

    public static String getCurrentUserName() {
        return userName;
    }

    public static void clearSession() {
        userId = 0;
        userName = null;
    }
}