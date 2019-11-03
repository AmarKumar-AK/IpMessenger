package com.ipmessenger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.desktop.SystemEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class mainFrame {
    private JPanel panel1;
    //area where we get message
    private JTextPane taHistory;
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
    private JButton btnMediaButton;
    private JButton moreButton;
    Socket lastClient=null;
    DataOutputStream lastOut=null;
    int lc =1;
    int bc=1;
    private Socket currentClient = null;

    DefaultListModel<String> ips = new DefaultListModel<>();

    public mainFrame()
    {


        StyledDocument doc = taHistory.getStyledDocument();

        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setBackground(left, Color.YELLOW);
        StyleConstants.setForeground(left, Color.RED);
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);



        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setBackground(right, Color.GRAY);
        StyleConstants.setForeground(right, Color.BLUE);
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);


        server server = new server(taHistory,ips,listIp);
        server.start();

        ip IpAddr=new ip();
        try
        {
            //IpAddr.start();
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                String chat="";
                char[] ch={0};
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
                String filename=temp;
                taHistory.setText("");
                try
                {
                    File file=new File("database/"+filename.replace(".","_")+".txt");
                    Scanner sc=new Scanner(file);
                    sc.useDelimiter("\\Z");
                    chat = sc.next();
                    System.out.println(chat);
//                    Scanner scanner = new Scanner(chat);
                    ch = chat.toCharArray();

                    System.out.println(chat.length());


//                    taHistory.setText();
//                        String msgsave=sc.next()+"\n";
//                        taHistory.setText(msgsave);


//                    taHistory.setText();
//                    taHistory.setText(sc.next());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
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
                clientConnect client = new clientConnect(temp,5000,taSendMsg,taHistory,btnSend,btnMediaButton,panel1);
                try {
                    currentClient = client.getSocket();
                    client.start();
                    System.out.println("client = "+currentClient);
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
                listIp.setSelectedIndex(ips.size()-1);
                lc=0;

                String msg = "";
                for(int i=0;i<ch.length-1;i++)
                {
//                        System.out.println("i");
                    char a=ch[i],b=ch[i+1];
                    if(a=='[' && b=='Y')
                    {
                        while(ch[i]!=']')
                        {
//                                System.out.println("j"+i);
                            i++;
                        }
                        i++;
                        msg = "";
                        while(ch[i]!='[')
                        {
//                                System.out.println("k"+i);
                            msg+=ch[i];
                            i++;
                            if(i==chat.length()){
                                break;
                            }
                        }

                        i--;
                        ////////////right
                        try
                        {
                            msg =msg.trim();
                            msg="\n"+msg;
                            System.out.println("::::r"+msg);
                            doc.insertString(doc.getLength(), msg, right );
                            doc.setParagraphAttributes(doc.getLength(), 1, right, false);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }

                    }
                    else if(a=='[' && b!='Y')
                    {
                        while(ch[i]!=']')
                        {
//                                System.out.println("l"+i);
                            i++;
                        }
                        i++;
                        msg = "";
                        while(ch[i]!='[')
                        {
//                                System.out.println("m"+i);
                            msg+=ch[i];
//                                System.out.println(msg);
                            i++;
                            if(i==chat.length()){
                                break;
                            }
                        }

                        i--;
                        System.out.println("amar"+i);
                        //////////////left
                        try
                        {
                            msg =msg.trim();
                            msg="\n"+msg;
                            System.out.println("::::l"+msg);
                            doc.insertString(doc.getLength(), msg, left );
                            doc.setParagraphAttributes(doc.getLength(), 1, left, false);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }

                    }
                }


            }
        });


        listIp.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String chat ="";
                char[] ch;
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
                    String filename=temp1;
                    taHistory.setText("");
                    try
                    {
                        File file=new File("database/"+filename.replace(".","_")+".txt");
                        Scanner sc=new Scanner(file);
                        sc.useDelimiter("\\Z");
                        chat = sc.next();
                        System.out.println(chat);
//                    Scanner scanner = new Scanner(chat);
                        ch = chat.toCharArray();
                        String msg = "";
                        System.out.println(chat.length());
                        for(int i=0;i<ch.length-1;i++)
                        {
//                        System.out.println("i");
                            char a=ch[i],b=ch[i+1];
                            if(a=='[' && b=='Y')
                            {
                                while(ch[i]!=']')
                                {
//                                System.out.println("j"+i);
                                    i++;
                                }
                                i++;
                                msg = "";
                                while(ch[i]!='[')
                                {
//                                System.out.println("k"+i);
                                    msg+=ch[i];
                                    i++;
                                    if(i==chat.length()){
                                        break;
                                    }
                                }

                                i--;
                                ////////////right
                                try
                                {
                                    msg =msg.trim();
                                    msg="\n"+msg;
                                    System.out.println("::::r"+msg);
                                    doc.insertString(doc.getLength(), msg, right );
                                    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
                                } catch (BadLocationException ex) {
                                    ex.printStackTrace();
                                }

                            }
                            else if(a=='[' && b!='Y')
                            {
                                while(ch[i]!=']')
                                {
//                                System.out.println("l"+i);
                                    i++;
                                }
                                i++;
                                msg = "";
                                while(ch[i]!='[')
                                {
//                                System.out.println("m"+i);
                                    msg+=ch[i];
//                                    System.out.println(msg);
                                    i++;
                                    if(i==chat.length()){
                                        break;
                                    }
                                }

                                i--;
                                System.out.println("amar"+i);
                                //////////////left
                                try
                                {
                                    msg =msg.trim();
                                    msg="\n"+msg;
                                    System.out.println("::::l"+msg);
                                    doc.insertString(doc.getLength(), msg, left );
                                    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
                                } catch (BadLocationException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
//                        String msgsave=sc.next()+"\n";
//                        taHistory.setText(msgsave);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("selected ip = "+temp1);
                    System.out.println(temp1);
                    //listIp.setModel(ips);

                    clientConnect client = new clientConnect(temp1,5000,taSendMsg,taHistory,btnSend,btnMediaButton,panel1);
                    try {
                        currentClient = client.getSocket();
                        client.start();

                        System.out.println("client = "+currentClient);
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
        moreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                String ip = currentClient.getInetAddress().getHostAddress();
                JOptionPane.showMessageDialog(panel1,"kaksdkkal");
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
