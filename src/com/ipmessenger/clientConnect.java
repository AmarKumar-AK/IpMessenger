package com.ipmessenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private DataInputStream In=null;

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
    public DataOutputStream getOut() {
        return out;
    }

    public long getPid() {
        return pid;
    }

    public void run() {// establish a connection
        try
        {
            pid = Thread.currentThread().getId();
            System.out.println("first time\n");
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 1000);
            System.out.println("CCCsocket = "+socket);
            System.out.println("Connected"+Thread.currentThread().getId());

            try {
                out    = new DataOutputStream(socket.getOutputStream());
                In = new DataInputStream(socket.getInputStream());

            }
            catch(IOException e2)
            {}

            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent)  {
                    String msg=taMsgSend.getText();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    if(!msg.equals(null))
                    {
                        taMsgSend.setText("");
                        System.out.println("send b clicked");


                        System.out.println("pid:"+Thread.currentThread().getId());
                        try {
                            out.writeUTF(msg);
                            String ack;
                            ack=In.readUTF();
                            if(ack.equals("ackOK"))
                                taHistory.append("[You @ "+dtf.format(now)+"]: "+msg+"\n");

                        }
                        catch (IOException e1)
                        {
                            e1.printStackTrace();
                            Thread.currentThread().stop();
                        }

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

        // close the connection
        //list click action
//        try
//        {
//            //input.close();
//            out.close();
//            socket.close();
//        }
//        catch(IOException i)
//        {
//            // System.out.println(i);
//        }
    }
}
