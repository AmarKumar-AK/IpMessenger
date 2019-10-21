package com.ipmessenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class mainFrame {
    private JPanel panel1;
    private JTextArea taHistory;
    private JButton btnSend;
    private JTextField tfNewIp;
    private JButton btnSearchIp;
    private JList<String> listIp;
    private JSplitPane sp;
    private JPanel rpanel;
    private JPanel lpanel;
    private JLabel labelName;
    private JTextArea taSendMsg;
    Socket lastClient=null;
    DataOutputStream lastOut=null;
    int lc =1;


    public mainFrame()
    {
        server server = new server(taHistory);
        server.start();

        DefaultListModel<String> ips = new DefaultListModel<>();
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

                         lastClient.close();
                    }
                    catch (IOException i){

                    };
                    try{

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
                        taHistory.append(ips.get(i));
                        flag=1;
                        listIp.setSelectedIndex(i);
                        break;
                    }
                }
                if(flag==0)
                {
                    ips.addElement(temp);
                }
                tfNewIp.setText("");
                System.out.println(temp);
                listIp.setModel(ips);
                clientConnect client = new clientConnect(temp,5000,taSendMsg,taHistory,btnSend);
                client.start();
                try {
                    client.join();
                } catch (InterruptedException ee) {
                    ee.printStackTrace();
                }
                lastClient=client.getSocket();
                System.out.println("socket = "+lastClient);
                lastOut=client.getOut();
                System.out.println("out = "+lastOut);


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
