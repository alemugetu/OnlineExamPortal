/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.onlineexamserver;

/**
 *
 * @author hp
 */
import java.net.*;

public class Server {

    public static void main(String[] args) {

        try{

            ServerSocket server = new ServerSocket(2222);

            System.out.println("Server Running...");

            while(true){

                Socket socket = server.accept();

                System.out.println("Client Connected");

                new ClientHandler(socket).start();
            }

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}