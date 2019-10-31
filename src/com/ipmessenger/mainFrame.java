package com.ipmessenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    DefaultListModel<String> ips = new DefaultListModel<>();


    public mainFrame()
    {
        server server = new server(taHistory,ips,listIp);
        server.start();


//        ips.addElement("192.168.1.11");
//        ips.addElement("192.168.1.12");
//        ips.addElement("192.168.1.13");
//        ips.addElement("192.168.1.14");
//        ips.addElement("192.168.1.15");

        //String ips[] ={"192.168.1.11","192.168.1.13","192.168.1.12","192.168.1.14","192.168.1.15"};
        //listIp.setModel(ips);




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
                        listIp.setSelectedIndex(i);
                        break;
                    }
                }

                tfNewIp.setText("");
                System.out.println(temp);
                listIp.setModel(ips);
                clientConnect client = new clientConnect(temp,5000,taSendMsg,taHistory,btnSend);
                try {
                    client.start();
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
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ip Messenger");
        frame.setSize(800,600);
        frame.setContentPane(new mainFrame().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);




    }
}
