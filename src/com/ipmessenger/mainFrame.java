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
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.*;

public class mainFrame {
    private JPanel panel1;      //area where we get message
    private JTextPane taHistory;    //button for sending message
    private JButton btnSend;    //area where we type ip to search
    private JTextField tfNewIp;    //button for searching ip
    private JButton btnSearchIp;    //area where list of ip is shown
    private JList<String> listIp;
    private JSplitPane sp;
    private JPanel rpanel;
    private JPanel lpanel;    //name of person with whom you are chatting
    private JLabel labelName;    //area where we type message
    private JTextArea taSendMsg;
    private JButton btnMediaButton;
    private JButton moreButton;
    Socket lastClient=null;
    DataOutputStream lastOut=null;
    int lc =1;
    int bc=1;
    private Socket currentClient = null;
    private clientConnect currClient ;

    DefaultListModel<String> ips = new DefaultListModel<>();


    public mainFrame() throws IOException {
        // getting own system ips
        compairingOwnIp compairingOwnIp = new compairingOwnIp();
        ArrayList<String> myips = compairingOwnIp.getMyips();


        //Reading iplist from file ip.txt
        Set<String> hash_set = new HashSet<String>();
        File ipsfile = new File("database/ip.txt");
        if(ipsfile.exists())
        {
            BufferedReader wbr = new BufferedReader(new FileReader(ipsfile));
            String st;
            while((st = wbr.readLine())!=null){
                hash_set.add(st);
            }
        }

        Iterator<String> j = hash_set.iterator();

        for(int i=0;i<myips.size();i++)
            System.out.println(myips.get(i));
        while(j.hasNext()){
            String ip =j.next();
            int flag =1;
            for(int i=0;i<myips.size();i++)
            {
                if(ip.equals(myips.get(i)))
                {
                    flag =0;
                    break;
                }
            }
            if(flag==1)
            {
                ips.addElement(ip);
            }


        }

        listIp.setModel(ips);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (UnsupportedLookAndFeelException e) {}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}

        taHistory.setAutoscrolls(true);
        StyledDocument doc = taHistory.getStyledDocument();

        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setBackground(left, Color.YELLOW);
        StyleConstants.setForeground(left, Color.RED);
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(left,24);
        StyleConstants.setSpaceAbove(left,20);

        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setBackground(right, Color.black);
        StyleConstants.setForeground(right, Color.white);
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(right,24);
        StyleConstants.setSpaceAbove(right,20);



        taHistory.setMargin(new Insets(10,10,10,10));

        server server = new server(taHistory,ips,listIp);
        server.start();

        taHistory.setAutoscrolls(true);
        listIp.setFixedCellHeight(40);
        listIp.setFixedCellWidth(10);
        listIp.setBorder(BorderFactory.createRaisedBevelBorder());
        listIp.setCellRenderer(getRenderer());
//        listIp.setForeground(Color.blue);
        ImageIcon icon = new ImageIcon("icon_2.png");



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
                String temp = tfNewIp.getText();
                int flag_ownip =1;
                for(int i=0;i<myips.size();i++)
                {
                    System.out.println("myip"+myips.get(i));
                    if(temp.equals(myips.get(i)))
                    {
                        flag_ownip =0;
                        break;
                    }

                }
                System.out.println("fo"+flag_ownip);
                if(!tfNewIp.getText().equals("") && flag_ownip==1)
                {
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
//                            System.out.println("firoz................");
//                        listIp.setSelectedIndex(1);
                            break;
                        }
                    }

                    tfNewIp.setText("");
                    System.out.println(temp);

                    clientConnect client = new clientConnect(temp,5000,taSendMsg,taHistory,btnSend,btnMediaButton,panel1,labelName);
                    try {
                        currentClient = client.getSocket();
                        currClient = client;
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
//                                System.out.println("::::r"+msg);
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
//                            System.out.println("amar"+i);
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
                else
                {
                    JOptionPane.showMessageDialog(panel1,"Please enter valid Ip !");
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
                    //listIp.setSelectionForeground(Color.green);
//                    listIp.set
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
//                                    System.out.println("::::r"+msg);
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
//                                System.out.println("amar"+i);
                                //////////////left
                                try
                                {
                                    msg =msg.trim();
                                    msg="\n"+msg;
//                                    System.out.println("::::l"+msg);
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

                    clientConnect client = new clientConnect(temp1,5000,taSendMsg,taHistory,btnSend,btnMediaButton,panel1,labelName);
                    try {
                        currentClient = client.getSocket();
                        client.start();
                        currClient = client;

                        System.out.println("client = "+currentClient);
//                        if(lastClient!=null)
//                        {
//                            labelName.setText(temp1);
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(panel1,"User is offline !");
//                        }
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
                Socket socket = currClient.getSocket();
                if(socket!=null)
                {
                    try{
//                        System.out.println("Last Client: "+lastClient);
//                        System.out.println("Current Client : " + currentClient);
                        JOptionPane.showMessageDialog(panel1, "IP: " + socket.getInetAddress().getHostAddress() + "\nPort: " + socket.getPort() + "\nLocal Port: " + socket.getLocalPort());
                    } catch(NullPointerException e) {
                    JOptionPane.showMessageDialog(panel1, "User is offline !");
                        System.out.println(e);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(panel1,"User is offline !");
                }


            }
        });
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                Color ipscolor = new Color(255, 255, 255);
                listCellRendererComponent.setBackground(ipscolor);
//                listCellRendererComponent.set
                ImageIcon icon = new ImageIcon("icon_2.png");
                listCellRendererComponent.setIcon(icon);
                //getListCellRendererComponent.
                return listCellRendererComponent;
            }
        };
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Ip Messenger");
        frame.setSize(1080,720);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2,dim.height/2-frame.getSize().height/2);
        frame.setContentPane(new mainFrame().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
