package com.ipmessenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class clientConnect extends Thread {

    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream input   = null;
    private DataOutputStream out     = null;
    private String address = null;
    private int port = 0;
    private JTextArea taMsgSend;
    private JTextArea taHistory;
    private JButton sendButton;

    private long pid;


    // constructor to put ip address and port
    public clientConnect(String adr, int prt,JTextArea taMsgSend,JTextArea taHistory,JButton sendButton) {
        address = adr;
        port = prt;
        this.taMsgSend=taMsgSend;
        this.sendButton=sendButton;
        this.taHistory=taHistory;
    }

    public Socket getSocket() {
        return socket;
    }

    public long getPid() {
        return pid;
    }

    public void run() {// establish a connection
        try
        {
            pid = Thread.currentThread().getId();
            System.out.println("first time\n");
            socket = new Socket(address, port);
            System.out.println("Connected"+Thread.currentThread().getId());

            // takes input from terminal
            //input  = new DataInputStream(taMsgSend.);
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent)  {
                    String msg=taMsgSend.getText();
                    taMsgSend.setText("");
                    System.out.println("send b clicked");
                    taHistory.append("[You] :"+msg+"\n");
                    System.out.println("pid:"+Thread.currentThread().getId());
                    // sends output to the socket
                    try {

                        out    = new DataOutputStream(socket.getOutputStream());
                    }
                    catch(IOException e2)
                    {}
                    try {
                        out.writeUTF(msg);
                    }
                    catch (IOException e1)
                    {e1.printStackTrace();
                        Thread.currentThread().stop();
                    }
                }
            });





        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

//        // string to read message from input
//        String line = "";
//
//        // keep reading until "Over" is input
//        while (!line.equals("Over"))
//        {
//            try
//            {
//                line = input.readLine();
//                out.writeUTF(line);
//            }
//            catch(IOException i)
//            {
//                System.out.println(i);
//            }
//        }

        // close the connection
        try
        {
            //input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            // System.out.println(i);
        }
    }
}
