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
    static  int i=0;
    DefaultListModel<String> ips = new DefaultListModel<>();// counter for clients
    JList<String > listip;
    public DefaultListModel<String> getIps() {
        return ips;
    }

    // constructor with port
    public server(JTextArea taMsgRecv,DefaultListModel<String> ips,JList<String> listip)
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
                if(flag==0)
                    ips.addElement(s.getInetAddress().getHostAddress());
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
    private JTextArea taMsgRecv;
    Socket s;
    boolean isloggedin;
    boolean exit =false;
    DefaultListModel<String> ips = new DefaultListModel<>();
    JList<String>listip;
    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos,JTextArea taMsgRecv,DefaultListModel<String>ips,JList<String> listip) {
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
                received = dis.readUTF();
                String ack = "ackOK";
                dos.writeUTF(ack);

                System.out.println(received);
                if(!received.equals(""))
                {
                    taMsgRecv.append("["+s.getInetAddress().getHostAddress()+"] "+received+"\n");
                }

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
