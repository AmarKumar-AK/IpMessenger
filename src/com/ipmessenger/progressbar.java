package com.ipmessenger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class progressbar extends Thread {
    private JProgressBar pb;
    private boolean stop;
    JFrame frame = new JFrame();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    public progressbar(String title) {
        frame.setLocation(dim.width/2-frame.getSize().width/2,dim.height/2-frame.getSize().height/2);
        frame.setTitle(title);
        pb = new JProgressBar();
        pb.setValue(0);
        JPanel p = new JPanel();
        pb.update(pb.getGraphics());
        //p.setBackground(Color.BLACK);
        p.add(pb);
        pb.setString(title);
        frame.add(p);
        stop=true;
        frame.setSize(400,100);
        frame.setVisible(true);
    }

    //    @Override
    public void run() {
        int i=0;
        //pb.setString("Sending...");
        while(stop){
            pb.update(pb.getGraphics());

            pb.setValue(i);
            //pb.repaint();

            i=(i+10)%100;
            try{Thread.sleep(80);}catch(Exception e){}
        }
        pb.setValue(100);
        pb.setString("Sent !");
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frame.dispose();
//        pb.update(pb.getGraphics());

    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}