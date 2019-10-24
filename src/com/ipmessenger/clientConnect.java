package com.ipmessenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

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
    private JButton btnMediaButton;
    private JPanel panel1;
    private long pid;
    private JFileChooser fc=null;


    // constructor to put ip address and port
    public clientConnect(String adr, int prt,JTextArea taMsgSend,JTextArea taHistory,JButton sendButton, JButton btnMediaButton, JPanel panel1) {
        address = adr;
        port = prt;
        this.taMsgSend=taMsgSend;
        this.sendButton=sendButton;
        this.taHistory=taHistory;
        this.btnMediaButton=btnMediaButton;
        this.panel1 = panel1;
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
            }
            catch(IOException e2)
            {}

            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent)  {
                    System.out.println("Printing Action Event");
                    System.out.println(actionEvent);
                    String msg=taMsgSend.getText();
                    if(!msg.equals(null))
                    {
                        taMsgSend.setText("");
                        System.out.println("send b clicked");
                        taHistory.append("[You] :"+msg+"\n");
                        System.out.println("pid:"+Thread.currentThread().getId());
                        try {
                            out.writeUTF(msg);
                        }
                        catch (IOException e1)
                        {e1.printStackTrace();
                            Thread.currentThread().stop();
                        }
                    }
                    if(fc!=null)
                    {
                        try {
                            out.writeUTF("Attachment");
                            out.writeUTF(String.valueOf(fc.getSelectedFile()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FileInputStream fis = null;
                        BufferedInputStream bis = null;
                        OutputStream os = null;

                        try {
                            while (true) {
                                try {
                                    // send file
                                    File FILE_TO_SEND = fc.getSelectedFile();
                                    File myFile = new File (String.valueOf(FILE_TO_SEND));
                                    byte [] mybytearray  = new byte [(int)myFile.length()];
                                    fis = new FileInputStream(myFile);
                                    bis = new BufferedInputStream(fis);
                                    bis.read(mybytearray,0,mybytearray.length);
                                    os = socket.getOutputStream();
                                    System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                                    os.write(mybytearray,0,mybytearray.length);
                                    try {
                                        os.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("Done.");
                                    taHistory.append("[You] :"+String.valueOf(fc.getSelectedFile())+"sent.\n");
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (bis != null) {
                                        try {
                                            bis.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (os != null) {
                                        try {
                                            os.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        finally {
                        }
                    }

                }
            });

            btnMediaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    fc = new JFileChooser();
                    fc.showOpenDialog(panel1);
                    System.out.println("FC "+fc.getSelectedFile());
                    //SendMedia sm = new SendMedia(fc.getSelectedFile());
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
