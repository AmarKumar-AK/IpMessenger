package com.ipmessenger;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class server extends Thread {
    static Vector<ClientHandler> ar = new Vector<>();
    private JTextArea taMsgRecv;
    static  int i=0;                                 // counter for clients

    // constructor with port
    public server(JTextArea taMsgRecv)
    {
        this.taMsgRecv=taMsgRecv;
    }
    public void run()
    {
        // server is listening on port 5000
        try
        {
            ServerSocket ss = new ServerSocket(5000);

            Socket s = null;

            // running infinite loop for getting
            // client request
            while (true)
            {
                // Accept the incoming request
                System.out.println("server waiting! \n");
                s = ss.accept();


                System.out.println("New client request received : " + s);

                // obtain input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos,taMsgRecv);


                // Create a new Thread with this object.
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                ar.add(mtch);

                // start the thread.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced
                // by any naming scheme
                i++;

            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    private JTextArea taMsgRecv;
    Socket s = null;
    boolean isloggedin;
    boolean exit =false;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos,JTextArea taMsgRecv) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.taMsgRecv=taMsgRecv;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                System.out.println(received);
                taMsgRecv.append("[+] "+received+"\n");

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }
            } catch (IOException e) {
                try
                {
                    // closing resources
                    this.dis.close();
                    this.dos.close();

                }catch(IOException e1){
                    //e1.printStackTrace();
                }
                //e.printStackTrace();
            }

        }

    }
}
