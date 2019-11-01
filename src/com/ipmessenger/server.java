package com.ipmessenger;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class server extends Thread {
    static Vector<ClientHandler> ar = new Vector<>();
    private JTextPane taMsgRecv;
    static  int i=0;
    DefaultListModel<String> ips = new DefaultListModel<>();// counter for clients
    JList<String > listip;
    public DefaultListModel<String> getIps() {
        return ips;
    }

    // constructor with port
    public server(JTextPane taMsgRecv,DefaultListModel<String> ips,JList<String> listip)
    {
        this.taMsgRecv=taMsgRecv;
        this.ips = ips;
        this.listip = listip;
    }
    public void run()
    {
        // server is listening on port 5000
        try
        {
            ServerSocket ss = new ServerSocket(5000);

            Socket s;

            // running infinite loop for getting
            // client request
            while (true)
            {
                // Accept the incoming request
                System.out.println("server waiting! \n");
                s = ss.accept();

                int flag = 0;
                for (int i=0;i<ips.size();i++)
                {
                    String a= ips.get(i);
                    if(a.equals(s.getInetAddress().getHostAddress()))
                    {
                        //taHistory.append(ips.get(i));
                        flag=1;
                        break;
                    }
                }

                if(flag==0) {
                    ips.addElement(s.getInetAddress().getHostAddress());
                }

                for(int i=0;i<ips.size();i++)
                {
                    System.out.println(ips.get(i));
                }
                System.out.println("New client request received : " + s);

                // obtain input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos,taMsgRecv,ips,listip);


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
    private JTextPane taMsgRecv;
    Socket s;
    boolean isloggedin;
    boolean exit =false;
    DefaultListModel<String> ips = new DefaultListModel<>();
    JList<String>listip;
    public  static  String FILE_TO_RECEIVED=null;
    public final static int FILE_SIZE=1073741820;   //1GB
    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos,JTextPane taMsgRecv,DefaultListModel<String>ips,JList<String> listip) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.taMsgRecv=taMsgRecv;
        this.ips = ips;
        this.listip=listip;
    }

    @Override
    public void run() {
        listip.setModel(ips);
        for(int i=0;i<ips.size();i++)
        {
            if(ips.get(i).equals(s.getInetAddress().getHostAddress()))
            {

                break;
            }
        }
        String received;
        while (true)
        {
            try
            {
                // receive the string
                long recvfilesize=0;
                received = dis.readUTF();
                String ack = "ackOK";
                dos.writeUTF(ack);
                String filename=s.getInetAddress().getHostName();
                System.out.println("fn"+filename);

                if(received.equals("Attachment5psafv"))
                {
                    received=dis.readUTF();
                    String path="media/";
                    FILE_TO_RECEIVED=path+received.toLowerCase();
                    int bytesRead;
                    int current=0;
                    FileOutputStream fos=null;
                    BufferedOutputStream bos=null;
                    try
                    {
                        System.out.println("Connecting...........");
                        recvfilesize=dis.readLong();
                        System.out.println("recvFilesize: "+recvfilesize);

                        byte [] mybytearray=new byte[FILE_SIZE];
                        InputStream is=s.getInputStream();
                        fos=new FileOutputStream(FILE_TO_RECEIVED);
                        bos=new BufferedOutputStream(fos);
                        do {
                            bytesRead=is.read(mybytearray,current,(mybytearray.length-current));
                            System.out.println("bytesread : "+bytesRead);
                            System.out.println("current : "+current);
                            current+=bytesRead;
                        }while(current<recvfilesize);
                        System.out.println(received);
                        if(!received.equals(""))
                        {
                            taMsgRecv.setText(taMsgRecv.getText()+"["+s.getInetAddress().getHostAddress()+"] "+received+"\n");
                        }
                        bos.write(mybytearray,0,current);
                        bos.flush();
                        received=null;
                        System.out.println("file "+FILE_TO_RECEIVED+" downloaded ("+current+" bytes read)");
                    }
                    finally {
                        if(fos!=null)  fos.close();
                        if(bos!=null)  bos.close();
                    }
                }else {

                    if (received.equals("logout")) {
                        this.isloggedin = false;
                        this.s.close();
                        break;
                    }

                    System.out.println(received);
                    if (!received.equals("")) {
                        taMsgRecv.setText(taMsgRecv.getText() + "[" + s.getInetAddress().getHostAddress() + "] " + received + "\n");
                    }

                    File file = new File("database/" + filename.replace(".", "_") + ".txt");
                    file.setWritable(true, false);
                    FileWriter fr = new FileWriter(file, true);
                    BufferedWriter br = new BufferedWriter(fr);
                    br.write("[" + s.getInetAddress().getHostAddress() + "]" + received + "\n");
                    br.close();
                    fr.close();
                    file.setReadOnly();
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
