package com.ipmessenger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.desktop.SystemEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class mainFrame {
    private JPanel panel1;
    //area where we get message
    private JTextArea taHistory;
    //button for sending message
    private JButton btnSend;
    //area where we type ip to search
    private JTextField tfNewIp;
    //button for searching ip
    private JButton btnSearchIp;
    //area where list of ip is shown
    private JList<String> listIp;
    private JSplitPane sp;
    private JPanel rpanel;
    private JPanel lpanel;
    //name of person with whom you are chatting
    private JLabel labelName;
    //area where we type message
    private JTextArea taSendMsg;
    Socket lastClient=null;
    DataOutputStream lastOut=null;
    int lc =1;
    int bc=1;
    DefaultListModel<String> ips = new DefaultListModel<>();

    public mainFrame()
    {
        server server = new server(taHistory,ips,listIp);
        server.start();

        tfNewIp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    btnSearchIp.doClick();
                }
            }
        });

        btnSearchIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lc==0)
                {
                    try{
                        System.out.println("lastsocket = "+lastClient);
                        if(lastOut!=null)
                         lastClient.close();
                    }
                    catch (IOException i){
                        System.out.println(i);
                    };
                    try{
                        if(lastOut!=null)
                        lastOut.close();
                    }
                    catch (IOException i){

                    };

                    for( ActionListener al : btnSend.getActionListeners() ) {
                        btnSend.removeActionListener( al );
                    }

                }

                String temp = tfNewIp.getText();
                int flag = 0;
                for (int i=0;i<ips.size();i++)
                {
                    String a= ips.get(i);
                    if(a.equals(temp))
                    {
                        //taHistory.append(ips.get(i));
                        flag=1;
                        System.out.println("firoz................");
                        listIp.setSelectedIndex(1);
                        break;
                    }
                }

                tfNewIp.setText("");
                System.out.println(temp);
                listIp.setModel(ips);
                clientConnect client = new clientConnect(temp,5000,taSendMsg,taHistory,btnSend);
                try {
                    client.start();
                    if(lastClient!=null)
                        labelName.setText(temp);
                } catch (UnknownError eee) {
                    eee.printStackTrace();
                }
                try {
                    client.join();
                } catch (InterruptedException ee) {
                    ee.printStackTrace();
                }
                lastClient=client.getSocket();
                System.out.println("socket = "+lastClient);
                lastOut=client.getOut();
                System.out.println("out = "+lastOut);
                if(lastOut!=null && flag!=1)
                {
                    ips.addElement(temp);
                }
                lc=0;
            }
        });


        listIp.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting())
                {
                    if(bc==0)
                    {
                        try{
                            System.out.println("lastsocket = "+lastClient);
                            if(lastOut!=null)
                                lastClient.close();
                        }
                        catch (IOException i){
                            System.out.println(i);
                        };
                        try{
                            if(lastOut!=null)
                                lastOut.close();
                        }
                        catch (IOException i){

                        };
                    }
                    String temp1=listIp.getSelectedValue();
                    System.out.println("selected ip = "+temp1);
                    System.out.println(temp1);
                    //listIp.setModel(ips);

                    clientConnect client = new clientConnect(temp1,5000,taSendMsg,taHistory,btnSend);
                    try {
                        client.start();
                        if(lastClient!=null)
                            labelName.setText(temp1);
                    } catch (UnknownError eee) {
                        eee.printStackTrace();
                    }
                    try {
                        client.join();
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }
                    lastClient=client.getSocket();
                    System.out.println("socket = "+lastClient);
                    bc=0;
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ip Messenger");
        frame.setSize(800,600);
        frame.setContentPane(new mainFrame().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);




    }
}
